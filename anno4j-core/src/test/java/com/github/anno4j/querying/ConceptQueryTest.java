package com.github.anno4j.querying;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.openrdf.annotations.Iri;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link com.github.anno4j.Anno4j#getConcept(String)} and {@link com.github.anno4j.Anno4j#getConcepts(String)}.
 */
public class ConceptQueryTest {

    /*
    Ontology for testing:

                    ex:Vehicle        ex:House
                        |                |
                 -----------------       |
                /       |         \     /
             ex:Car   ex:Truck    ex:Camper
                 |
            ex:Cabrio
     */

    @Iri("http://example.org/v2/House")
    private interface House extends ResourceObject { }

    @Iri("http://example.org/v2/Vehicle")
    private interface Vehicle extends ResourceObject { }

    @Iri("http://example.org/v2/Car")
    private interface Car extends Vehicle { }

    @Iri("http://example.org/v2/Truck")
    private interface Truck extends Vehicle { }

    @Iri("http://example.org/v2/Cabrio")
    private interface Cabrio extends Car { }

    @Iri("http://example.org/v2/Camper")
    private interface Camper extends Vehicle, House { }

    /**
     * Inheritance tree of the above classes and some instances in TTL syntax:
     */
    private static final String ONTOLOGY_RDFS_TURTLE =
            "@prefix ex: <http://example.org/v2/> . " +
            "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> . " +
            // Taxonomy:
            "ex:Car rdfs:subClassOf ex:Vehicle . " +
            "ex:Truck rdfs:subClassOf ex:Vehicle . " +
            "ex:Camper rdfs:subClassOf ex:Vehicle, ex:House . " +
            "ex:Cabrio rdfs:subClassOf ex:Car . " +
            // Instances:
            "ex:r8spyder a ex:Cabrio, ex:Car, ex:Vehicle . " +
            "ex:golf a ex:Car, ex:Vehicle . " +
            "ex:t5 a ex:Car, ex:Truck, ex:Vehicle . " +
            "ex:vario a ex:Camper, ex:Vehicle, ex:House . ";


    @Test
    public void testGetConcept() throws Exception {
        // Create an Anno4j object and load the ontology information:
        Anno4j anno4j = new Anno4j(new SailRepository(new MemoryStore()), null, false);
        InputStream is = new ByteArrayInputStream(ONTOLOGY_RDFS_TURTLE.getBytes("UTF-8"));
        anno4j.getRepository().getConnection().add(is, "http://example.org/v2/", RDFFormat.TURTLE);

        // Test resources are mapped to their correct concepts:
        assertEquals(Cabrio.class, anno4j.getConcept("http://example.org/v2/r8spyder"));
        assertEquals(Car.class, anno4j.getConcept("http://example.org/v2/golf"));
        assertEquals(Camper.class, anno4j.getConcept("http://example.org/v2/vario"));

        // Resources with unknown type are mapped to ResourceObject:
        assertEquals(ResourceObject.class, anno4j.getConcept("http://example.org/v2/318i"));

        // Resources with multiple types are mapped to any of them:
        Class<? extends ResourceObject> t5Concept = anno4j.getConcept("http://example.org/v2/t5");
        assertTrue(t5Concept.equals(Car.class) || t5Concept.equals(Truck.class));
    }

    @Test
    public void testGetConcepts() throws Exception {
        // Create an Anno4j object and load the ontology information:
        Anno4j anno4j = new Anno4j(new SailRepository(new MemoryStore()), null, false);
        InputStream is = new ByteArrayInputStream(ONTOLOGY_RDFS_TURTLE.getBytes("UTF-8"));
        anno4j.getRepository().getConnection().add(is, "http://example.org/v2/", RDFFormat.TURTLE);

        // Test resources are mapped to their correct concepts:
        assertEquals(Sets.<Class<?>>newHashSet(Cabrio.class), anno4j.getConcepts("http://example.org/v2/r8spyder"));
        assertEquals(Sets.<Class<?>>newHashSet(Car.class), anno4j.getConcepts("http://example.org/v2/golf"));
        assertEquals(Sets.<Class<?>>newHashSet(Camper.class), anno4j.getConcepts("http://example.org/v2/vario"));

        // Resources with unknown type are mapped to ResourceObject:
        assertEquals(Sets.<Class<?>>newHashSet(ResourceObject.class), anno4j.getConcepts("http://example.org/v2/318i"));

        // Test resource with multiple specific types:
        assertEquals(Sets.<Class<?>>newHashSet(Car.class, Truck.class), anno4j.getConcepts("http://example.org/v2/t5"));
    }
}
