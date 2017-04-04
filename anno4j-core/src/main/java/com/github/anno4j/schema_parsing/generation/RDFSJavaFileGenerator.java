package com.github.anno4j.schema_parsing.generation;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.RDF;
import com.github.anno4j.model.namespaces.RDFS;
import com.github.anno4j.model.namespaces.XSD;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.building.OntologyModelBuilder;
import com.github.anno4j.schema_parsing.building.RDFSModelBuilder;
import com.github.anno4j.schema_parsing.model.ExtendedRDFSClazz;
import com.squareup.javapoet.JavaFile;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Generates Java files of {@link com.github.anno4j.model.impl.ResourceObject}
 * and {@link com.github.anno4j.model.impl.ResourceObjectSupport} classes from a RDFS ontology.
 * These classes receive approriate annotations and methods for using them with Anno4j.
 */
public class RDFSJavaFileGenerator implements JavaFileGenerator {

    /**
     * The underlying ontology model builder.
     */
    private RDFSModelBuilder modelBuilder;

    /**
     * Intitializes the generator with a new Anno4j object using
     * an in-memory triplestore.
     *
     * @throws RepositoryException       If an error occurs on initializing the underlying Anno4j instance.
     * @throws RepositoryConfigException If an error occurs on initializing the underlying Anno4j instance.
     */
    public RDFSJavaFileGenerator() throws RepositoryException, RepositoryConfigException {
        modelBuilder = new RDFSModelBuilder();
    }

    /**
     * Intitializes the generator with a new Anno4j object using
     * an in-memory triplestore.
     *
     * @param anno4j An Anno4j instance to which ontology information will be persisted on
     *               a successful call to {@link #generateJavaFiles(OntGenerationConfig, File)}.
     */
    public RDFSJavaFileGenerator(Anno4j anno4j) {
        modelBuilder = new RDFSModelBuilder(anno4j);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addRDF(InputStream rdfInput, String base) {
        modelBuilder.addRDF(rdfInput, base);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addRDF(String url, String base) {
        modelBuilder.addRDF(url, base);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addRDF(InputStream rdfInput, String base, String format) {
        modelBuilder.addRDF(rdfInput, base, format);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addRDF(String url, String base, String format) {
        modelBuilder.addRDF(url, base, format);
    }

    /**
     * Checks if the resource is from a standard vocabulary, e.g. RDF or RDFS.
     *
     * @param resource The resource to check.
     * @return Whether the resource is from a special vocabulary.
     */
    private boolean isFromSpecialVocabulary(ResourceObject resource) {
        return resource.getResourceAsString().startsWith(RDF.NS)
                || resource.getResourceAsString().startsWith(RDFS.NS)
                || resource.getResourceAsString().startsWith(XSD.NS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void generateJavaFiles(OntGenerationConfig config, File outputDirectory) throws JavaFileGenerationException, IOException {
        // Check if the output directory is actually a directory:
        if (!outputDirectory.exists()) {
            // Try to create it:
            if (!outputDirectory.mkdirs()) {
                throw new JavaFileGenerationException("The output directory " + outputDirectory.getAbsolutePath() + " could not be created.");
            }
        } else if (outputDirectory.isFile()) {
            throw new JavaFileGenerationException(outputDirectory.getAbsolutePath() + " must be a directory.");
        }

        // Build the underlying RDFS model:
        try {
            modelBuilder.build();
        } catch (OntologyModelBuilder.RDFSModelBuildingException e) {
            throw new JavaFileGenerationException(e.getMessage());
        }

        // Check if the model is valid:
        if (!modelBuilder.validate().isValid()) {
            throw new InvalidOntologyException("The ontology information was found to be invalid.");
        }

        // Write the .java files:
        for (ExtendedRDFSClazz clazz : modelBuilder.getClazzes()) {
            // Don't output files for classes that are from RDF/RDFS/... vocab and not for literal types:
            if (!isFromSpecialVocabulary(clazz) && !clazz.isLiteral()) {

                String clazzPackage = clazz.getJavaPackageName();

                JavaFile resourceObjectFile = JavaFile.builder(clazzPackage, clazz.buildTypeSpec(config))
                        .build();

                JavaFile supportFile = JavaFile.builder(clazzPackage, clazz.buildSupportTypeSpec(config))
                        .build();

                resourceObjectFile.writeTo(outputDirectory);
                supportFile.writeTo(outputDirectory);
            }
        }
    }
}
