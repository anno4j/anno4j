package com.github.anno4j.model.impl.collection;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;

import java.util.HashSet;
import java.util.LinkedList;

import static org.junit.Assert.*;

/**
 * Test suite for the AnnotationPage interface.
 */
public class AnnotationPageTest {

    private Anno4j anno4j;

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
    }

    @Test
    public void testItems() throws RepositoryException, IllegalAccessException, InstantiationException {
        AnnotationPage page = this.anno4j.createObject(AnnotationPage.class);

        AnnotationPage result = this.anno4j.findByID(AnnotationPage.class, page.getResourceAsString());

        assertEquals(0, result.getItems().size());

        page.addItem(this.anno4j.createObject(Annotation.class));

        result = this.anno4j.findByID(AnnotationPage.class, page.getResourceAsString());

        assertEquals(1, result.getItems().size());

        HashSet<Annotation> annotations = new HashSet<>();
        annotations.add(this.anno4j.createObject(Annotation.class));
        annotations.add(this.anno4j.createObject(Annotation.class));

        page.setItems(annotations);

        result = this.anno4j.findByID(AnnotationPage.class, page.getResourceAsString());

        assertEquals(2, result.getItems().size());
    }

    @Test
    public void testOtherPageFields() throws RepositoryException, IllegalAccessException, InstantiationException {
        AnnotationPage page = this.anno4j.createObject(AnnotationPage.class);
        AnnotationPage prev = this.anno4j.createObject(AnnotationPage.class);
        AnnotationPage next = this.anno4j.createObject(AnnotationPage.class);

        AnnotationCollection collection = this.anno4j.createObject(AnnotationCollection.class);

        page.setNext(next);
        page.setPrev(prev);

        page.setStartIndex(0);
        page.setPartOf(collection);

        AnnotationPage result = this.anno4j.findByID(AnnotationPage.class, page.getResourceAsString());

        assertEquals(prev.getResourceAsString(), result.getPrev().getResourceAsString());
        assertEquals(next.getResourceAsString(), result.getNext().getResourceAsString());

        assertEquals(0, page.getStartIndex());
        assertEquals(collection.getResourceAsString(), result.getPartOf().getResourceAsString());
    }

    @Test
    public void testSymmetricSetters() throws RepositoryException, IllegalAccessException, InstantiationException {
        AnnotationPage prev = this.anno4j.createObject(AnnotationPage.class);
        AnnotationPage next = this.anno4j.createObject(AnnotationPage.class);

        AnnotationPage prev2 = this.anno4j.createObject(AnnotationPage.class);
        AnnotationPage next2 = this.anno4j.createObject(AnnotationPage.class);

        next.setPrevSymmetric(prev);

        prev2.setNextSymmetric(next2);

        assertEquals(prev.getResourceAsString(), next.getPrev().getResourceAsString());
        assertEquals(next.getResourceAsString(), prev.getNext().getResourceAsString());

        assertEquals(prev2.getResourceAsString(), next2.getPrev().getResourceAsString());
        assertEquals(next2.getResourceAsString(), prev2.getNext().getResourceAsString());
    }

    @Test
    public void testGetTriples() throws RepositoryException, IllegalAccessException, InstantiationException {
        AnnotationPage page = this.anno4j.createObject(AnnotationPage.class);

        AnnotationCollection collection = this.anno4j.createObject(AnnotationCollection.class);
        collection.addLabel("someLabel");
        collection.setTotal(3);
        page.setPartOf(collection);

        AnnotationPage next = this.anno4j.createObject(AnnotationPage.class);
        page.setNextSymmetric(next);

        page.setStartIndex(0);

        Annotation annotation1 = this.anno4j.createObject(Annotation.class);
        Annotation annotation2 = this.anno4j.createObject(Annotation.class);
        page.addItem(annotation1);
        page.addItem(annotation2);

        System.out.println(page.getTriples(RDFFormat.JSONLD));
    }
}