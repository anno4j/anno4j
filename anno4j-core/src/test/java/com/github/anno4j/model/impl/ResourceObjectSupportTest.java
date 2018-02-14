package com.github.anno4j.model.impl;

import com.github.anno4j.Anno4j;
import com.github.anno4j.io.ObjectParser;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.impl.agent.Person;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.annotations.Iri;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;

import java.net.URL;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Test for {@link ResourceObjectSupport}.
 * Tests for instance checking and casting are implemented in {@link SpecialAnnotationSupport}.
 */
public class ResourceObjectSupportTest {

    /**
     * Test graph for {@link ResourceObjectSupport#getTriples(RDFFormat, int, Resource[])} in Turtle syntax:
     *
     *      ____ e
     *      |
     * a -- b -- d -- f
     *   |  |\________
     *   |  |         |
     *   |  --- g --- h
     *   |
     *   -- c
     */
    private static final String GET_TRIPLES_TEST_GRAPH_DEFAULT = "@prefix ex: <http://example.de/> . " +
            "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . " +
            "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> . " +
            "ex:a ex:foo ex:b . " +
            "ex:a ex:foo ex:c . " +
            "ex:b ex:foo ex:d . " +
            "ex:b ex:foo ex:e . " +
            "ex:b ex:foo ex:g . " +
            "ex:b ex:foo ex:h . " +
            "ex:d ex:foo ex:f . " +
            "ex:g ex:foo ex:h . " +
            "ex:a a ex:special_annotation . " +
            "ex:b a ex:special_annotation . " +
            "ex:c a ex:special_annotation . " +
            "ex:d a ex:special_annotation . " +
            "ex:e a ex:special_annotation . " +
            "ex:f a ex:special_annotation . " +
            "ex:g a ex:special_annotation . " +
            "ex:h a ex:special_annotation . ";

    /**
     * Test graph for {@link ResourceObjectSupport#getTriples(RDFFormat, int, Resource[])} that will be persisted in a
     * non-default context:
     *
     * c --- i
     */
    private static final String GET_TRIPLES_TEST_GRAPH_WITH_CONTEXT = "@prefix ex: <http://example.de/> . " +
            "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . " +
            "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> . " +
            "ex:c ex:foo ex:i . " +
            "ex:c a ex:special_annotation . " +
            "ex:i a ex:special_annotation . ";

    /**
     * Subtype of the OADM annotation which provides a testing method.
     */
    @Iri("http://example.de/special_annotation")
    public interface SpecialAnnotation extends Annotation {

        @Iri("http://example.de/foo")
        Set<SpecialAnnotation> getNeighbours();

        @Iri("http://example.de/foo")
        void setNeighbours(Set<SpecialAnnotation> neighbours);

        /**
         * Tests {@link ResourceObjectSupport#isInstance(Resource)} and {@link ResourceObjectSupport#isInstance(Class)}.
         * Is implemented in {@link SpecialAnnotationSupport} because these tests must be run inside a support class.
         */
        void testTypeChecking() throws Exception;

        /**
         * Tests {@link ResourceObjectSupport#cast(Class)}.
         * Is implemented in {@link SpecialAnnotationSupport} because these tests must be run inside a support class.
         */
        void testCasting() throws Exception;
    }

    /**
     * Subtype of the testing annotation for testing down-casts.
     */
    @Iri("http://example.de/very_special_annotation")
    public interface VerySpecialAnnotation extends SpecialAnnotation {
    }

    /**
     * The resource object used for testing.
     */
    private VerySpecialAnnotation annotation;

    @Before
    public void setUp() throws Exception {
        Anno4j anno4j = new Anno4j();
        annotation = anno4j.createObject(VerySpecialAnnotation.class);
    }

    /**
     * Tests instance checking and casting.
     */
    @Test
    public void testTypeChecking() throws Exception {
        annotation.testTypeChecking();
    }

    /**
     * Tests instance checking and casting.
     */
    @Test
    public void testCasting() throws Exception {
        annotation.testCasting();
    }

    /**
     * Reads a RDF document to a certain context of a Anno4j-connected repository.
     * @param type The type of resources to read.
     * @param anno4j The Anno4j object to read to.
     * @param rdfDocument The RDF document to read.
     * @param documentUrl The URL of the document.
     * @param format The format of the document.
     * @param context The context to which the RDF data should be read. A value of null corresponds to the default graph.
     * @param <T> The type of resources to read.
     * @throws RepositoryException Thrown if an error occurs accessing the repository of the Anno4j object or the intermediate repository.
     * @throws RDFParseException Thrown if an error occurs parsing the given RDF document in the given format.
     */
    private <T extends ResourceObject> void parseRDF(Class<T> type, Anno4j anno4j, String rdfDocument, URL documentUrl, RDFFormat format, URI context) throws RepositoryException, RDFParseException {
        // Parse the RDF document using ObjectParser:
        ObjectParser parser = null;
        try {
            parser = new ObjectParser();
        } catch (RepositoryConfigException e) {
            throw new RepositoryException(e);
        }
        List<T> resources = parser.parse(type, rdfDocument, documentUrl, format, false);

        for (T resource : resources) {
            anno4j.persist(resource, context);
        }
    }

