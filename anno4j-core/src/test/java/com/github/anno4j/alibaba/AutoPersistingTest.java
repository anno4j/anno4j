package com.github.anno4j.alibaba;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Body;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.annotations.Iri;
import org.openrdf.repository.RepositoryException;

import static org.junit.Assert.assertEquals;

/**
 * Created by schlegel on 17/02/16.
 */
public class AutoPersistingTest {

    private Anno4j anno4j;

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
    }

    @Test
    public void autoSaveTest() throws RepositoryException, IllegalAccessException, InstantiationException {
        // Create test annotation
        Annotation annotation = anno4j.createObject(Annotation.class);
        TestBody body =  anno4j.createObject(TestBody.class);
        body.setValue("FirstValue");
        annotation.setBody(body);

        // persist annotation
        anno4j.persist(annotation);

        // update value
        Annotation queriedAnnotation = anno4j.findByID(Annotation.class, annotation.getResourceAsString());
        assertEquals("FirstValue", ((TestBody)queriedAnnotation.getBody()).getValue());
        ((TestBody)queriedAnnotation.getBody()).setValue("UpdatedValue");

        Annotation updatedAnnotation = anno4j.findByID(Annotation.class, annotation.getResourceAsString());
        assertEquals("UpdatedValue", ((TestBody)queriedAnnotation.getBody()).getValue());
    }

    @Iri("http://www.example.com/schema#AutoPersistingTestBody")
    public static interface TestBody extends Body {
        @Iri("http://www.example.com/schema#autoPersistingvalue")
        String getValue();

        @Iri("http://www.example.com/schema#autoPersistingvalue")
        void setValue(String value);
    }
}
