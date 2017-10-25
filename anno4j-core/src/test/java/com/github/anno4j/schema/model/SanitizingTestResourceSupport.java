package com.github.anno4j.schema.model;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.schema.SchemaSanitizingObjectSupport;
import com.github.anno4j.schema.SchemaSanitizingObjectSupportTest;
import com.google.common.collect.Sets;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by fischmat on 20.06.17.
 */
@Partial
public abstract class SanitizingTestResourceSupport extends SchemaSanitizingObjectSupport implements SanitizingTestResource {

    @Override
    public void setSuperproperty(Set<Integer> values) {
        sanitizeSchema("urn:anno4j_test:sanitizing_superprop");
    }

    @Override
    public void setSubproperty(Set<Integer> values) {
        sanitizeSchema("urn:anno4j_test:sanitizing_subprop");
    }

    @Override
    public void setObjectSuperproperty(Set<SanitizingTestResource> values) {
        sanitizeSchema("urn:anno4j_test:sanitizing_obj_superprop");
    }

    @Override
    public void setObjectSubproperty(Set<SanitizingTestResource> values) {
        sanitizeSchema("urn:anno4j_test:sanitizing_obj_subprop");
    }

    @Override
    public void setSymmetric(Set<SanitizingTestResource> values) {
        sanitizeSchema("urn:anno4j_test:sanitizing_symmetric");
    }

    @Override
    public void setTransitive(Set<SanitizingTestResource> values) {
        sanitizeSchema("urn:anno4j_test:sanitizing_transitive");
    }

    @Override
    public void setInverse1(Set<SanitizingTestResource> values) {
        sanitizeSchema("urn:anno4j_test:sanitizing_inverse1");
    }

    @Override
    public void setInverse2(Set<SanitizingTestResource> values) {
        sanitizeSchema("urn:anno4j_test:sanitizing_inverse2");
    }

    /**
     * This method tests value removal and belongs to
     * {@link SchemaSanitizingObjectSupportTest#testRemoveValue()}.
     */
    @Override
    public void testRemoveValue(SanitizingTestResource removeObject) {
        // Test with resource:
        removeValue("urn:anno4j_test:sanitizing_obj_superprop", removeObject);
        assertEquals(1, getObjectSuperproperty().size());
        assertFalse(getObjectSuperproperty().contains(removeObject));
        assertEquals(0, getObjectSubproperty().size());

        // Test with literal:
        removeValue("urn:anno4j_test:sanitizing_superprop", 2);
        assertEquals(Sets.newHashSet(1), getSuperproperty());
        assertTrue(getSubproperty().isEmpty());
    }
}
