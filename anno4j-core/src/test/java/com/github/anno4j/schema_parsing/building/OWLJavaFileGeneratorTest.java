package com.github.anno4j.schema_parsing.building;

import com.github.anno4j.Anno4j;
import com.github.anno4j.schema.model.owl.OWLClazz;
import com.github.anno4j.schema_parsing.generation.JavaFileGenerator;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.object.ObjectConnection;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

import static org.junit.Assert.*;

/**
 * Test for {@link OWLJavaFileGenerator} in terms of OWL.
 * RDFS compliance is tested by {@link RDFSModelBuilderTest}.
 */
public class OWLJavaFileGeneratorTest {

    private OntologyModelBuilder modelBuilder;

    @Before
    public void setUp() throws Exception {
        // Build the OWL restaurant ontology:
        modelBuilder = new OWLJavaFileGenerator();
        ClassLoader classLoader = getClass().getClassLoader();
        URL ontUrl = classLoader.getResource("restaurant_owl.ttl");
        File ontologyFile = new File(ontUrl.getFile());
        modelBuilder.addRDF(new FileInputStream(ontologyFile), "http://example.de/ont#", "TURTLE");
        modelBuilder.build();
    }

    @Test
    public void testOWLEquivalence() throws Exception {
        ObjectConnection connection = modelBuilder.getConnection();

        OWLClazz drink = connection.findObject(OWLClazz.class, new URIImpl("http://example.de/ont#Drink"));
        OWLClazz beverage = connection.findObject(OWLClazz.class, new URIImpl("http://example.de/ont#Beverage"));

        assertTrue((drink != null && beverage == null) || (drink == null && beverage != null));
    }

    @Test
    public void testOWLGeneration() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        URL ontUrl = classLoader.getResource("restaurant_owl.ttl");
        File ontologyFile = new File(ontUrl.getFile());

        OntGenerationConfig config = new OntGenerationConfig();

        JavaFileGenerator generator = new OWLJavaFileGenerator();
        generator.addRDF(new FileInputStream(ontologyFile), "http://example.de/ont#", "TURTLE");

        File outputDir = new File("target/generationOutput");
        generator.generateJavaFiles(config, outputDir);
    }
}