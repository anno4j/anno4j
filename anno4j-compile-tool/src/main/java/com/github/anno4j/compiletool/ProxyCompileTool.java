package com.github.anno4j.compiletool;

import com.github.anno4j.Anno4j;
import com.google.common.collect.Lists;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.openrdf.annotations.Iri;
import org.openrdf.idGenerator.IDGeneratorAnno4jURN;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.composition.ClassFactory;
import org.openrdf.repository.object.managers.RoleMapper;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.reflections.util.ClasspathHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ProxyCompileTool {

    /**
     * Returns the supported command line options of this tool.
     * @return The options of this tool.
     */
    private static Options getCLIOptions() {
        Options options = new Options();

        Option input = new Option("i", "input", true, "Path to the directory " +
                "where the resource objects and support classes source are stored.");
        input.setRequired(true);
        options.addOption(input);

        Option output = new Option("o", "output", true, "Path to the directory where the generated JAR files will be stored.");
        output.setRequired(true);
        options.addOption(output);

        Option threadNum = new Option("t", "threads", true, "Number of threads to use for building behaviour classes. Default: 1");
        threadNum.setType(Integer.class);
        options.addOption(threadNum);

        Option classpath = new Option("cp", "classpath", true, "Classpath for compiling sources. Separated by " + File.pathSeparator);
        classpath.setType(String.class);
        options.addOption(classpath);

        Option pattern = new Option("p", "pattern", true, "RegEx describing the files that will be compiled.");
        pattern.setType(String.class);
        options.addOption(pattern);

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
     * @param commandLine The parsed command line arguments.
     * @return Returns the input directory.
     */
    private static File getInputDirectory(CommandLine commandLine) {
        File input = new File(commandLine.getOptionValue("input"));
        if(!input.isDirectory() || !input.canRead()) {
            System.err.println(input + " must be a readable directory.");
            System.exit(1);
        }
        // Check input contains source files:
        Collection<File> siblings = FileUtils.listFiles(input, new String[]{".java"}, true);
        if(siblings.isEmpty()) {
            System.err.println(input + " does not contain sources.");
            System.exit(1);
        }
        return input;
    }

    /**
     * @param commandLine The parsed command line arguments.
     * @return Returns the output directory.
     */
    private static File getOutputDirectory(CommandLine commandLine) {
        File output = new File(commandLine.getOptionValue("output"));
        if(!(output.isDirectory() || output.mkdirs()) || !output.canWrite()) {
            System.err.println(output + " must be a writable directory.");
            System.exit(1);
        }
        return output;
    }

    /**
     * Returns the absolute path to the output directory with a trailing slash.
     * @param commandLine The parsed command line arguments.
     * @return The output path.
     */
    private static String getOutputPath(CommandLine commandLine) {
        String outputPath = getOutputDirectory(commandLine).getAbsolutePath();
        if(!outputPath.endsWith(File.separator)) {
            outputPath += File.separator;
        }
        return outputPath;
    }

    /**
     * @param commandLine The parsed command line arguments.
     * @return Returns the number of threads to use.
     */
    private static int getNumberOfThreads(CommandLine commandLine) {
        String threadNumStr = commandLine.getOptionValue("threads");
        int numThreads;
        if(threadNumStr != null) {
            numThreads = Integer.parseInt(threadNumStr);
            if(numThreads < 1) {
                System.err.println("Number of threads must be positive");
            } else if(numThreads > Runtime.getRuntime().availableProcessors()) {
                System.out.println("Number of threads greater than available processors.");
            }
        } else {
            numThreads = 1;
        }
        return numThreads;
    }

    /**
     * Parses the command line option {@code -cp} which specifies various dependency files separated by
     * {@link File#pathSeparator}.
     * @param commandLine The parsed command line arguments.
     * @return Returns the URLs of the elements of the users classpath.
     * @throws MalformedURLException Thrown if any of the specified files has a URL that does not satisfy the requirements.
     * @throws FileNotFoundException Thrown if any of the specified files is not found.
     */
    private static URL[] getCompileDependencies(CommandLine commandLine) throws MalformedURLException, FileNotFoundException {
        String[] splits = commandLine.getOptionValue("classpath", "").split(File.pathSeparator);
        URL[] dependencies = new URL[splits.length];
        for (int i = 0; i < splits.length; i++) {
            File file = new File(splits[i]);
            if(file.exists()) {
                dependencies[i] = file.toURI().toURL();
            } else {
                throw new FileNotFoundException("Dependency " + splits[i] + " not found!");
            }
        }
        return dependencies;
    }

    /**
     * Compiles the sources found in the input directory that match the regex pattern (-p option of command line)
     * and outputs a JAR file in the output directory.
     * @param commandLine The parsed command line arguments.
     * @param input The input directory.
     * @param output The output directory.
     * @return Returns the written JAR file containing compiled {@code class} files.
     * @throws IOException Thrown if an error occurs while writing the JAR file.
     * @throws JavaSourceCompiler.CompileException Thrown if an error occurs during compilation.
     */
    private static File compileSources(CommandLine commandLine, File input, File output) throws IOException, JavaSourceCompiler.CompileException {
        // Pattern:
        Pattern pattern = Pattern.compile(commandLine.getOptionValue("pattern", ".*\\.java$"));
        // Classpath for compiling:
        JavaSourceCompiler compiler = new JavaSourceCompiler();
        for (URL depencency : getCompileDependencies(commandLine)) {
            compiler.addDependency(depencency.getPath());
        }
        File ontologyJar = new File(getOutputPath(commandLine) + "ontology.jar");
        compiler.compileDirectory(input, ontologyJar, pattern);

        return ontologyJar;
    }

    /**
     * Creates an Anno4j instance that can use the concepts contained in the given JAR file.
     * @param ontologyJar A JAR file containing concepts.
     * @param classLoader The class loader to use.
     * @param persistSchemaAnnotations Whether schema annotations should be scanned.
     * @return Returns an Anno4j instance.
     * @throws Exception Thrown on error.
     */
    private static Anno4j getAnno4jWithClassLoader(File ontologyJar, ClassLoader classLoader, boolean persistSchemaAnnotations) throws Exception {
        Set<URL> clazzes = new HashSet<>();
        clazzes.addAll(ClasspathHelper.forManifest(ontologyJar.toURI().toURL()));
        Anno4j anno4j = new Anno4j(new SailRepository(new MemoryStore()), new IDGeneratorAnno4jURN(), null, persistSchemaAnnotations, clazzes);

        // Register concepts found in JAR:
        RoleMapper mapper = anno4j.getObjectRepository().getConnection().getObjectFactory().getResolver().getRoleMapper();
        for (final String className : getClassNamesInJar(ontologyJar)) {
            Class<?> clazz = classLoader.loadClass(className);
            if(clazz.getAnnotation(Iri.class) != null) {
                mapper.addConcept(clazz);
            }
        }

        return anno4j;
    }

    /**
     * Packs the proxies created by the Anno4j instance into a JAR file.
     * @param commandLine The parsed command line arguments.
     * @param anno4j The Anno4j instance.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     * @throws IOException Thrown if an error occurs while writing the JAR file.
     */
    private static void packProxies(CommandLine commandLine, Anno4j anno4j) throws RepositoryException, IOException {
        ClassFactory factory = anno4j.getObjectRepository().getConnection().getObjectFactory().getResolver().getClassFactory();
        JarPacker packer = new JarPacker(new File(getOutputPath(commandLine) + "proxies.jar"));
        packer.addFile(factory.getOutput());
        packer.close();
    }

    /**
     * Main entry point for this tool.
     * See {@link #getCLIOptions()} for supported arguments.
     * @param args The CLI arguments passed to the application.
     */
    public static void main(String[] args) {
        try {
            // Get CLI arguments:
            CommandLine commandLine = parseCommandLineArguments(args);
            // Input JAR file containing compiled classes:
            File input = getInputDirectory(commandLine);
            // Output JAR file containing implemented proxies:
            File output = getOutputDirectory(commandLine);
            // Number of threads:
            int numThreads = getNumberOfThreads(commandLine);

            // Compile sources:
            File ontologyJar = compileSources(commandLine, input, output);

            // Get the dependencies:
            URL[] compileDependencies = getCompileDependencies(commandLine);
            URL[] dependencyURLs = new URL[1 + compileDependencies.length];
            dependencyURLs[0] = ontologyJar.toURI().toURL();
            System.arraycopy(compileDependencies, 0, dependencyURLs, 1, compileDependencies.length);
            System.out.println("Using the following dependencies:");
            for(URL dependency : dependencyURLs) {
                System.out.println(dependency);
            }

            // Get a class loader that can read from JAR files:
            ClassLoader classLoader = new URLClassLoader(dependencyURLs, ClassLoader.getSystemClassLoader());

            // Create an Anno4j instance that can load from the input JAR:
            Anno4j anno4j = getAnno4jWithClassLoader(ontologyJar, classLoader, false);

            ProxyCompileWorker.ProgressCallback progressCallback = new ProxyCompileWorker.ProgressCallback(getClassNamesInJar(ontologyJar).size());

            // Run the implementation tasks in multiple threads:
            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            Collection<Callable<Object>> tasks = new ArrayList<>();
            for (final String className : getClassNamesInJar(ontologyJar)) {
                Runnable task = new ProxyCompileWorker(anno4j, classLoader, className, progressCallback);
                tasks.add(Executors.callable(task));
            }
            executor.invokeAll(tasks);

            // Pack cached proxy implementations into a JAR:
            packProxies(commandLine, anno4j);

            System.out.println("Packed behaviour implementations into JAR: " + output.getAbsolutePath());
            System.exit(0);

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
