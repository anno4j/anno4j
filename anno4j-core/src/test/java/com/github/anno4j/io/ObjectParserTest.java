package com.github.anno4j.io;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Body;
import com.github.anno4j.model.Target;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.DCTYPES;
import com.github.anno4j.model.namespaces.RDF;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.openrdf.annotations.Iri;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Testsuite testing the {@link ObjectParser} class.
 */
public class ObjectParserTest {

    /**
     * Returns the URIs of the given resource objects (cf. {@link ResourceObject#getResourceAsString()}).
     * @param resources The resources for which to get an URI.
     * @return Returns the URIs of the given resources.
     */
    private static Collection<String> getResourcesAsStrings(Collection<? extends ResourceObject> resources) {
        Collection<String> uris = new LinkedList<>();
        for(ResourceObject resource : resources) {
            uris.add(resource.getResourceAsString());
        }
        return uris;
    }

    @Test
    public void testJSONLD() throws Exception {

            URL url = new URL("http://example.com/");

            ObjectParser objectParser = new ObjectParser();
            List<Annotation> annotations = objectParser.parse(JSONLD, url, RDFFormat.JSONLD, true);

            for(Annotation anno : annotations) {
                System.out.println(anno.toString());
            }

            assertEquals(1, annotations.size());

            objectParser.shutdown();
    }

    @Test
    public void testTurtle() throws Exception {
            URL url = new URL("http://example.com/");

            ObjectParser objectParser = new ObjectParser();
            List<Annotation> annotations = objectParser.parse(TURTLE, url, RDFFormat.TURTLE, true);

            for(Annotation anno : annotations) {
                System.out.println(anno.toString());
            }

            assertEquals(1, annotations.size());

            objectParser.shutdown();
    }

    @Test
    public void testMultipleTurtle() throws Exception {
            URL url = new URL("http://example.com/");

            ObjectParser objectParser = new ObjectParser();

            List<Annotation> annotations = new LinkedList<>();
            annotations.addAll(objectParser.parse(TURTLE, url, RDFFormat.TURTLE, true));
            annotations.addAll(objectParser.parse(TURTLE2, url, RDFFormat.TURTLE, true));
            annotations.addAll(objectParser.parse(TURTLE3, url, RDFFormat.TURTLE, true));

            assertEquals(3, annotations.size());

            for(Annotation anno : annotations) {
                System.out.println(anno.toString());
            }

            objectParser.shutdown();
    }

    @Test
    public void testMultipleInOneTurtle() throws Exception {
            URL url = new URL("http://example.com/");

            ObjectParser objectParser = new ObjectParser();
            List<Annotation> annotations = objectParser.parse(TURTLE_MULTIPLE, url, RDFFormat.TURTLE, true);

            assertEquals(3, annotations.size());

            for(Annotation anno : annotations) {
                System.out.println(anno.toString());
            }

            objectParser.shutdown();
    }

    @Test
    public void testClearing() throws Exception {
        Anno4j anno4j = new Anno4j(new SailRepository(new MemoryStore()), null, false);
        ObjectParser parser = new ObjectParser();

        List<Annotation> annotations = parser.parse(Annotation.class, TURTLE, new URL("http://example.com/"), RDFFormat.TURTLE, true);
        assertEquals(1, annotations.size());

        // Persist the annotations:
        for(Annotation annotation : annotations) {
            anno4j.persist(annotation);
        }
        // Test that values are available in the anno4j-connected repository:
        Body body1 = anno4j.findByID(Body.class,"http://www.example.com/ns#body1");
        Annotation anno1 = anno4j.findByID(Annotation.class,"http://www.example.com/ns#anno1");
        assertEquals(Sets.newHashSet(body1), anno1.getBodies());

        // Read another document clearing the internal triplestore:
        annotations = parser.parse(Annotation.class, TURTLE2, new URL("http://example.com/"), RDFFormat.TURTLE, true);
        assertEquals(1, annotations.size());

        // Persist the annotations:
        for(Annotation annotation : annotations) {
            anno4j.persist(annotation);
        }
        // Test that values are available in the anno4j-connected repository:
        Body body2 = anno4j.findByID(Body.class,"http://www.example.com/ns#body2");
        Annotation anno2 = anno4j.findByID(Annotation.class,"http://www.example.com/ns#anno2");
        assertEquals(Sets.newHashSet(body2), anno2.getBodies());
    }

    @Test
    public void testGenericParsing() throws Exception {
        URL url = new URL("http://example.com/");
        ObjectParser parser = new ObjectParser();

        // Get all dctype:Sound resources:
        List<Sound> sounds = parser.parse(Sound.class, TURTLE_MULTIPLE, url, RDFFormat.TURTLE, true);
        assertEquals(3, sounds.size());
        Collection<String> uris = getResourcesAsStrings(sounds);
        assertTrue(uris.contains("http://www.example.com/ns#body3"));
        assertTrue(uris.contains("http://www.example.com/ns#body4"));
        assertTrue(uris.contains("http://www.example.com/ns#body5"));

        // Get all resources:
        List<ResourceObject> resources = parser.parse(ResourceObject.class, TURTLE_MULTIPLE, url, RDFFormat.TURTLE, true);
        assertEquals(15, resources.size());
        uris = getResourcesAsStrings(resources);
        assertTrue(uris.contains("http://www.example.com/ns#anno3"));
        assertTrue(uris.contains("http://www.example.com/ns#body3"));
        assertTrue(uris.contains("http://www.example.com/ns#target3"));
        assertTrue(uris.contains("http://www.example.com/ns#anno4"));
        assertTrue(uris.contains("http://www.example.com/ns#body4"));
        assertTrue(uris.contains("http://www.example.com/ns#target4"));
        assertTrue(uris.contains("http://www.example.com/ns#anno5"));
        assertTrue(uris.contains("http://www.example.com/ns#body5"));
        assertTrue(uris.contains("http://www.example.com/ns#target5"));
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
