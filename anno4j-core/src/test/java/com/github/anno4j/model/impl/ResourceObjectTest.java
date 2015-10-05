package com.github.anno4j.model.impl;

import com.github.anno4j.model.Annotation;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.repository.Repository;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

/**
 * Created by schlegel on 05/10/15.
 */
public class ResourceObjectTest extends TestCase {

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

    @Test
    public void testGetNTriples() throws Exception {
        Annotation annotation = new Annotation();
        annotation.setAnnotatedAt("" + System.currentTimeMillis());
        annotation.setSerializedAt("" + System.currentTimeMillis());

        String output = annotation.getNTriples();

        assertTrue(output.contains("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/oa#Annotation>"));
        assertTrue(output.contains(" <http://www.w3.org/ns/oa#annotatedAt> "));
        assertTrue(output.contains(" <http://www.w3.org/ns/oa#serializedAt> "));
    }
}