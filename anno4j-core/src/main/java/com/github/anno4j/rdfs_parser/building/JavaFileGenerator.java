package com.github.anno4j.rdfs_parser.building;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.namespaces.RDF;
import com.github.anno4j.model.namespaces.RDFS;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSClazz;
import com.github.anno4j.rdfs_parser.naming.IdentifierBuilder;
import com.squareup.javapoet.JavaFile;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;

import java.io.*;
import java.net.URISyntaxException;

/**
 *
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

    private File getPackageDirectory(File outputDir, String packageName) throws JavaFileGenerationException {
        String path = outputDir.getAbsolutePath() + File.separator + packageName.replace(".", File.separator);
        File packageDirectory = new File(path);
        if(packageDirectory.isDirectory() || packageDirectory.mkdirs()) {
            return packageDirectory;
        } else {
            throw new JavaFileGenerationException("Could not create directory for package " + packageName);
        }
    }

    public void generate(OntGenerationConfig config) throws JavaFileGenerationException, RDFSModelBuilder.RDFSModelBuildingException {
        File outputDirectory = config.getOutputDirectory();
        if(!outputDirectory.exists()) {
            if(!outputDirectory.mkdirs()) {
                throw new JavaFileGenerationException("Could not create directory " + outputDirectory.getAbsolutePath());
            }
        } else if(outputDirectory.isDirectory() && outputDirectory.list().length != 0) {
            throw new JavaFileGenerationException("The output directory " + outputDirectory.getAbsolutePath() + " is not empty.");
        }

        modelBuilder.build();

        if(!modelBuilder.validate().isValid()) {
            throw new JavaFileGenerationException("The built model is invalid.");
        }

        for (ExtendedRDFSClazz clazz : modelBuilder.getRDFSClazzes()) {
            // Do not generate files for RDF(S) classes:
            if(!clazz.getResourceAsString().startsWith(RDF.NS) && !clazz.getResourceAsString().startsWith(RDFS.NS)) {
                try {
                    String packageName = clazz.getJavaPackageName();

                    JavaFile javaFile = JavaFile.builder(packageName, clazz.buildTypeSpec(config)).build();
                    javaFile.writeTo(outputDirectory);

                } catch ( URISyntaxException | IOException e) {
                    throw new JavaFileGenerationException("Failed generating a name for " + clazz.getResourceAsString()
                            + " Details: " + e.getMessage());
                }
            }
        }
        System.out.println("Files written to " + outputDirectory.getAbsolutePath());
    }

    public static void main(String[] args) throws RepositoryException, RepositoryConfigException, FileNotFoundException {
        String[] langPref = {"de", "en"};
        OntGenerationConfig config = new OntGenerationConfig();
        config.setJavaDocLanguagePreference(langPref);
        config.setIdentifierLanguagePreference(langPref);
        config.setOutputDirectory(new File("/Users/fischmat/Desktop/gen"));

        JavaFileGenerator generator = new JavaFileGenerator();
        generator.addRDF(new FileInputStream("/Users/fischmat/Desktop/testont.xml"), "http://example.de/");

        try {
            generator.generate(config);
        } catch (JavaFileGenerationException | RDFSModelBuilder.RDFSModelBuildingException e) {
            e.printStackTrace();
        }
    }
}
