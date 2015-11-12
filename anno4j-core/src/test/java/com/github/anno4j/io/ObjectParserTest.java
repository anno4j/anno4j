package com.github.anno4j.io;

import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Body;
import com.github.anno4j.model.Target;
import com.github.anno4j.model.namespaces.DCTYPES;
import com.github.anno4j.model.namespaces.RDF;
import com.github.anno4j.io.impl.ObjectParser;
import org.junit.Test;
import org.openrdf.annotations.Iri;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.rio.RDFFormat;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Testsuite testing the {@link ObjectParser} class.
 */
public class ObjectParserTest {

    @Test
    public void testJSONLD() {

        try {
            URL url = new URL("http://example.com/");

            ObjectParser objectParser = new ObjectParser();
            List<Annotation> annotations = objectParser.parse(JSONLD, url, RDFFormat.JSONLD);

            for(Annotation anno : annotations) {
                System.out.println(anno.getTriples(RDFFormat.JSONLD));
            }

            assertEquals(1, annotations.size());

            objectParser.shutdown();
        } catch (RepositoryException | MalformedURLException | RepositoryConfigException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTurtle() {
        try {
            URL url = new URL("http://example.com/");

            ObjectParser objectParser = new ObjectParser();
            List<Annotation> annotations = objectParser.parse(TURTLE, url, RDFFormat.TURTLE);

            for(Annotation anno : annotations) {
                System.out.println(anno.getTriples(RDFFormat.TURTLE));
            }

            assertEquals(1, annotations.size());

            objectParser.shutdown();
        } catch (IOException | RepositoryException | RepositoryConfigException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMultipleTurtle() {
        try {
            URL url = new URL("http://example.com/");

            ObjectParser objectParser = new ObjectParser();

            objectParser.parse(TURTLE, url, RDFFormat.TURTLE);
            objectParser.parse(TURTLE2, url, RDFFormat.TURTLE);
            List<Annotation> annotations = objectParser.parse(TURTLE3, url, RDFFormat.TURTLE);

            assertEquals(3, annotations.size());

            for(Annotation anno : annotations) {
                System.out.println(anno.getTriples(RDFFormat.TURTLE));
            }

            objectParser.shutdown();
        } catch (IOException | RepositoryException | RepositoryConfigException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMultipleInOneTurtle() {
        try {
            URL url = new URL("http://example.com/");

            ObjectParser objectParser = new ObjectParser();
            List<Annotation> annotations = objectParser.parse(TURTLE_MULTIPLE, url, RDFFormat.TURTLE);

            assertEquals(3, annotations.size());

            for(Annotation anno : annotations) {
                System.out.println(anno.getTriples(RDFFormat.TURTLE));
            }

            objectParser.shutdown();
        } catch (IOException | RepositoryException | RepositoryConfigException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inner class to represent a sound media item.
     */
    @Iri(DCTYPES.SOUND)
    public static class Sound extends Body {

        public Sound() {}

        @Iri(RDF.VALUE)
        private String value;

        /**
         * Sets new value.
         *
         * @param value New value of value.
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Gets value.
         *
         * @return Value of value.
         */
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "Sound{" +
                    "value='" + value + '\'' +
                    '}';
        }
    }

    /**
     * Inner class to represent an image media file as body.
     */
    @Iri(DCTYPES.IMAGE)
    public static class Image extends Target {

        public Image() {}
    }

    private final static String TURTLE = "@prefix oa: <http://www.w3.org/ns/oa#> ." +
            "@prefix ex: <http://www.example.com/ns#> ." +
            "@prefix dctypes: <http://purl.org/dc/dcmitype/> ." +
            "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ." +

            "ex:anno1 a oa:Annotation ;" +
            "   oa:hasBody ex:body1 ;" +
            "   oa:hasTarget ex:target1 ." +

            "ex:body1 a dctypes:Sound ;" +
            "   rdf:value \"somevalue\" ." +
            "ex:target1 a dctypes:Image .";

    private final static String TURTLE2 = "@prefix oa: <http://www.w3.org/ns/oa#> ." +
            "@prefix ex: <http://www.example.com/ns#> ." +
            "@prefix dctypes: <http://purl.org/dc/dcmitype/> ." +

            "ex:anno2 a oa:Annotation ;" +
            "   oa:hasBody ex:body2 ;" +
            "   oa:hasTarget ex:target2 ." +

            "ex:body2 a dctypes:Sound ." +
            "ex:target2 a dctypes:Image .";

    private final static String TURTLE3 = "@prefix oa: <http://www.w3.org/ns/oa#> ." +
            "@prefix ex: <http://www.example.com/ns#> ." +
            "@prefix dctypes: <http://purl.org/dc/dcmitype/> ." +

            "ex:anno3 a oa:Annotation ;" +
            "   oa:hasBody ex:body3 ;" +
            "   oa:hasTarget ex:target3 ." +

            "ex:body3 a dctypes:Sound ." +
            "ex:target3 a dctypes:Image .";

    private final static String TURTLE_MULTIPLE = "@prefix oa: <http://www.w3.org/ns/oa#> ." +
            "@prefix ex: <http://www.example.com/ns#> ." +
            "@prefix dctypes: <http://purl.org/dc/dcmitype/> ." +

            "ex:anno3 a oa:Annotation ;" +
            "   oa:hasBody ex:body3 ;" +
            "   oa:hasTarget ex:target3 ." +

            "ex:body3 a dctypes:Sound ." +
            "ex:target3 a dctypes:Image ." +

            "ex:anno4 a oa:Annotation ;" +
            "   oa:hasBody ex:body4 ;" +
            "   oa:hasTarget ex:target4 ." +

            "ex:body4 a dctypes:Sound ." +
            "ex:target4 a dctypes:Image ." +

            "ex:anno5 a oa:Annotation ;" +
            "   oa:hasBody ex:body5 ;" +
            "   oa:hasTarget ex:target5 ." +

            "ex:body5 a dctypes:Sound ." +
            "ex:target5 a dctypes:Image .";

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
