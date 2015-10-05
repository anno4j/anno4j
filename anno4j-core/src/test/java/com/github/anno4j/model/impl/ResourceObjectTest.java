package com.github.anno4j.model.impl;

import com.github.anno4j.Anno4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.repository.object.ObjectConnection;

import static org.junit.Assert.assertEquals;

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
        assertEquals(resourceObject.getResourceAsString(), resourceObject1.getResourceAsString());

    }
}