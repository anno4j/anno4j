package com.github.anno4j.model.impl.collection;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.repository.RepositoryException;

import java.util.HashSet;

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
//        assertEquals(collection.getResourceAsString(), result.getPartof().getResourceAsString());
    }
}