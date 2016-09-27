package com.github.anno4j.mico.test;

import com.github.anno4j.Anno4j;
import com.github.anno4j.mico.model.AnimalDetectionBody;
import com.github.anno4j.mico.model.PartMMM;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.repository.RepositoryException;

import static org.junit.Assert.*;

/**
 * Test suite for the AnimalDetectionBody.
 */
public class AnimalDetectionBodyTest {

    private Anno4j anno4j;

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
    }

    @Test
    public void testConfidence() throws RepositoryException, IllegalAccessException, InstantiationException {
        PartMMM part = this.anno4j.createObject(PartMMM.class);

        AnimalDetectionBody body = this.anno4j.createObject(AnimalDetectionBody.class);

        part.setBody(body);

        body.setConfidence(-1.0);

        assertEquals(0.0, body.getConfidence(), 0.0);

//        body.setConfidence(1.5);
//
//        assertEquals(1.0, body.getConfidence(), 0.0);
//
//        body.setConfidence(0.7);
//
//        assertEquals(0.7, body.getConfidence(), 0.0);
    }

}