    @Test
    public void testGetTriples() throws Exception {
        Anno4j anno4j = new Anno4j(new SailRepository(new MemoryStore()), null, false);

        // Parse first RDF graph to default context:
        parseRDF(SpecialAnnotation.class, anno4j, GET_TRIPLES_TEST_GRAPH_DEFAULT, new URL("http://example.de/"), RDFFormat.TURTLE, null);

        // Parse second RDF graph to a named context:
        URI context = new URIImpl("http://example.de/context1");
        parseRDF(SpecialAnnotation.class, anno4j, GET_TRIPLES_TEST_GRAPH_WITH_CONTEXT, new URL("http://example.de/"), RDFFormat.TURTLE, context);

        SpecialAnnotation a = anno4j.findByID(SpecialAnnotation.class, "http://example.de/a");

        // Test export with maximum path length of one:
        Anno4j parsed = new Anno4j();
        String serialized = a.getTriples(RDFFormat.JSONLD, 1, null);
        parseRDF(SpecialAnnotation.class, parsed, serialized, new URL("http://example.de/"), RDFFormat.JSONLD, null);
        assertNotNull(parsed.findByID(SpecialAnnotation.class, "http://example.de/b"));
        assertNotNull(parsed.findByID(SpecialAnnotation.class, "http://example.de/c"));
        assertNull(parsed.findByID(SpecialAnnotation.class, "http://example.de/e"));
        assertNull(parsed.findByID(SpecialAnnotation.class, "http://example.de/d"));
        assertNull(parsed.findByID(SpecialAnnotation.class, "http://example.de/f"));
        assertNull(parsed.findByID(SpecialAnnotation.class, "http://example.de/h"));
        assertNull(parsed.findByID(SpecialAnnotation.class, "http://example.de/i"));

        // Test longer path length exceeding length of some paths:
        parsed = new Anno4j();
        serialized = a.getTriples(RDFFormat.JSONLD, 2, null);
        parseRDF(SpecialAnnotation.class, parsed, serialized, new URL("http://example.de/"), RDFFormat.JSONLD, null);
        assertNotNull(parsed.findByID(SpecialAnnotation.class, "http://example.de/b"));
        assertNotNull(parsed.findByID(SpecialAnnotation.class, "http://example.de/c"));
        assertNotNull(parsed.findByID(SpecialAnnotation.class, "http://example.de/e"));
        assertNotNull(parsed.findByID(SpecialAnnotation.class, "http://example.de/d"));
        assertNotNull(parsed.findByID(SpecialAnnotation.class, "http://example.de/h"));
        assertNull(parsed.findByID(SpecialAnnotation.class, "http://example.de/f"));
        assertNull(parsed.findByID(SpecialAnnotation.class, "http://example.de/i"));

        // Test multiple contexts:
        parsed = new Anno4j();
        serialized = a.getTriples(RDFFormat.JSONLD, 2, context, null);
        parseRDF(SpecialAnnotation.class, parsed, serialized, new URL("http://example.de/"), RDFFormat.JSONLD, null);
        assertNotNull(parsed.findByID(SpecialAnnotation.class, "http://example.de/b"));
        assertNotNull(parsed.findByID(SpecialAnnotation.class, "http://example.de/c"));
        assertNotNull(parsed.findByID(SpecialAnnotation.class, "http://example.de/e"));
        assertNotNull(parsed.findByID(SpecialAnnotation.class, "http://example.de/d"));
        assertNotNull(parsed.findByID(SpecialAnnotation.class, "http://example.de/h"));
        assertNotNull(parsed.findByID(SpecialAnnotation.class, "http://example.de/i"));
        assertNull(parsed.findByID(SpecialAnnotation.class, "http://example.de/f"));
    }

    @Test
    public void testRefreshing() throws Exception {
        Anno4j anno4j = new Anno4j(new SailRepository(new MemoryStore()), null, false);

        Person a = anno4j.createObject(Person.class, (Resource) new URIImpl("http://example.de/anno1"));
        a.setMbox("alice@example.org");

        Person b = anno4j.createObject(Person.class, (Resource) new URIImpl("http://example.de/anno1"));
        b.setMbox("bob@example.org");

        // Refresh the first object and check whether it returns the new value:
        a.update();
        assertEquals("bob@example.org", a.getMbox());
    }
}