package com.github.anno4j.model;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.impl.selector.CSSSelector;
import com.github.anno4j.model.impl.selector.DataPositionSelector;
import com.github.anno4j.model.impl.selector.FragmentSelector;
import com.github.anno4j.model.impl.targets.SpecificResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;

import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test suite for the selectors of an annotation.
 * This test uses a fragment selector, that is conform to the W3C Media Fragments. Other selectors can be used accordingly.
 *
 * A simple annotation is set up, provided with a respective selector, then persisted and queried.
 */
public class SelectorTest {
    public final static String SOME_PAGE = "http://example.org/";

    private Anno4j anno4j;

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
    }

    @Test
    public void testSelector() throws Exception {
        // Create annotation
        Annotation annotation = anno4j.createObject(Annotation.class);

        ResourceObject randomObject = anno4j.createObject(ResourceObject.class);
        randomObject.setResourceAsString(SOME_PAGE + "randomobject");

        // Create specific resource and selector
        SpecificResource specificResource = anno4j.createObject(SpecificResource.class);

        // Create selector
        FragmentSelector fragmentSelector = anno4j.createObject(FragmentSelector.class);
        fragmentSelector.setValue("#xywh=50,50,640,480");

        // Connect all entities
        specificResource.setSource(randomObject);
        specificResource.setSelector(fragmentSelector);

        annotation.addTarget(specificResource);

        // Query object
        List<FragmentSelector> result = anno4j.findAll(FragmentSelector.class);

        FragmentSelector resultObject = result.get(0);

        // Tests
        assertEquals(fragmentSelector.getResource(), resultObject.getResource());
        assertEquals(fragmentSelector.getValue(), resultObject.getValue());
    }

    @Test
    public void testRefinedBy() throws RepositoryException, IllegalAccessException, InstantiationException {
        CSSSelector selector = this.anno4j.createObject(CSSSelector.class);

        CSSSelector result = this.anno4j.findByID(CSSSelector.class, selector.getResourceAsString());

        assertEquals(0, result.getRefinedSelectors().size());

        selector.addRefinedSelector(this.anno4j.createObject(DataPositionSelector.class));

        result = this.anno4j.findByID(CSSSelector.class, selector.getResourceAsString());

        assertEquals(1, result.getRefinedSelectors().size());

        HashSet<Selector> selectors = new HashSet<>();
        selectors.add(this.anno4j.createObject(FragmentSelector.class));
        selectors.add(this.anno4j.createObject(FragmentSelector.class));

        selector.setRefinedSelectors(selectors);

        result = this.anno4j.findByID(CSSSelector.class, selector.getResourceAsString());

        assertEquals(2, result.getRefinedSelectors().size());
    }
}