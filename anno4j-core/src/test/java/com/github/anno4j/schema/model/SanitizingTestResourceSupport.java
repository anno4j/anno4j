package com.github.anno4j.schema.model;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.schema.SchemaSanitizingSupport;

import java.util.Set;

/**
 * Created by fischmat on 20.06.17.
 */
@Partial
public abstract class SanitizingTestResourceSupport extends SchemaSanitizingSupport implements SanitizingTestResource {

    @Override
    public void setSuperproperty(Set<Integer> values) {
        sanitizeSchema();
    }

    @Override
    public void setSubproperty(Set<Integer> values) {
        sanitizeSchema();
    }

    @Override
    public void setSymmetric(Set<SanitizingTestResource> values) {
        sanitizeSchema();
    }

    @Override
    public void setTransitive(Set<SanitizingTestResource> values) {
        sanitizeSchema();
    }

    @Override
    public void setInverse1(Set<SanitizingTestResource> values) {
        sanitizeSchema();
    }

    @Override
    public void setInverse2(Set<SanitizingTestResource> values) {
        sanitizeSchema();
    }
}
