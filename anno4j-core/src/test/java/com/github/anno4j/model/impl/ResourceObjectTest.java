package com.github.anno4j.model.impl;

import com.github.anno4j.Anno4j;
import com.github.anno4j.example.TextAnnotationBody;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.impl.agent.Person;
import com.github.anno4j.model.impl.agent.Software;
import com.github.anno4j.model.impl.body.TextualBody;
import com.github.anno4j.querying.Comparison;
import com.github.anno4j.querying.QueryService;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.idGenerator.IDGenerator;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;

import java.util.List;

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
    public void testQueryForResource() throws RepositoryException, IllegalAccessException, InstantiationException, ParseException, MalformedQueryException, QueryEvaluationException {
        Annotation annotation = this.anno4j.createObject(Annotation.class);

        TextualBody body = this.anno4j.createObject(TextualBody.class, (Resource) new URIImpl("http://example.org/resource1"));
        annotation.addBody(body);

        System.out.println(body.getResourceAsString());

        QueryService qs = this.anno4j.createQueryService();
        qs.addCriteria("oa:hasBody", "http://example.org/resource1");

        List<Annotation> result = qs.execute(Annotation.class);

        assertEquals(1, result.size());
    }

    @Test
    public void testQueryYourself() throws RepositoryException, IllegalAccessException, InstantiationException, ParseException, MalformedQueryException, QueryEvaluationException {
        TextualBody body = this.anno4j.createObject(TextualBody.class, (Resource) new URIImpl("http://example.org/resource1"));

        QueryService qs = this.anno4j.createQueryService();
        qs.addCriteria(".", "http://example.org/resource1");

        List<TextualBody> result = qs.execute(TextualBody.class);

        assertEquals(1, result.size());
    }

    @Test
    public void testDoubleCreation() throws RepositoryException, IllegalAccessException, InstantiationException {
        anno4j.createObject(TextualBody.class, (Resource) new URIImpl("http://example.org/resource1"));
        anno4j.createObject(TextualBody.class, (Resource) new URIImpl("http://example.org/resource1"));

        assertEquals(anno4j.findAll(TextualBody.class).size(), 1);
    }

    @Test
    public void testFindAllResourceObject() throws RepositoryException, IllegalAccessException, InstantiationException {
        anno4j.createObject(ResourceObject.class, new URIImpl("http://example.org/resource1"));

        assertEquals(anno4j.findAll(ResourceObject.class).size(), 1);
    }

    @Test
    public void testSetResourceAsString() throws Exception {
        ResourceObject resourceObject = anno4j.createObject(ResourceObject.class);
        resourceObject.setResourceAsString("http://www.somepage.org/resource1/");

        ResourceObject resourceObject1 = anno4j.findByID(ResourceObject.class, resourceObject.getResourceAsString());
        assertEquals("http://www.somepage.org/resource1/", resourceObject1.getResourceAsString());
    }

    @Test
    public void testPostSetResourceAsStringModification() throws Exception {
        Annotation annotation = anno4j.createObject(Annotation.class);
        annotation.setResourceAsString("http://www.somepage.org/resource1/");

        TextualBody body = this.anno4j.createObject(TextualBody.class, (Resource) new URIImpl("http://example.org/body"));
        annotation.addBody(body);

        Annotation annotation1 = anno4j.findByID(Annotation.class, annotation.getResourceAsString());
        assertEquals("http://www.somepage.org/resource1/", annotation1.getResourceAsString());
        assertEquals(1, annotation1.getBodies().size());
        assertEquals(body.getResourceAsString(), annotation1.getBodies().iterator().next().getResourceAsString());
    }

    @Test
    public void testAutomaticResourceNaming() throws RepositoryException, InstantiationException, IllegalAccessException, QueryEvaluationException {
        ResourceObject resourceObject = anno4j.createObject(ResourceObject.class);
        assertNotEquals(IDGenerator.BLANK_RESOURCE, resourceObject.getResource());

        ResourceObject resourceResult = anno4j.findByID(ResourceObject.class, resourceObject.getResourceAsString());
        assertEquals(resourceObject.getResourceAsString(), resourceResult.getResourceAsString());
    }

    @Test
    public void testGetNTriples() throws Exception {
        Annotation annotation = anno4j.createObject(Annotation.class);
        annotation.setCreated("2015-01-28T12:00:00Z");
        annotation.setGenerated("2015-01-28T12:00:00Z");

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
        annotation.setCreated("2015-01-28T12:00:00Z");

        // Create a (for test cases only) textual body
        TextAnnotationBody body = anno4j.createObject(TextAnnotationBody.class);
        String value = "someValue";
        String format = "someFormat";
        String language = "someLanguage";
        body.setValue(value);
        body.setFormat(format);
        body.setLanguage(language);

        // Add the body to the annotation
        annotation.addBody(body);

        Annotation an = anno4j.findByID(Annotation.class, annotation.getResourceAsString());

        String output = an.getTriples(RDFFormat.TURTLE);

        // Check for the specific annotation id
        assertTrue(output.contains("<" + an.getResourceAsString() + ">"));

        // Check annotation type
        assertTrue(output.contains("a <http://www.w3.org/ns/oa#Annotation>"));

        // Check provenance
        assertTrue(output.contains("<http://purl.org/dc/terms/created> " + "\"" + "2015-01-28T12:00:00Z" + "\""));

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
        annotation.setCreated("2015-01-28T12:00:00Z");

        // Create a (for test cases only) textual body
        TextAnnotationBody body = anno4j.createObject(TextAnnotationBody.class);
        String value = "someValue";
        String format = "someFormat";
        String language = "someLanguage";
        body.setValue(value);
        body.setFormat(format);
        body.setLanguage(language);

        // Add the body to the annotation
        annotation.addBody(body);

        Annotation an = anno4j.findByID(Annotation.class, annotation.getResourceAsString());

        String output = an.getTriples(RDFFormat.JSONLD);

        // Create Strings that need to be contained in the JSONLD output (at some place)
        String jsonldBody = "  \"@id\" : \"" + body.getResourceAsString() + "\",\n" +
                "  \"@type\" : [ \"http://www.w3.org/ns/oa#EmbeddedContent\", \"https://github.com/anno4j/ns#CreationProvenance\", \"https://github.com/anno4j/ns#ExternalWebResource\", \"https://github.com/anno4j/ns#Resource\" ],\n" +
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
        annotation.setCreated("2015-01-28T12:00:00Z");

        Software softwareAgent = anno4j.createObject(Software.class);
        softwareAgent.setHomepage("www.example.org");
        softwareAgent.setName("SoftwareAgentName");

        Person personAgent = anno4j.createObject(Person.class);
        personAgent.setName("PersonAgentName");
        personAgent.setNickname("PersonNick");

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