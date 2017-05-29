package com.github.anno4j.schema_parsing.util;

import com.github.anno4j.Anno4j;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.google.common.collect.Sets;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.rio.RDFFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;

import static junit.framework.TestCase.assertEquals;

/**
 * Test for {@link StronglyConnectedComponents}.
 */
public class StronglyConnectedComponentsTest {

    private Anno4j anno4j;

    private static Collection<Collection<RDFSClazz>> sccs;

    @Before
    public void setUp() throws Exception {
        // Get the ontology file from the test resources:
        ClassLoader classLoader = getClass().getClassLoader();
        URL ontUrl = classLoader.getResource("scc_test.ttl");
        if(ontUrl == null) {
            throw new FileNotFoundException("scc_test.ttl was not found.");
        }

        anno4j = new Anno4j();
        anno4j.getRepository().getConnection().add(new File(ontUrl.getFile()), "http://example.de/ont#", RDFFormat.TURTLE, (Resource) null);

        // The strongly connected components in the ontology:
        // See https://en.wikipedia.org/wiki/Strongly_connected_component#/media/File:Graph_Condensation.svg
        // for a visualization:
        sccs = new HashSet<>();

        sccs.add(Sets.newHashSet(
                anno4j.findByID(RDFSClazz.class, "http://example.de/ont#A"),
                anno4j.findByID(RDFSClazz.class, "http://example.de/ont#B"),
                anno4j.findByID(RDFSClazz.class, "http://example.de/ont#C"),
                anno4j.findByID(RDFSClazz.class, "http://example.de/ont#D"),
                anno4j.findByID(RDFSClazz.class, "http://example.de/ont#E")));

        sccs.add(Sets.newHashSet(
                anno4j.findByID(RDFSClazz.class, "http://example.de/ont#F")
        ));

        sccs.add(Sets.newHashSet(
                anno4j.findByID(RDFSClazz.class, "http://example.de/ont#G"),
                anno4j.findByID(RDFSClazz.class, "http://example.de/ont#H"),
                anno4j.findByID(RDFSClazz.class, "http://example.de/ont#I")));

        sccs.add(Sets.newHashSet(
                anno4j.findByID(RDFSClazz.class, "http://example.de/ont#J")
        ));

        sccs.add(Sets.newHashSet(
                anno4j.findByID(RDFSClazz.class, "http://example.de/ont#K"),
                anno4j.findByID(RDFSClazz.class, "http://example.de/ont#L")
        ));

        sccs.add(Sets.newHashSet(
                anno4j.findByID(RDFSClazz.class, "http://example.de/ont#M"),
                anno4j.findByID(RDFSClazz.class, "http://example.de/ont#N"),
                anno4j.findByID(RDFSClazz.class, "http://example.de/ont#O"),
                anno4j.findByID(RDFSClazz.class, "http://example.de/ont#P")
        ));
    }

    @Test
    public void findSCCs() throws Exception {
        Collection<RDFSClazz> seeds = Sets.newHashSet(
                anno4j.findByID(RDFSClazz.class, "http://example.de/ont#A"),
                anno4j.findByID(RDFSClazz.class, "http://example.de/ont#B"),
                anno4j.findByID(RDFSClazz.class, "http://example.de/ont#C"),
                anno4j.findByID(RDFSClazz.class, "http://example.de/ont#D"),
                anno4j.findByID(RDFSClazz.class, "http://example.de/ont#E"),
                anno4j.findByID(RDFSClazz.class, "http://example.de/ont#F"),
                anno4j.findByID(RDFSClazz.class, "http://example.de/ont#G"),
                anno4j.findByID(RDFSClazz.class, "http://example.de/ont#H"),
                anno4j.findByID(RDFSClazz.class, "http://example.de/ont#I"),
                anno4j.findByID(RDFSClazz.class, "http://example.de/ont#J"),
                anno4j.findByID(RDFSClazz.class, "http://example.de/ont#K"),
                anno4j.findByID(RDFSClazz.class, "http://example.de/ont#L"),
                anno4j.findByID(RDFSClazz.class, "http://example.de/ont#M"),
                anno4j.findByID(RDFSClazz.class, "http://example.de/ont#N"),
                anno4j.findByID(RDFSClazz.class, "http://example.de/ont#O"),
                anno4j.findByID(RDFSClazz.class, "http://example.de/ont#P")
        );
        assertEquals(sccs, StronglyConnectedComponents.findSCCs(seeds));
    }
}