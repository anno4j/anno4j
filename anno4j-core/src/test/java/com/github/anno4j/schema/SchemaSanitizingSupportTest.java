package com.github.anno4j.schema;

import com.github.anno4j.Anno4j;
import com.github.anno4j.schema.model.SanitizingTestResource;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link SchemaSanitizingSupport}.
 */
public class SchemaSanitizingSupportTest {

    private Anno4j anno4j;

    @Before
    public void setUp() throws Exception {
        anno4j = new Anno4j();
    }

    @Test
    public void testSuperpropertySanitizing() throws Exception {
        SanitizingTestResource resource = anno4j.createObject(SanitizingTestResource.class);

        resource.setSuperproperty(Sets.newHashSet(3));
        assertEquals(Sets.newHashSet(3), resource.getSuperproperty());
        assertEquals(Sets.<Integer>newHashSet(), resource.getSubproperty());

        resource.setSubproperty(Sets.newHashSet(1, 2));
        resource.sanitizeSchema();
        assertEquals(Sets.newHashSet(1, 2, 3), resource.getSuperproperty());
        assertEquals(Sets.newHashSet(1, 2), resource.getSubproperty());
    }

    @Test
    public void testSymmetrySanitizing() throws Exception {
        SanitizingTestResource resource1 = anno4j.createObject(SanitizingTestResource.class);
        SanitizingTestResource resource2 = anno4j.createObject(SanitizingTestResource.class);

        resource1.setSymmetric(Sets.newHashSet(resource2));
        resource1.sanitizeSchema();

        assertEquals(Sets.newHashSet(resource2), resource1.getSymmetric());
        assertEquals(Sets.newHashSet(resource1), resource2.getSymmetric());
    }

    @Test
    public void testTransitivitySanitizing() throws Exception {
        SanitizingTestResource resource1 = anno4j.createObject(SanitizingTestResource.class);
        SanitizingTestResource resource2 = anno4j.createObject(SanitizingTestResource.class);
        SanitizingTestResource resource3 = anno4j.createObject(SanitizingTestResource.class);

        // Important: Must be in this order because sanitizing is local by specification:
        resource2.setTransitive(Sets.newHashSet(resource3));
        resource1.setTransitive(Sets.newHashSet(resource2));
        resource1.sanitizeSchema();

        assertEquals(Sets.newHashSet(resource2, resource3), resource1.getTransitive());
    }

    @Test
    public void testInversePropertySanitizing() throws Exception {
        SanitizingTestResource resource1 = anno4j.createObject(SanitizingTestResource.class);
        SanitizingTestResource resource2 = anno4j.createObject(SanitizingTestResource.class);

        resource1.setInverse1(Sets.newHashSet(resource2));
        assertTrue(resource2.getInverse2().contains(resource1));
    }
}