package com.github.anno4j.model.impl;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.idGenerator.IDGenerator;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by schlegel on 05/10/15.
 */
public class ResourceObjectTest {

    private Anno4j anno4j;
    private ObjectConnection connection;

    @Before
       public void setUp() throws Exception {
        this.anno4j = new Anno4j();
        this.connection = this.anno4j.getObjectRepository().getConnection();
    }

    @After
    public void tearDown() throws Exception {
        connection.close();
    }

    @Test
    public void testSetResourceAsString() throws Exception {
        ResourceObject resourceObject = anno4j.createObject(ResourceObject.class);
        resourceObject.setResourceAsString("http://www.somepage.org/resource1/");
        connection.addObject(resourceObject);

        ResourceObject resourceObject1 = (ResourceObject) connection.getObject(resourceObject.getResource());
        assertEquals("http://www.somepage.org/resource1/", resourceObject1.getResourceAsString());
    }

    @Test
    public void testAutomaticResourceNaming() throws RepositoryException, InstantiationException, IllegalAccessException {
        ResourceObject resourceObject = anno4j.createObject(ResourceObject.class);
        connection.addObject(resourceObject);
        assertNotEquals(IDGenerator.BLANK_RESOURCE, resourceObject.getResource());

        ResourceObject resourceResult = (ResourceObject) connection.getObject(resourceObject.getResource());
        assertEquals(resourceObject.getResourceAsString(), resourceResult.getResourceAsString());
    }

    @Test
    public void testGetNTriples() throws Exception {
        Annotation annotation = anno4j.createObject(Annotation.class);
        annotation.setAnnotatedAt("" + System.currentTimeMillis());
        annotation.setSerializedAt("" + System.currentTimeMillis());

        String output = annotation.getNTriples();

        assertTrue(output.contains("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/oa#Annotation>"));
        assertTrue(output.contains(" <http://www.w3.org/ns/oa#annotatedAt> "));
        assertTrue(output.contains(" <http://www.w3.org/ns/oa#serializedAt> "));
    }
}