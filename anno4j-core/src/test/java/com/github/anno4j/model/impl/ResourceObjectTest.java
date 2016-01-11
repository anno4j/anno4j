package com.github.anno4j.model.impl;

import com.github.anno4j.Anno4j;
import com.github.anno4j.example.TextAnnotationBody;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.impl.agent.Person;
import com.github.anno4j.model.impl.agent.Software;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.idGenerator.IDGenerator;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.rio.RDFFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by schlegel on 05/10/15.
 */
public class ResourceObjectTest {

    private Anno4j anno4j;
    private ObjectConnection connection;

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
        this.connection = this.anno4j.getObjectRepository().getConnection();
        this.connection.setAutoCommit(false);
    }

    @After
    public void tearDown() throws Exception {
        connection.close();
    }

    @Test
    public void testSetResourceAsString() throws Exception {
        ResourceObject resourceObject = anno4j.createObject(ResourceObject.class);
        resourceObject.setResourceAsString("http://www.somepage.org/resource1/");
        connection.addObject(resourceObject);

        ResourceObject resourceObject1 = (ResourceObject) connection.getObject(resourceObject.getResource());
        assertEquals("http://www.somepage.org/resource1/", resourceObject1.getResourceAsString());
    }

    @Test
    public void testAutomaticResourceNaming() throws RepositoryException, InstantiationException, IllegalAccessException {
        ResourceObject resourceObject = anno4j.createObject(ResourceObject.class);
        connection.addObject(resourceObject);
        assertNotEquals(IDGenerator.BLANK_RESOURCE, resourceObject.getResource());

        ResourceObject resourceResult = (ResourceObject) connection.getObject(resourceObject.getResource());
        assertEquals(resourceObject.getResourceAsString(), resourceResult.getResourceAsString());
    }

    @Test
    public void testGetNTriples() throws Exception {
        Annotation annotation = anno4j.createObject(Annotation.class);
        annotation.setAnnotatedAt("" + System.currentTimeMillis());
        annotation.setSerializedAt("" + System.currentTimeMillis());

        this.connection.addObject(annotation);

        Annotation an = (Annotation) this.connection.getObject(annotation.getResource());

        String output = an.getTriples(RDFFormat.NTRIPLES);
        System.out.println("output" + output);
        assertTrue(output.contains("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/oa#Annotation>"));
        assertTrue(output.contains(" <http://www.w3.org/ns/oa#annotatedAt> "));
        assertTrue(output.contains(" <http://www.w3.org/ns/oa#serializedAt> "));
    }

    @Test
    public void testGetTriplesWithTurtle() throws RepositoryException, IllegalAccessException, InstantiationException {
        // Create arbitrary annotation with some provenance information
        Annotation annotation = anno4j.createObject(Annotation.class);
        long time = System.currentTimeMillis();
        annotation.setAnnotatedAt("" + time);

        // Create a (for test cases only) textual body
        TextAnnotationBody body = anno4j.createObject(TextAnnotationBody.class);
        String value = "someValue";
        String format = "someFormat";
        String language = "someLanguage";
        body.setValue(value);
        body.setFormat(format);
        body.setLanguage(language);

        // Add the body to the annotation
        annotation.setBody(body);

        this.connection.addObject(annotation);

        Annotation an = (Annotation) this.connection.getObject(annotation.getResource());

        String output = an.getTriples(RDFFormat.TURTLE);

        // Check for the specific annotation id
        assertTrue(output.contains("<" + an.getResourceAsString() + ">"));

        // Check annotation type
        assertTrue(output.contains("a <http://www.w3.org/ns/oa#Annotation>"));

        // Check provenance
        assertTrue(output.contains("<http://www.w3.org/ns/oa#annotatedAt> " + "\"" + time + "\""));

        // Check that the annotation has a body
        assertTrue(output.contains("<http://www.w3.org/ns/oa#hasBody> <" + body.getResourceAsString() + ">"));

        // Check body values
        assertTrue(output.contains("<" + body.getResourceAsString() + "> "));
        assertTrue(output.contains("a <http://www.w3.org/ns/oa#EmbeddedContent>"));
        assertTrue(output.contains("<http://purl.org/dc/elements/1.1/format> \"" + format + "\""));
        assertTrue(output.contains("<http://purl.org/dc/elements/1.1/language> \"" + language + "\""));
        assertTrue(output.contains("<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> \"" + value + "\""));
    }

    @Test
    public void testGetTriplesWithJSONLD() throws RepositoryException, IllegalAccessException, InstantiationException {
        // Create arbitrary annotation with some provenance information
        Annotation annotation = anno4j.createObject(Annotation.class);
        long time = System.currentTimeMillis();
        annotation.setAnnotatedAt("" + time);

        // Create a (for test cases only) textual body
        TextAnnotationBody body = anno4j.createObject(TextAnnotationBody.class);
        String value = "someValue";
        String format = "someFormat";
        String language = "someLanguage";
        body.setValue(value);
        body.setFormat(format);
        body.setLanguage(language);

        // Add the body to the annotation
        annotation.setBody(body);

        this.connection.addObject(annotation);

        Annotation an = (Annotation) this.connection.getObject(annotation.getResource());

        String output = an.getTriples(RDFFormat.JSONLD);

        // Create Strings that need to be contained in the JSONLD output (at some place)
        String jsonldBody = "  \"@id\" : \"" + body.getResourceAsString() + "\",\n" +
                "  \"@type\" : [ \"http://www.w3.org/ns/oa#EmbeddedContent\" ],\n" +
                "  \"http://purl.org/dc/elements/1.1/format\" : [ {\n" +
                "    \"@value\" : \"" + body.getFormat() + "\"\n" +
                "  } ],\n" +
                "  \"http://purl.org/dc/elements/1.1/language\" : [ {\n" +
                "    \"@value\" : \"" + body.getLanguage() + "\"\n" +
                "  } ],\n" +
                "  \"http://www.w3.org/1999/02/22-rdf-syntax-ns#value\" : [ {\n" +
                "    \"@value\" : \"" + body.getValue() + "\"";

        String jsondldAnnotation = "  \"@id\" : \"" + an.getResourceAsString() + "\",\n" +
                "  \"@type\" : [ \"http://www.w3.org/ns/oa#Annotation\" ],\n" +
                "  \"http://www.w3.org/ns/oa#annotatedAt\" : [ {\n" +
                "    \"@value\" : \"" + an.getAnnotatedAt() + "\"\n" +
                "  } ],\n" +
                "  \"http://www.w3.org/ns/oa#hasBody\" : [ {\n" +
                "    \"@id\" : \"" + body.getResourceAsString() + "\"";

        // Test if the crucial annotation information is present
        assertTrue(output.contains(jsondldAnnotation));

        // Test if the crucial body information is present
        assertTrue(output.contains(jsonldBody));
    }

    @Test
    public void testGetTriplesOnAgent() throws RepositoryException, IllegalAccessException, InstantiationException {
        Annotation annotation = anno4j.createObject(Annotation.class);
        long time = System.currentTimeMillis();
        annotation.setAnnotatedAt("" + time);

        Software softwareAgent = anno4j.createObject(Software.class);
        softwareAgent.setHomepage("www.example.org");
        softwareAgent.setName("SoftwareAgentName");

        Person personAgent = anno4j.createObject(Person.class);
        personAgent.setName("PersonAgentName");
        personAgent.setNick("PersonNick");

        annotation.setAnnotatedBy(softwareAgent);
        annotation.setSerializedBy(personAgent);

        this.connection.addObject(annotation);

        Annotation an = (Annotation) this.connection.getObject(annotation.getResource());

        String output = an.getTriples(RDFFormat.JSONLD);

        String jsonldPerson = " \"@type\" : [ \"https://github.com/anno4j/ns#Agent\", \"http://xmlns.com/foaf/0.1/Person\" ],\n" +
                "  \"http://xmlns.com/foaf/0.1/name\" : [ {\n" +
                "    \"@value\" : \"PersonAgentName\"\n" +
                "  } ],\n" +
                "  \"http://xmlns.com/foaf/0.1/nick\" : [ {\n" +
                "    \"@value\" : \"PersonNick\"\n" +
                "  } ]";

        assertTrue(output.contains(jsonldPerson));

        String jsondldSoftware = " \"@type\" : [ \"https://github.com/anno4j/ns#Agent\", \"http://www.w3.org/ns/prov/SoftwareAgent\" ],\n" +
                "  \"http://xmlns.com/foaf/0.1/homepage\" : [ {\n" +
                "    \"@value\" : \"www.example.org\"\n" +
                "  } ],\n" +
                "  \"http://xmlns.com/foaf/0.1/name\" : [ {\n" +
                "    \"@value\" : \"SoftwareAgentName\"\n" +
                "  } ]";

        assertTrue(output.contains(jsondldSoftware));
    }
}