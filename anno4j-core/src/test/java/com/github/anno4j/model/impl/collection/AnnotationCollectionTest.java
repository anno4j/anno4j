package com.github.anno4j.model.impl.collection;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;

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
        Annotation annotation1 = this.anno4j.createObject(Annotation.class);
        Annotation annotation2 = this.anno4j.createObject(Annotation.class);
        page1.addItem(annotation1);
        page1.addItem(annotation2);

        AnnotationPage page2 = this.anno4j.createObject(AnnotationPage.class);
        page1.setNextSymmetric(page2);

        collection.setFirstPage(page1);
        collection.setLastPage(page2);

        AnnotationCollection result = this.anno4j.findByID(AnnotationCollection.class, collection.getResourceAsString());

        assertEquals(2, result.getTotal());
        assertEquals(page1.getResourceAsString(), result.getFirstPage().getResourceAsString());
        assertEquals(page2.getResourceAsString(), result.getLastPage().getResourceAsString());

        assertEquals(page1.getNext().getResourceAsString(), page2.getResourceAsString());
        assertEquals(page2.getPrev().getResourceAsString(), page1.getResourceAsString());
    }

    @Test
    public void testCascadingSetters() throws RepositoryException, IllegalAccessException, InstantiationException {
        AnnotationPage page1 = this.anno4j.createObject(AnnotationPage.class);
        AnnotationPage page2 = this.anno4j.createObject(AnnotationPage.class);
        AnnotationPage page3 = this.anno4j.createObject(AnnotationPage.class);

        AnnotationCollection collection = this.anno4j.createObject(AnnotationCollection.class);

        page1.setNextSymmetric(page2);
        page2.setNextSymmetric(page3);

        collection.setFirstPageCascading(page1);

        assertEquals(collection.getResourceAsString(), page1.getPartOf().getResourceAsString());
        assertEquals(collection.getResourceAsString(), page2.getPartOf().getResourceAsString());
        assertEquals(collection.getResourceAsString(), page3.getPartOf().getResourceAsString());
    }

    @Test
    public void testGetTriples() throws RepositoryException, IllegalAccessException, InstantiationException {
        AnnotationCollection collection = this.anno4j.createObject(AnnotationCollection.class);

        collection.addLabel("someLabel");
        collection.setTotal(3);

        AnnotationPage first = this.anno4j.createObject(AnnotationPage.class);
        Annotation annotationFirst = this.anno4j.createObject(Annotation.class);
        first.addItem(annotationFirst);

        AnnotationPage last = this.anno4j.createObject(AnnotationPage.class);
        Annotation annotationLast = this.anno4j.createObject(Annotation.class);
        last.addItem(annotationLast);

        collection.setFirstPage(first);
        collection.setLastPage(last);

        System.out.println(collection.getTriples(RDFFormat.TURTLE));

        System.out.println("--------------------------------");

        System.out.println(collection.getTriplesExpanded(RDFFormat.TURTLE));
    }
}
