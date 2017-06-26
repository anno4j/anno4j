package com.github.anno4j.schema.model;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.schema.SchemaSanitizingObjectSupport;

import java.util.Set;

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
}
