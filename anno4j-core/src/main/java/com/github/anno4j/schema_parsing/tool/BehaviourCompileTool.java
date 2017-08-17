package com.github.anno4j.schema_parsing.tool;

import com.github.anno4j.Anno4j;
import com.google.common.collect.Lists;
import org.apache.commons.cli.*;
import org.openrdf.idGenerator.IDGeneratorAnno4jURN;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.reflections.util.ClasspathHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class BehaviourCompileTool {

    /**
     * Returns the supported command line options of this tool.
     * @return The options of this tool.
     */
    private static Options getCLIOptions() {
        Options options = new Options();

        Option input = new Option("i", "input", true, "Path to the directory " +
                "where the compiled resource objects and support classes are stored.");
        input.setRequired(true);
        options.addOption(input);

        Option output = new Option("o", "output", true, "Path of the JAR file to create.");
        output.setRequired(true);
        options.addOption(output);

        Option threadNum = new Option("t", "threads", true, "Number of threads to use for building behaviour classes. Default: 1");
        threadNum.setType(Integer.class);
        options.addOption(threadNum);

        return options;
    }

    /**
     * Parses the given command line arguments for the options supported by this tool (see {@link #getCLIOptions()}).
     * Prints help text and terminates application if parsing fails.
     * @param args The CLI arguments as injected into {@link #main(String[])}.
     * @return The parsed command line options.
     */
    private static CommandLine parseCommandLineArguments(String[] args) {
        CommandLine commandLine;

        // Parse arguments and print help if invalid:
        Options options = getCLIOptions();
        CommandLineParser parser = new PosixParser();
        HelpFormatter helpFormatter = new HelpFormatter();
        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            helpFormatter.printHelp("behaviour-compile-tool", options);
            System.exit(1);
            return null;
        }
        return commandLine;
    }

    /**
     * Returns all files (not directories) that are recursively contained in the given root
     * directory and that optionally have the given file extension.
     * @param root The directory from which to recursively scan.
     * @param extension If this parameter is not null then only files with this file extension will be returned.
     *                  Otherwise all files are returned.
     * @return If {@code root} is a directory then all recursively contained files matching the given file extension
     * (if {@code extension} is null this test is omitted) are returned. If {@code root} is a file then
     * it a collection with only this file is returned if it matches the extension (if specified).
     */
    private static Collection<File> listFilesRecursive(File root, String extension) {
        if(root.isDirectory()) {
            List<File> files = new LinkedList<>();

            File[] siblings = root.listFiles();
            if(siblings != null) {
                for (File sibling : siblings) {
                    if(extension == null || sibling.getName().endsWith(extension)) {
                        files.add(sibling);
                    }
                    files.addAll(listFilesRecursive(sibling, extension));
                }
            }

            return files;

        } else { // Not a directory
            if(extension == null || root.getName().endsWith(extension)) {
                return Lists.newArrayList(root);
            } else {
                return Lists.newArrayList();
            }
        }
    }

    /**
     * Returns all recursively contained files (no directories) of root.
     * This method is equivalent to {@link #listFilesRecursive(File, String)} without specifying an
     * extension.
     * @param root The path to scan.
     * @return Returns all files contained in {@code root} if {@code root} is a directory.
     * Returns a list only containing {@code root} if the latter isn't a directory.
     */
    private static Collection<File> listFilesRecursive(File root) {
        return listFilesRecursive(root, null);
    }

    /**
     * Collects the names in the form {@code com.example.MyClass} of all classes contained in the given JAR file.
     * @param jarFile The JAR file from which to read.
     * @return Returns all fully qualified names of those classes contained in the JAR.
     * @throws IOException Thrown if an error occurs while reading from the JAR.
     */
    private static Collection<String> getClassNamesInJar(File jarFile) throws IOException {
        Collection<String> classNames = new HashSet<>();

        ZipInputStream zip = new ZipInputStream(new FileInputStream(jarFile));
        for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {

            if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                // Get the class name of the .class file:
                String className = entry.getName().replace('/', '.');
                classNames.add(className.substring(0, className.length() - ".class".length()));
            }
        }

        return classNames;
    }

    /**
     * Main entry point for this tool.
     * See {@link #getCLIOptions()} for supported arguments.
     * @param args The CLI arguments passed to the application.
     */
    public static void main(String[] args) {
        try {
            CommandLine commandLine = parseCommandLineArguments(args);

            File input = new File(commandLine.getOptionValue("input"));
            if(input.isDirectory() || !input.getName().toLowerCase().endsWith(".jar")) {
                System.err.println(input + " must be a JAR file.");
            }

            // TODO compile code here?

            int numThreads = Integer.parseInt(commandLine.getOptionValue("threads"));
            if(numThreads < 1) {
                System.err.println("Number of threads must be positive");
            } else if(numThreads > Runtime.getRuntime().availableProcessors()) {
                System.out.println("Number of threads greater than available processors.");
            }

            Set<URL> clazzes = new HashSet<>();
            clazzes.addAll(ClasspathHelper.forManifest(input.toURI().toURL()));
            Anno4j anno4j = new Anno4j(new SailRepository(new MemoryStore()), new IDGeneratorAnno4jURN(), null, false, clazzes);
            BehaviourCompileWorker.ProgressCallback progressCallback = new BehaviourCompileWorker.ProgressCallback();

            // Get a class loader that can read from JAR file:
            ClassLoader classLoader = new URLClassLoader(new URL[]{input.toURI().toURL()}, ClassLoader.getSystemClassLoader());

            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            for (final String className : getClassNamesInJar(input)) {
                executor.submit(new BehaviourCompileWorker(anno4j, classLoader, className, progressCallback));
            }
            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
