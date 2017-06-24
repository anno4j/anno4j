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

        resource.setSuperproperty(Sets.newHashSet(1));
        resource.setSubproperty(Sets.newHashSet(2));

        // Check that the values for the subproperty are also set for the superproperty:
        assertEquals(Sets.newHashSet(1, 2), resource.getSuperproperty());

        resource.setSuperproperty(Sets.newHashSet(3));

        // Now the superproperty should have only the new value and the subproperty should be cleared:
        assertEquals(Sets.newHashSet(3), resource.getSuperproperty());
        assertEquals(Sets.newHashSet(), resource.getSubproperty());

        // Check that subproperty keeps values that are also present in updated superproperty:
        resource.setSubproperty(Sets.newHashSet(4));
        assertEquals(Sets.newHashSet(3, 4), resource.getSuperproperty());
        assertEquals(Sets.newHashSet(4), resource.getSubproperty());

        resource.setSuperproperty(Sets.newHashSet(4, 5));
        assertEquals(Sets.newHashSet(4, 5), resource.getSuperproperty());
        assertEquals(Sets.newHashSet(4), resource.getSubproperty());
    }

    @Test
    public void testSymmetrySanitizing() throws Exception {
        SanitizingTestResource resource1 = anno4j.createObject(SanitizingTestResource.class);
        SanitizingTestResource resource2 = anno4j.createObject(SanitizingTestResource.class);

        resource1.setSymmetric(Sets.newHashSet(resource2));
        resource1.sanitizeSchema("urn:anno4j_test:sanitizing_transitive");

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
        resource1.sanitizeSchema("urn:anno4j_test:sanitizing_transitive");

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