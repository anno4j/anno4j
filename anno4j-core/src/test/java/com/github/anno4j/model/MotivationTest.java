package com.github.anno4j.model;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.motivation.Bookmarking;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.repository.object.ObjectConnection;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test suite for the motivation of an annotation. Only one instance is tested, all the other types of motivations are built up in the same fashion.
 *
 * An annotation with motivation is set up, persisted, and queried.
 */
public class MotivationTest {

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
    public void testMotivationBookmarking() throws Exception {
        // Create test annotation
        Annotation annotation = anno4j.createObject(Annotation.class);

        // Create and add motivation
        Motivation motivation = anno4j.createObject(Bookmarking.class);
        annotation.setMotivatedBy(motivation);

        // Persist annotation
        connection.addObject(annotation);

        // Query persisted object
        List<Bookmarking> result = connection.getObjects(Bookmarking.class).asList();

        // Tests
        assertEquals(1, result.size());

        assertEquals(motivation.getResource().toString(), result.get(0).getResource().toString());
    }
}