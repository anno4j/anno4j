package com.github.anno4j.model.impl.body;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Motivation;
import com.github.anno4j.model.MotivationFactory;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.repository.RepositoryException;

import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * Test suite for the TextualBody interface.
 */
public class TextualBodyTest {

    private Anno4j anno4j;

    private final static String VALUE = "testvalue";

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
    }

    @Test
    public void testTextualBody() throws RepositoryException, IllegalAccessException, InstantiationException {
        Annotation annotation = this.anno4j.createObject(Annotation.class);

        TextualBody body = this.anno4j.createObject(TextualBody.class);
        body.setValue(VALUE);

        annotation.setBody(body);

        Annotation result = this.anno4j.findByID(Annotation.class, annotation.getResourceAsString());

        assertEquals(VALUE, ((TextualBody) result.getBody()).getValue());
        assertEquals(0, ((TextualBody) result.getBody()).getPurposes().size());

        body.addPurpose(MotivationFactory.getAssessing(this.anno4j));

        result = this.anno4j.findByID(Annotation.class, annotation.getResourceAsString());
        assertEquals(1, ((TextualBody) result.getBody()).getPurposes().size());

        HashSet<Motivation> purposes = new HashSet<>();
        purposes.add(MotivationFactory.getBookmarking(this.anno4j));
        purposes.add(MotivationFactory.getClassifying(this.anno4j));

        body.setPurposes(purposes);

        result = this.anno4j.findByID(Annotation.class, annotation.getResourceAsString());
        assertEquals(2, ((TextualBody) result.getBody()).getPurposes().size());
    }
}