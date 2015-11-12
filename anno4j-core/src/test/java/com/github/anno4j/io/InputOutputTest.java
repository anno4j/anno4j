package com.github.anno4j.io;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Body;
import com.github.anno4j.model.Target;
import com.github.anno4j.model.namespaces.DCTYPES;
import com.github.anno4j.model.namespaces.RDF;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.annotations.Iri;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.rio.RDFFormat;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * This test suite is used in order to test different setup configurations of input and output of annotations.
 */
public class InputOutputTest {

    private Anno4j anno4j;
    private ObjectConnection connection;

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
        this.connection = this.anno4j.getObjectRepository().getConnection();
    }

    @After
    public void tearDown() throws Exception {
        connection.close();
    }

    @Test
    /**
     * This test creates an Annotation object by parsing a JSONLD string, then the annotation is persisted.
     * It is then checked, if both the persisted and parsed Annotation have the same ID.
     */
    public void testInputAndPersistAnnotation() {

        try {
            URL url = new URL("http://baseParserURL.com/");

            // Create and ObjectParser
            ObjectParser objectParser = new ObjectParser();

            // Parse the annotation JS
            List<Annotation> annotations = objectParser.parse(JSONLD, url, RDFFormat.JSONLD);

            // Print the (parsed) annotation
            Annotation annotation = annotations.get(0);

            System.out.println("Initial annotation ID: " + annotation.getResourceAsString());

            System.out.println(annotation.getTriples(RDFFormat.JSONLD));

            // Change the ID of the annotation
            annotation.setResourceAsString("http://thisismynewID.com/");

            // Print the (altered) annotation
            System.out.println("Altered annotation ID: " + annotation.getResource().toString());

            System.out.println(annotation.getTriples(RDFFormat.JSONLD));

            // Persist the annotation
            anno4j.createPersistenceService().persistAnnotation(annotation);

            // Query for persisted annotations
            List<Annotation> result = connection.getObjects(Annotation.class).asList();

            assertEquals(1, result.size());

            Annotation resultObject = result.get(0);

            // Print the (queried) annotation
            System.out.println("Persisted annotation ID: " + resultObject.getResource().toString());

            System.out.println(resultObject.getTriples(RDFFormat.JSONLD));

            // Test for the same ID on the altered and persisted annotation
            assertEquals(annotation.getResourceAsString(), resultObject.getResourceAsString());
        } catch (RepositoryException e) {
            e.printStackTrace();
        } catch (RepositoryConfigException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (QueryEvaluationException e) {
            e.printStackTrace();
        }


    }

    /**
     * Inner class to represent a sound media item.
     */
    @Iri(DCTYPES.SOUND)
    public static interface Sound extends Body {

        @Iri(RDF.VALUE)
        String getValue();

        @Iri(RDF.VALUE)
        void setValue(String value);
    }

    /**
     * Inner class to represent an image media file as body.
     */
    @Iri(DCTYPES.IMAGE)
    public static interface Image extends Target {}

    private final static String JSONLD = "{\n" +
            "  \"@context\": {\n" +
            "    \"oa\" :     \"http://www.w3.org/ns/oa#\",\n" +
            "    \"dc\" :     \"http://purl.org/dc/elements/1.1/\",\n" +
            "    \"dcterms\": \"http://purl.org/dc/terms/\",\n" +
            "    \"dctypes\": \"http://purl.org/dc/dcmitype/\",\n" +
            "    \"foaf\" :   \"http://xmlns.com/foaf/0.1/\",\n" +
            "    \"rdf\" :    \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\",\n" +
            "    \"rdfs\" :   \"http://www.w3.org/2000/01/rdf-schema#\",\n" +
            "    \"skos\" :   \"http://www.w3.org/2004/02/skos/core#\",\n" +
            "\n" +
            "    \"body\" :         {\"@id\" : \"oa:hasBody\"},\n" +
            "    \"target\" :       {\"@type\":\"@id\", \"@id\" : \"oa:hasTarget\"},\n" +
            "    \"source\" :       {\"@type\":\"@id\", \"@id\" : \"oa:hasSource\"},\n" +
            "    \"selector\" :     {\"@type\":\"@id\", \"@id\" : \"oa:hasSelector\"},\n" +
            "    \"state\" :        {\"@type\":\"@id\", \"@id\" : \"oa:hasState\"},\n" +
            "    \"scope\" :        {\"@type\":\"@id\", \"@id\" : \"oa:hasScope\"},\n" +
            "    \"annotatedBy\" :  {\"@type\":\"@id\", \"@id\" : \"oa:annotatedBy\"},\n" +
            "    \"serializedBy\" : {\"@type\":\"@id\", \"@id\" : \"oa:serializedBy\"},\n" +
            "    \"motivation\" :   {\"@type\":\"@id\", \"@id\" : \"oa:motivatedBy\"},\n" +
            "    \"stylesheet\" :   {\"@type\":\"@id\", \"@id\" : \"oa:styledBy\"},\n" +
            "    \"cached\" :       {\"@type\":\"@id\", \"@id\" : \"oa:cachedSource\"},\n" +
            "    \"conformsTo\" :   {\"@type\":\"@id\", \"@id\" : \"dcterms:conformsTo\"},\n" +
            "    \"members\" :      {\"@type\":\"@id\", \"@id\" : \"oa:membershipList\", \"@container\": \"@list\"},\n" +
            "    \"item\" :         {\"@type\":\"@id\", \"@id\" : \"oa:item\"},\n" +
            "    \"related\" :      {\"@type\":\"@id\", \"@id\" : \"skos:related\"},\n" +
            "\n" +
            "    \"format\" :       \"dc:format\",\n" +
            "    \"language\":      \"dc:language\",\n" +
            "    \"annotatedAt\" :  \"oa:annotatedAt\",\n" +
            "    \"serializedAt\" : \"oa:serializedAt\",\n" +
            "    \"when\" :         \"oa:when\",\n" +
            "    \"value\" :        \"rdf:value\",\n" +
            "    \"start\" :        \"oa:start\",\n" +
            "    \"end\" :          \"oa:end\",\n" +
            "    \"exact\" :        \"oa:exact\",\n" +
            "    \"prefix\" :       \"oa:prefix\",\n" +
            "    \"suffix\" :       \"oa:suffix\",\n" +
            "    \"label\" :        \"rdfs:label\",\n" +
            "    \"name\" :         \"foaf:name\",\n" +
            "    \"mbox\" :         \"foaf:mbox\",\n" +
            "    \"nick\" :         \"foaf:nick\",\n" +
            "    \"styleClass\" :   \"oa:styleClass\"\n" +
            "  },\n" +

            " \"@id\": \"http://example.org/anno1\" , \n" +
            " \"@type\":\"oa:Annotation\" , \n" +
            " \"body\": { \n" +
            " \"@id\":\"http://example.org/body1\", \n" +
            " \"@type\":\"dctypes:Sound\", \n" +
            " \"value\":\"someValue\" \n" +
            " }, \n" +
            " \"target\": { \n" +
            " \"@id\": \"http://example.org/target1\", \n" +
            " \"@type\": \"dctypes:Image\" \n" +
            " }\n" +
            "}";
}
