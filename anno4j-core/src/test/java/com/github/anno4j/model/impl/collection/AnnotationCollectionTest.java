package com.github.anno4j.model.impl.collection;

import com.github.anno4j.Anno4j;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.repository.RepositoryException;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test suite for the AnnotationCollection and AnnotationPage interfaces.
 */
public class AnnotationCollectionTest {

    private Anno4j anno4j;

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
    }

    @Test
    public void testCollectionLabels() throws RepositoryException, IllegalAccessException, InstantiationException {
        AnnotationCollection collection = this.anno4j.createObject(AnnotationCollection.class);

        AnnotationCollection result = this.anno4j.findByID(AnnotationCollection.class, collection.getResourceAsString());

        assertEquals(0, result.getLabels().size());

        collection.addLabel("red");

        result = this.anno4j.findByID(AnnotationCollection.class, collection.getResourceAsString());

        assertEquals(1, result.getLabels().size());
        assertTrue(result.getLabels().contains("red"));

        HashSet<String> labels = new HashSet<>();
        labels.add("blue");
        labels.add("green");

        collection.setLabels(labels);

        result = this.anno4j.findByID(AnnotationCollection.class, collection.getResourceAsString());

        assertEquals(2, result.getLabels().size());
        assertTrue(result.getLabels().contains("blue"));
        assertTrue(result.getLabels().contains("green"));
    }

    @Test
    public void testOtherCollectionFields() throws RepositoryException, IllegalAccessException, InstantiationException {
        AnnotationCollection collection = this.anno4j.createObject(AnnotationCollection.class);

        AnnotationPage page1 = this.anno4j.createObject(AnnotationPage.class);
        AnnotationPage page2 = this.anno4j.createObject(AnnotationPage.class);

        collection.setTotal(2);
        collection.setFirstPage(page1);
        collection.setLastPage(page2);

        AnnotationCollection result = this.anno4j.findByID(AnnotationCollection.class, collection.getResourceAsString());

        assertEquals(2, result.getTotal());
        assertEquals(page1.getResourceAsString(), result.getFirstPage().getResourceAsString());
        assertEquals(page2.getResourceAsString(), result.getLastPage().getResourceAsString());
    }
}
