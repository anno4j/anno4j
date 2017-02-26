package com.github.anno4j.rdfs_parser.building;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.namespaces.RDF;
import com.github.anno4j.model.namespaces.RDFS;
import com.github.anno4j.model.namespaces.XSD;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSClazz;
import com.squareup.javapoet.JavaFile;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;

import java.io.*;

/**
 * Generates Anno4j Java classes for an RDFS ontology.
 */
public class JavaFileGenerator {

    public class JavaFileGenerationException extends Exception {
        public JavaFileGenerationException() {
        }

        public JavaFileGenerationException(String message) {
            super(message);
        }
    }

    private RDFSModelBuilder modelBuilder;

    public JavaFileGenerator() throws RepositoryConfigException, RepositoryException {
        modelBuilder = new RDFSModelBuilder(new Anno4j());
    }

    public void addRDF(String fileName) {
        modelBuilder.addRDF(fileName);
    }

    public void addRDF(InputStream rdfData, String baseUri) {
        modelBuilder.addRDF(rdfData, baseUri);
    }

    private File createOutputDirectory(OntGenerationConfig config) throws JavaFileGenerationException {
        File outputDirectory = config.getOutputDirectory();
        String[] outputDirectoryContent = outputDirectory.list();

        if (!outputDirectory.exists()) {
            if (!outputDirectory.mkdirs()) {
                throw new JavaFileGenerationException("Could not create directory " + outputDirectory.getAbsolutePath());
            }
        } else if (outputDirectory.isDirectory() && outputDirectoryContent != null && outputDirectoryContent.length != 0) {
            throw new JavaFileGenerationException("The output directory " + outputDirectory.getAbsolutePath() + " is not empty.");
        }
        return outputDirectory;
    }

    public void generate(OntGenerationConfig config) throws JavaFileGenerationException, RDFSModelBuilder.RDFSModelBuildingException, IOException {
        File outputDirectory = createOutputDirectory(config);

        modelBuilder.build();

        if (!modelBuilder.validate().isValid()) {
            throw new JavaFileGenerationException("The built model is invalid.");
        }

        for (ExtendedRDFSClazz clazz : modelBuilder.getRDFSClazzes()) {
            // Do not generate files for RDF(S) classes and XSD datatypes:
            if (!clazz.getResourceAsString().startsWith(RDF.NS) && !clazz.getResourceAsString().startsWith(RDFS.NS)
                    && !clazz.getResourceAsString().startsWith(XSD.NS)) {
                String packageName = clazz.getJavaPackageName();

                JavaFile interfaceFile = JavaFile.builder(packageName, clazz.buildTypeSpec(config)).build();
                JavaFile supportFile = JavaFile.builder(packageName, clazz.buildSupportTypeSpec(config)).build();

                interfaceFile.writeTo(outputDirectory);
                supportFile.writeTo(outputDirectory);
            }
        }
    }
}
