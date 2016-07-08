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

public class ResourceObjectTest {

    private Anno4j anno4j;

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
    }

    @Test
    public void testSetResourceAsString() throws Exception {
        ResourceObject resourceObject = anno4j.createObject(ResourceObject.class);
        resourceObject.setResourceAsString("http://www.somepage.org/resource1/");
        anno4j.persist(resourceObject);

        ResourceObject resourceObject1 = anno4j.findByID(ResourceObject.class, resourceObject.getResourceAsString());
        assertEquals("http://www.somepage.org/resource1/", resourceObject1.getResourceAsString());
    }

    @Test
    public void testAutomaticResourceNaming() throws RepositoryException, InstantiationException, IllegalAccessException {
        ResourceObject resourceObject = anno4j.createObject(ResourceObject.class);
        assertNotEquals(IDGenerator.BLANK_RESOURCE, resourceObject.getResource());

        ResourceObject resourceResult = anno4j.findByID(ResourceObject.class, resourceObject.getResourceAsString());
        assertEquals(resourceObject.getResourceAsString(), resourceResult.getResourceAsString());
    }

    @Test
    public void testGetNTriples() throws Exception {
        Annotation annotation = anno4j.createObject(Annotation.class);
        annotation.setCreated("" + System.currentTimeMillis());
        annotation.setGenerated("" + System.currentTimeMillis());

        Annotation an = anno4j.findByID(Annotation.class, annotation.getResourceAsString());

        String output = an.getTriples(RDFFormat.NTRIPLES);
        System.out.println("output" + output);
        assertTrue(output.contains("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/oa#Annotation>"));
        assertTrue(output.contains(" <http://purl.org/dc/terms/issued> "));
        assertTrue(output.contains(" <http://purl.org/dc/terms/created> "));
    }

    @Test
    public void testGetTriplesWithTurtle() throws RepositoryException, IllegalAccessException, InstantiationException {
        // Create arbitrary annotation with some provenance information
        Annotation annotation = anno4j.createObject(Annotation.class);
        long time = System.currentTimeMillis();
        annotation.setCreated("" + time);

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

        Annotation an = anno4j.findByID(Annotation.class, annotation.getResourceAsString());

        String output = an.getTriples(RDFFormat.TURTLE);

        // Check for the specific annotation id
        assertTrue(output.contains("<" + an.getResourceAsString() + ">"));

        // Check annotation type
        assertTrue(output.contains("a <http://www.w3.org/ns/oa#Annotation>"));

        // Check provenance
        assertTrue(output.contains("<http://purl.org/dc/terms/created> " + "\"" + time + "\""));

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
        Annotation annotation = this.anno4j.createObject(Annotation.class);
        long time = System.currentTimeMillis();
        annotation.setCreated("" + time);

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

        Annotation an = anno4j.findByID(Annotation.class, annotation.getResourceAsString());

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
                "  \"http://purl.org/dc/terms/created\" : [ {\n" +
                "    \"@value\" : \"" + an.getCreated() + "\"\n" +
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
        annotation.setCreated("" + time);

        Software softwareAgent = anno4j.createObject(Software.class);
        softwareAgent.setHomepage("www.example.org");
        softwareAgent.setName("SoftwareAgentName");

        Person personAgent = anno4j.createObject(Person.class);
        personAgent.setName("PersonAgentName");
        personAgent.setNick("PersonNick");

        annotation.setCreator(softwareAgent);
        annotation.setGenerator(personAgent);

        Annotation an = anno4j.findByID(Annotation.class, annotation.getResourceAsString());

        String output = an.getTriples(RDFFormat.JSONLD);

        String jsonldPersonType1 = "http://xmlns.com/foaf/0.1/Person";
        String jsonldPersonType2 = "https://github.com/anno4j/ns#Agent";
        String jsonldPerson = "\"http://xmlns.com/foaf/0.1/name\" : [ {\n" +
                "    \"@value\" : \"PersonAgentName\"\n" +
                "  } ],\n" +
                "  \"http://xmlns.com/foaf/0.1/nick\" : [ {\n" +
                "    \"@value\" : \"PersonNick\"\n" +
                "  } ]";

        assertTrue(output.contains(jsonldPersonType1));
        assertTrue(output.contains(jsonldPersonType2));
        assertTrue(output.contains(jsonldPerson));

        String jsonoldSoftwareType1 = "http://www.w3.org/ns/prov/SoftwareAgent";
        String jsonoldSoftwareType2 = "https://github.com/anno4j/ns#Agent";
        String jsondldSoftware = "\"http://xmlns.com/foaf/0.1/homepage\" : [ {\n" +
                "    \"@value\" : \"www.example.org\"\n" +
                "  } ],\n" +
                "  \"http://xmlns.com/foaf/0.1/name\" : [ {\n" +
                "    \"@value\" : \"SoftwareAgentName\"\n" +
                "  } ]";

        assertTrue(output.contains(jsonoldSoftwareType1));
        assertTrue(output.contains(jsonoldSoftwareType2));
        assertTrue(output.contains(jsondldSoftware));
    }
}