package com.github.anno4j.schema_parsing.util;

import com.google.common.collect.Sets;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;

import static junit.framework.TestCase.assertEquals;

/**
 * Test for {@link StronglyConnectedComponents}.
 */
public class StronglyConnectedComponentsTest {

    private static OntModel model;

    private static Collection<Collection<OntClass>> sccs;

    @Before
    public void setUp() throws Exception {
        // Get the ontology file from the test resources:
        ClassLoader classLoader = getClass().getClassLoader();
        URL ontUrl = classLoader.getResource("scc_test.ttl");
        if(ontUrl == null) {
            throw new FileNotFoundException("scc_test.ttl was not found.");
        }

        // Create a Jena ontology model from the RDF file:
        model = ModelFactory.createOntologyModel();
        model.read(new FileInputStream(ontUrl.getFile()), "http://example.de/ont#", "TURTLE");


        // The strongly connected components in the ontology:
        // See https://en.wikipedia.org/wiki/Strongly_connected_component#/media/File:Graph_Condensation.svg
        // for a visualization:
        sccs = new HashSet<>();

        sccs.add(Sets.newHashSet(
                model.getOntClass("http://example.de/ont#A"),
                model.getOntClass("http://example.de/ont#B"),
                model.getOntClass("http://example.de/ont#C"),
                model.getOntClass("http://example.de/ont#D"),
                model.getOntClass("http://example.de/ont#E")));

        sccs.add(Sets.newHashSet(
                model.getOntClass("http://example.de/ont#F")
        ));

        sccs.add(Sets.newHashSet(
                model.getOntClass("http://example.de/ont#G"),
                model.getOntClass("http://example.de/ont#H"),
                model.getOntClass("http://example.de/ont#I")));

        sccs.add(Sets.newHashSet(
                model.getOntClass("http://example.de/ont#J")
        ));

        sccs.add(Sets.newHashSet(
                model.getOntClass("http://example.de/ont#K"),
                model.getOntClass("http://example.de/ont#L")
        ));

        sccs.add(Sets.newHashSet(
                model.getOntClass("http://example.de/ont#M"),
                model.getOntClass("http://example.de/ont#N"),
                model.getOntClass("http://example.de/ont#O"),
                model.getOntClass("http://example.de/ont#P")
        ));
    }

    @Test
    public void findSCCs() throws Exception {
        Collection<OntClass> seeds = Sets.newHashSet(
                model.getOntClass("http://example.de/ont#A"),
                model.getOntClass("http://example.de/ont#B"),
                model.getOntClass("http://example.de/ont#C"),
                model.getOntClass("http://example.de/ont#D"),
                model.getOntClass("http://example.de/ont#E"),
                model.getOntClass("http://example.de/ont#F"),
                model.getOntClass("http://example.de/ont#G"),
                model.getOntClass("http://example.de/ont#H"),
                model.getOntClass("http://example.de/ont#I"),
                model.getOntClass("http://example.de/ont#J"),
                model.getOntClass("http://example.de/ont#K"),
                model.getOntClass("http://example.de/ont#L"),
                model.getOntClass("http://example.de/ont#M"),
                model.getOntClass("http://example.de/ont#N"),
                model.getOntClass("http://example.de/ont#O"),
                model.getOntClass("http://example.de/ont#P")
        );
        assertEquals(sccs, StronglyConnectedComponents.findSCCs(seeds));
    }

    @Test
    public void findSCCsSingleSource() throws Exception {
        // All classes are subclasses of ex:J:
        Collection<OntClass> seeds = Sets.newHashSet(
            model.getOntClass("http://example.de/ont#J")
        );
        assertEquals(sccs, StronglyConnectedComponents.findSCCs(seeds));
    }
}