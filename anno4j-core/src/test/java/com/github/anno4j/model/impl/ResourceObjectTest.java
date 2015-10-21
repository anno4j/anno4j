package com.github.anno4j.model.impl;

import com.github.anno4j.example.TextAnnotationBody;
import com.github.anno4j.model.Annotation;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.repository.Repository;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;

/**
 * Created by schlegel on 05/10/15.
 */
public class ResourceObjectTest extends TestCase {

    Repository repository;
    ObjectConnection connection;

    @Before
    public void setUp() throws Exception {
        repository = new SailRepository(new MemoryStore());
        repository.initialize();

        ObjectRepositoryFactory factory = new ObjectRepositoryFactory();
        ObjectRepository objectRepository = factory.createRepository(repository);
        connection = objectRepository.getConnection();
    }

    @Test
    public void testGetNTriples() throws Exception {
        Annotation annotation = new Annotation();
        annotation.setAnnotatedAt("" + System.currentTimeMillis());
        annotation.setSerializedAt("" + System.currentTimeMillis());

        String output = annotation.getNTriples();

        assertTrue(output.contains("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/oa#Annotation>"));
        assertTrue(output.contains(" <http://www.w3.org/ns/oa#annotatedAt> "));
        assertTrue(output.contains(" <http://www.w3.org/ns/oa#serializedAt> "));
    }

    @Test
    public void testGetTriplesWithTurtle() {
        // Create arbitrary annotation with some provenance information
        Annotation annotation = new Annotation();
        long time = System.currentTimeMillis();
        annotation.setAnnotatedAt("" + time);

        // Create a (for test cases only) textual body
        TextAnnotationBody body = new TextAnnotationBody();
        String value = "someValue";
        String format = "someFormat";
        String language = "someLanguage";
        body.setValue(value);
        body.setFormat(format);
        body.setLanguage(language);

        // Add the body to the annotation
        annotation.setBody(body);

        String output = annotation.getTriples(RDFFormat.TURTLE);

        // Check annotation type
        assertTrue(output.contains("<" + annotation.getResourceAsString() + "> a <http://www.w3.org/ns/oa#Annotation>"));

        // Check provenance
        assertTrue(output.contains("<http://www.w3.org/ns/oa#annotatedAt> " + "\"" + time + "\""));

        // Check that the annotation has a body
        assertTrue(output.contains("<" + annotation.getResourceAsString() + "> <http://www.w3.org/ns/oa#hasBody> <" + body.getResourceAsString() + ">"));

        // Check body values
        assertTrue(output.contains("<" + body.getResourceAsString() + "> a <http://www.w3.org/ns/oa#EmbeddedContent>"));
        assertTrue(output.contains("<http://purl.org/dc/elements/1.1/format> \"" + format + "\""));
        assertTrue(output.contains("<http://purl.org/dc/elements/1.1/language> \"" + language + "\""));
        assertTrue(output.contains("<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> \"" + value + "\""));

        System.out.println(annotation.getTriples(RDFFormat.TURTLE));
    }

    @Test
    public void testGetTriplesWithJSONLD() {
        // Create arbitrary annotation with some provenance information
        Annotation annotation = new Annotation();
        long time = System.currentTimeMillis();
        annotation.setAnnotatedAt("" + time);

        // Create a (for test cases only) textual body
        TextAnnotationBody body = new TextAnnotationBody();
        String value = "someValue";
        String format = "someFormat";
        String language = "someLanguage";
        body.setValue(value);
        body.setFormat(format);
        body.setLanguage(language);

        // Add the body to the annotation
        annotation.setBody(body);

        String output = annotation.getTriples(RDFFormat.JSONLD);

        String jsonld = "[ {" +
            "\"@id\" : \"urn:anno4j:2db454f9-5d2f-4f92-9aba-e276bbca2752\"," +
            "\"@type\" : [ \"http://www.w3.org/ns/oa#EmbeddedContent\" ]," +
            "\"http://purl.org/dc/elements/1.1/format\" : [ {" +
            "\"@value\" : \"someFormat\"" +
            "} ]," +
            "\"http://purl.org/dc/elements/1.1/language\" : [ {" +
            "\"@value\" : \"someLanguage\"" +
            "} ]," +
            "\"http://www.w3.org/1999/02/22-rdf-syntax-ns#value\" : [ {" +
            "\"@value\" : \"someValue\"" +
            "} ]" +
            "}, {" +
            "\"@id\" : \"urn:anno4j:e4282b36-af0d-4523-8853-d4af4549f51a\"," +
            "\"@type\" : [ \"http://www.w3.org/ns/oa#Annotation\" ]," +
        "\"http://www.w3.org/ns/oa#annotatedAt\" : [ {" +
                "\"@value\" : \"1445431914729\"" +
            "} ]," +
            "\"http://www.w3.org/ns/oa#hasBody\" : [ {" +
        "\"@id\" : \"urn:anno4j:2db454f9-5d2f-4f92-9aba-e276bbca2752\"" +
            "} ]" +
        "} ]";

//        assertEquals(jsonld, output);
    }
}