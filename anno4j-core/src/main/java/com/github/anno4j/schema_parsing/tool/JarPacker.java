package com.github.anno4j.schema_parsing.tool;

import java.io.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * Utility class for packing multiple {@code .class} files into a
 * JAR file that can be used as a Java library.
 */
class JarPacker {

    /**
     * The stream used for writing to the JAR created.
     */
    private JarOutputStream jarStream;

    /**
     * Creates a JAR packer that packs {@code .class} files into a JAR file at the given location.
     * Use {@link #addFile(File)} to add files to the JAR and finally {@link #close()} to finalize
     * the JAR.
     * @param file The file descriptor of the JAR file to create.
     * @throws IOException Thrown if an error occurs while writing to the JAR file.
     */
    public JarPacker(File file) throws IOException {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        jarStream = new JarOutputStream(new FileOutputStream(file), manifest);
    }

    public void addFile(File file) throws IOException {
        String basePath = file.getPath();
        if (!basePath.endsWith(File.separator)) {
            basePath += File.separator;
        }

        addFile(file, basePath);
    }

    /**
     * Adds a file or directory to the JAR file.
     * @param file The file or directory to add.
     * @throws IOException Thrown if an error occurs while writing to the JAR file.
     */
    private void addFile(File file, String basePath) throws IOException {
        if (file.isDirectory())
        {
            String name = file.getPath().substring(Math.min(file.getPath().length(), basePath.length()))
                                .replace("\\", "/");
            if (!name.isEmpty())
            {
                if (!name.endsWith("/"))
                    name += "/";
                JarEntry entry = new JarEntry(name);
                entry.setTime(file.lastModified());
                jarStream.putNextEntry(entry);
                jarStream.closeEntry();
            }

            File[] containedFiles = file.listFiles();
            if(containedFiles != null) {
                for (File nestedFile : containedFiles) {
                    addFile(nestedFile, basePath);
                }
            }
            return;
        }

        JarEntry entry = new JarEntry(file.getPath().substring(Math.min(file.getPath().length(), basePath.length()))
                .replace("\\", "/"));
        entry.setTime(file.lastModified());
        jarStream.putNextEntry(entry);
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));

        byte[] buffer = new byte[1024];
        while (true)
        {
            int count = in.read(buffer);
            if (count == -1)
                break;
            jarStream.write(buffer, 0, count);
        }
        jarStream.closeEntry();
    }

    /**
     * Finalizes the packing process and thus the written JAR file.
     * @throws IOException Thrown if an error occurs while finalizing the JAR.
     */
    public void close() throws IOException {
        jarStream.flush();
        jarStream.close();
    }
}
