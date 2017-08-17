package com.github.anno4j.schema_parsing.tool;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Pattern;

/**
 * Utility class for compiling source code and packing the generated {@code class} files into a JAR file.
 */
class JavaSourceCompiler {

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
     * Returns all files that are recursively contained in a directory and match a given pattern.
     * @param dir The directory to search in.
     * @param pattern The RegEx pattern that must be matched. If this parameter is null then all files are included, i.e.
     *                in this case the parameter is equivalent to {@code .*}
     * @param filesOnly If this is true only files are returned. Otherwise files and directories are returned.
     *                  In both cases the pattern must be matched.
     * @return Returns the files (optionally also directories) that are contained in {@code dir} and which absolute
     * path fulfill the given regex pattern. {@code dir} itself is not part of the returned collection.
     */
    private Collection<File> listFiles(File dir, Pattern pattern, boolean filesOnly) {
        Collection<File> files = new HashSet<>();

        File[] siblings = dir.listFiles();
        if(siblings != null) {
            for(File sibling : siblings) {
                boolean matches = pattern == null || pattern.matcher(sibling.getAbsolutePath()).matches();
                if(sibling.isDirectory()) {
                    // Recursively get all files with the extension in this directory:
                    files.addAll(listFiles(sibling, pattern, filesOnly));

                    if(!filesOnly && matches) {
                        files.add(sibling);
                    }

                } else if (matches){
                    files.add(sibling);
                }
            }
        }

        return files;
    }

    /**
     * Compiles the {@code .java} files contained in the given {@code sourceDirectory} and packs the generated
     * class files into a JAR file at {@code outputJar}.
     * @param sourceDirectory The directory containing Java source.
     * @param outputJar The location of the output JAR.
     * @param pattern RegEx which defines which files in the input directory are compiled. If this parameter is null all {@code .java} files
     *                in {@code sourceDirectory} are compiled, i.e. {@code .*\.java$}
     * @throws IOException Thrown if an error occurs while writing the JAR.
     * @throws CompileException Thrown if an error occurs while compiling sources.
     */
    public void compileDirectory(File sourceDirectory, File outputJar, Pattern pattern) throws IOException, CompileException {
        File workingDirectory = new File("classes" + System.identityHashCode(this) + System.currentTimeMillis());
        workingDirectory.mkdir();

        // Find all source files:
        if(pattern == null) {
            pattern = Pattern.compile(".*\\.java$");
        }
        Collection<File> sourceFiles = listFiles(sourceDirectory, pattern, true);

        // Compile them:
        compileJavaFiles(sourceFiles.toArray(new File[sourceFiles.size()]), workingDirectory);

        // Pack them:
        JarPacker packer = new JarPacker(outputJar);
        packer.addFile(workingDirectory);
        packer.close();

        // Remove working directory:
        FileUtils.deleteDirectory(workingDirectory);
    }
}
