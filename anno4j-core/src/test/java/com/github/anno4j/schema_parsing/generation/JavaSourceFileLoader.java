package com.github.anno4j.schema_parsing.generation;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * Compiles the <code>.java</code> files in a certain directory
 * and loads them afterwards.
 */
public class JavaSourceFileLoader {

    /**
     * Signalizes that an error occured during compilation of <code>.java</code> files.
     */
    public static class CompileException extends Exception {

        /**
         * {@inheritDoc}
         */
        public CompileException() {
        }

        /**
         * {@inheritDoc}
         */
        public CompileException(String message) {
            super(message);
        }
    }

    private Collection<String> userClassPath = new LinkedList<>();

    /**
     * Adds a dependency to the user classpath.
     * @param dependency The dependency to add.
     */
    public void addDependency(String dependency) {
        userClassPath.add(dependency);
    }

    /**
     * Returns the path to the Java Compiler <code>javac</code>.
     * @return The path to the Java Compiler.
     * @throws FileNotFoundException Thrown if the compiler could not be detected.
     */
    private String getJavaCompiler() throws FileNotFoundException {
        String osName = System.getProperty("os.name");

        try {
            String whereisOutput;
            // On Windows use the "where" command:
            if(osName != null && osName.toLowerCase().contains("win")) {
                whereisOutput = SystemCommand.runCommand("where javac");

                // On Unix and Unix-like systems use the whereis command:
            } else if(osName != null) {
                whereisOutput = SystemCommand.runCommand("whereis javac");

            } else {
                throw new FileNotFoundException("Unable to detect operating system");
            }

            if(!whereisOutput.isEmpty()) {
                // whereis may return multiple binaries. Split command output at non-escaped whitespaces:
                String[] tokens = whereisOutput.split("(?<!\\\\)\\s+");
                // List of binaries may be preceded by "javac: ". Skip it if its there:
                if(tokens.length >= 2 && tokens[0].equals("javac:")) {
                    return tokens[1];
                } else if(tokens.length >= 1) {
                    return tokens[0];
                } else {
                    throw new FileNotFoundException("javac could not be found.");
                }
            } else {
                throw new FileNotFoundException("javac could not be found.");
            }

        } catch (IOException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    /**
     * Compiles the source files listed in <code>sourceList</code> and stores the resulting
     * <code>.class</code> files (in their respective package directories) in the given output directory.
     * @param sourceList A file listing the <code>.java</code> files to compile each in one line.
     *                   The filenames must not contain whitspaces unless they are escaped.
     * @param outputDirectory The directory where the resulting files are stored.
     * @throws CompileException Thrown if an error occurs during compilation.
     * @throws FileNotFoundException Thrown if the source file list or the doutput directory does not exist.
     */
    private void compileJavaFiles(File sourceList, File outputDirectory) throws CompileException, FileNotFoundException {
        // Test that the file listing source files is present:
        if(!sourceList.exists()) {
            throw new FileNotFoundException("The source file list " + sourceList.getAbsolutePath() + " does not exist.");
        }
        // Try to create the output directory:
        if(!outputDirectory.isDirectory() && !outputDirectory.mkdirs()) {
            throw new FileNotFoundException("The destination directory " + outputDirectory.getAbsolutePath() + " can not be created");
        }

        // Get the path to the Java Compiler:
        String javacPath;
        try {
            javacPath = getJavaCompiler();
        } catch (IOException e) {
            throw new CompileException("Unable to find the Java compiler. Details: " + e.getMessage());
        }

        // Run the actual compilation:
        try {
            StringBuilder command = new StringBuilder(javacPath);
            if(!userClassPath.isEmpty()) {
                command.append(" -cp ");
                Iterator<String> dependencyIter = userClassPath.iterator();
                while (dependencyIter.hasNext()) {
                    command.append(dependencyIter.next());
                    if(dependencyIter.hasNext()) {
                        command.append(File.pathSeparator);
                    }
                }
            }
            command.append(" -g -d ")
                    .append(outputDirectory.getAbsolutePath())
                    .append(" @")
                    .append(sourceList.getAbsolutePath());

            SystemCommand.runCommand(command.toString());
        } catch (IOException e) {
            throw new CompileException(e.getMessage());
        }
    }

    /**
     * Compiles the given source files and stores the resulting
     * <code>.class</code> files (in their respective package directories) in the given output directory.
     * @param sourceFiles The <code>.java</code> files to compile.
     * @param outputDirectory The directory where to write resulting files.
     * @throws CompileException Thrown if an error occurs during compilation.
     * @throws FileNotFoundException Thrown if the source file list or the output directory does not exist.
     */
    private void compileJavaFiles(File[] sourceFiles, File outputDirectory) throws CompileException, FileNotFoundException {
        File sourceList = new File("sources" + System.currentTimeMillis() + ".list");
        try {
            if(sourceList.createNewFile()) {
                OutputStream outStream = new FileOutputStream(sourceList);
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(outStream));

                for (File file : sourceFiles) {
                    // Escape spaces and write the file as a line:
                    out.write(file.getAbsolutePath().replace(" ", "\\ "));
                    out.newLine();
                }

                out.flush();
                out.close();

            } else {
                throw new CompileException("No source list file could be created at " + sourceList.getAbsolutePath());
            }
        } catch (IOException e) {
            throw new CompileException(e.getMessage());
        }

        // Compile from the before created source list file:
        compileJavaFiles(sourceList, outputDirectory);


        if(!sourceList.delete()) {
            throw new CompileException("Can't delete list of source files: " + sourceList.getAbsolutePath());
        }
    }

    /**
     * Recursively lists all files with a certain extension that are contained in a directory.
     * @param dir The directory to recursively scan.
     * @param extension The extension to look for.
     * @return The files found.
     */
    private Collection<File> listFiles(File dir, String extension) {
        Collection<File> classFiles = new HashSet<>();

        File[] subFiles = dir.listFiles();
        if(subFiles != null) {
            for(File subFile : subFiles) {
                if(subFile.isDirectory()) {
                    // Recursively get all files with the extension in this directory:
                    classFiles.addAll(listFiles(subFile, extension));

                } else if(subFile.getName().endsWith(extension)) {
                    classFiles.add(subFile);
                }
            }
        }

        return classFiles;
    }

    /**
     * Loads all <code>.class</code> files that are contained in a certain directory.
     * The class files must be located in the subdirectories corresponding to their Java package.
     * @param dir The directory from where to load the class files.
     * @return The fully qualified names of the classes loaded.
     * @throws ClassNotFoundException Thrown if any class was not found or no classes can be loaded from
     * the given directory.
     */
    private Map<String, Class<?>> loadJavaClasses(File dir) throws ClassNotFoundException {
        Collection<File> files = listFiles(dir, ".class");

        String dirPath = dir.getAbsolutePath();
        int offset = dirPath.length();
        if(!dirPath.endsWith(File.separator)) {
            offset++;
        }
        Collection<String> clazzNames = new HashSet<>();
        for (File file : files) {
            if(file.getName().endsWith(".class")) {
                String clazzName = file.getAbsolutePath()
                        .substring(offset) // Extract name and package from subdirectories
                        .replace(".class", "") // Remove the .class extension from the file
                        .replace(File.separator, "."); // Replace / with .
                clazzNames.add(clazzName);
            }
        }

        Map<String, Class<?>> loadedClazzes = new HashMap<>();

        try {
            ClassLoader classLoader = new URLClassLoader(new URL[] {dir.toURI().toURL()});
            for (String clazzName : clazzNames) {
                Class<?> clazz = classLoader.loadClass(clazzName);
                loadedClazzes.put(clazzName, clazz);
            }

        } catch (MalformedURLException e) {
            throw new ClassNotFoundException(e.getMessage());
        }

        return loadedClazzes;
    }

    public Map<String, Class<?>> compileAndLoad(File sourceDirectory, File workingDirectory) throws FileNotFoundException, CompileException, ClassNotFoundException {
        // Find all source files:
        Collection<File> sourceFiles = listFiles(sourceDirectory, ".java");

        // Compile them:
        compileJavaFiles(sourceFiles.toArray(new File[sourceFiles.size()]), workingDirectory);

        // Load them:
        return loadJavaClasses(workingDirectory);
    }
}
