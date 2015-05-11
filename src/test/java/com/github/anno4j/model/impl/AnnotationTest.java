package com.github.anno4j.model.impl;

import com.github.anno4j.model.Annotation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import static org.junit.Assert.assertEquals;

/**
 * Created by schlegel on 06/05/15.
 */
public class AnnotationTest {

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
    public void testPersistAnnotation() throws Exception {
        // Create test annotation
        Annotation annotation = new Annotation();
        annotation.setAnnotatedAt("" + System.currentTimeMillis());
        annotation.setSerializedAt("" + System.currentTimeMillis());

        // persist annotation
        connection.addObject(annotation);

        // query persisted object
        Annotation result = connection.getObject(Annotation.class, annotation.getResource());

        assertEquals(annotation.getResource().toString(), result.getResource().toString());
        assertEquals(annotation.getAnnotatedAt(), result.getAnnotatedAt());
        assertEquals(annotation.getSerializedAt(), result.getSerializedAt());
    }

    @Test
    public void testResourceDefinition() throws Exception {
        // Create annotation
        Annotation annotation = new Annotation();
        Resource resource = new URIImpl("http://www.somepage.org/resource1/");
        annotation.setResource(resource);

        // Persist annotation
        connection.addObject(annotation);

        // Query persisted object
        Annotation result = (Annotation) connection.getObject(annotation.getResource());

        // Tests
        assertEquals(annotation.getResource(), result.getResource());
    }
}