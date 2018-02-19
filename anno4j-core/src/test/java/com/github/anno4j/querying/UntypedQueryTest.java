package com.github.anno4j.querying;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.impl.ResourceObject;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * This test checks that resources without an explicit {@code rdf:type} in the repository are
 * retrievable by {@link com.github.anno4j.Anno4j#findAll(Class)} and {@link com.github.anno4j.Anno4j#findByID(Class, URI)}
 * (and overriding methods).
 */
public class UntypedQueryTest {

    /**
     * RDF document in Turtle syntax for testing. This RDF graph doesn't contain type information.
     * But by inference {@code ex:a} and {@code ex:b} are of type {@code rdfs:Resource} and thus should
     * be retrievable as {@link com.github.anno4j.model.impl.ResourceObject}.
     */
    private static final String TEST_TTL = "@prefix ex: <http://example.org/> . " +
            "ex:a ex:foo ex:b . ";

    /**
     * The common Anno4j instance that is tested.
     */
    private Anno4j anno4j;

    /**
     * Returns the (duplicate free) set of URIs of the resources given.
     * @see ResourceObject#getResourceAsString()
     * @param resources The resources that should be converted.
     * @return Returns the set of URIs of the resources.
     */
    private Set<String> getURIsAsString(Collection<? extends ResourceObject> resources) {
        Set<String> uris = new HashSet<>();
        for (ResourceObject resource : resources) {
            uris.add(resource.getResourceAsString());
        }
        return uris;
    }

    /**
     * Reads the RDF document {@link #TEST_TTL} into the {@link #anno4j} connected repository.
     */
    @Before
    public void setUp() throws Exception {
        // Write RDF directly to the repository:
        anno4j = new Anno4j(new SailRepository(new MemoryStore()), null, false);
        RepositoryConnection repositoryConnection = anno4j.getRepository().getConnection();
        repositoryConnection.add(new ByteArrayInputStream(TEST_TTL.getBytes("UTF-8")), "http://example.org/", RDFFormat.TURTLE);
    }

    /**
     * Tests whether untyped resources are retrievable as {@link ResourceObject} using {@link Anno4j#findAll(Class)}.
     */
    @Test
    public void testUntypedFindAll() throws Exception {
        // ex:a, ex:foo and ex:b should be retrievable as ResourceObject:
        List<ResourceObject> resources = anno4j.findAll(ResourceObject.class);
        assertEquals(3, resources.size());
        assertEquals(Sets.newHashSet("http://example.org/a", "http://example.org/foo", "http://example.org/b"), getURIsAsString(resources));

        // ex:a, ex:foo and ex:b shouldn't be retrievable as a more special type:
        List<Annotation> annotations = anno4j.findAll(Annotation.class);
        assertEquals(0, annotations.size());
    }

    /**
     * Tests whether untyped resources are retrievable as {@link ResourceObject} using {@link Anno4j#findByID(Class, URI)}.
     */
    @Test
    public void testUntypedFindById() throws RepositoryException {
        ResourceObject a = anno4j.findByID(ResourceObject.class, "http://example.org/a");
        ResourceObject foo = anno4j.findByID(ResourceObject.class, "http://example.org/foo");
        ResourceObject b = anno4j.findByID(ResourceObject.class, "http://example.org/b");
        assertNotNull(a);
        assertNotNull(foo);
        assertNotNull(b);

        assertNull(anno4j.findByID(Annotation.class, "http://example.org/a"));
    }
}
