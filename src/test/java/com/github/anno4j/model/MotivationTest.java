package com.github.anno4j.model;

import com.github.anno4j.model.impl.annotation.AnnotationDefault;
import com.github.anno4j.model.impl.motivation.Bookmarking;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.result.Result;
import org.openrdf.sail.memory.MemoryStore;

import java.util.List;

import static org.junit.Assert.*;

public class MotivationTest {

    Repository repository;
    ObjectConnection connection;

    @Before
    public void setUp() throws Exception {
        repository = new SailRepository(new MemoryStore());
        repository.initialize();

        ObjectRepositoryFactory factory = new ObjectRepositoryFactory();
        ObjectRepository objectRepository = factory.createRepository(repository);
        connection = objectRepository.getConnection();
    }

    @After
    public void tearDown() throws Exception {
        connection.close();
    }

    @Test
    public void testMotivationBookmarking() throws Exception {
        // Create test annotation
        Annotation annotation = new AnnotationDefault();

        // Create and add motivation
        Motivation motivation = new Bookmarking();
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