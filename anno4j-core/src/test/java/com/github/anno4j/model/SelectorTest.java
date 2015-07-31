package com.github.anno4j.model;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.impl.selector.FragmentSelector;
import com.github.anno4j.model.impl.selector.FragmentSpecification;
import com.github.anno4j.model.impl.target.SpecificResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.repository.Repository;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Test suite for the selectors of an annotation.
 * This test uses a fragment selector, that is conform to the W3C Media Fragments. Other selectors can be used accordingly.
 *
 * A simple annotation is set up, provided with a respective selector, then persisted and queried.
 */
public class SelectorTest {

    Repository repository;
    ObjectConnection connection;

    public final static String SOME_PAGE = "http://example.org/";

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
    public void testSelector() throws Exception {
        // Create annotation
        Annotation annotation = new Annotation();

        ResourceObject randomObject = new ResourceObject(SOME_PAGE + "randomobject");

        // Create specific resource and selector
        SpecificResource specificResource = new SpecificResource();

        // Create selector
        FragmentSelector fragmentSelector = new FragmentSelector();
        fragmentSelector.setConformsToFragmentSpecification(FragmentSpecification.W3C_MEDIA_FRAGMENTS);
        fragmentSelector.setValue("#xywh=50,50,640,480");

        // Connect all entities
        specificResource.setSource(randomObject);
        specificResource.setSelector(fragmentSelector);

        annotation.setTarget(specificResource);

        // Persist annotation
        connection.addObject(annotation);

        // Query object
        List<FragmentSelector> result = connection.getObjects(FragmentSelector.class).asList();

        FragmentSelector resultObject = result.get(0);

        // Tests
        assertEquals(fragmentSelector.getResource(), resultObject.getResource());
        assertEquals(fragmentSelector.getValue(), resultObject.getValue());
    }
}