package com.github.anno4j.schema.model.owl;

import com.github.anno4j.model.impl.ResourceObjectSupport;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.github.anno4j.schema.model.rdfs.RDFSProperty;

import java.util.Set;

/**
 * Support class for {@link Restriction} implementing validity checks.
 */
public abstract class RestrictionSupport extends ResourceObjectSupport implements Restriction {

    @Override
    public void setOnProperty(Set<RDFSProperty> properties) {
        // Restrictions can only be defined on a single property:
        if(properties == null || properties.size() != 1) {
            throw new IllegalArgumentException("Restrictions can only be defined on a single property. " +
                                                "Use multiple restrictions for multiple properties.");
        }
    }

    @Override
    public void setOnClazz(Set<OWLClazz> clazzes) {
        // Restrictions can only be defined on a single class:
        if(clazzes == null || clazzes.size() != 1) {
            throw new IllegalArgumentException("Restrictions can only be defined on a single class. " +
                    "Use multiple restrictions for multiple classes.");
        }
    }

    @Override
    public void setMinCardinality(Set<Integer> minCardinality) {
        // There can only be one constraint for the minimum cardinality:
        if(minCardinality == null || minCardinality.size() != 1) {
            throw new IllegalArgumentException("There can be at most one owl:minCardinality constraint per restriction.");
        }
        // Also the cardinality must be non-negative:
        if(minCardinality.iterator().next() < 0) {
            throw new IllegalArgumentException("The minimum cardinality of a property must be non-negative.");
        }
    }

    @Override
    public void setMaxCardinality(Set<Integer> maxCardinality) {
        // There can only be one constraint for the maximum cardinality:
        if(maxCardinality == null || maxCardinality.size() != 1) {
            throw new IllegalArgumentException("There can be at most one owl:maxCardinality constraint per restriction.");
        }
        // Also the cardinality must be non-negative:
        if(maxCardinality.iterator().next() < 0) {
            throw new IllegalArgumentException("The maximum cardinality of a property must be non-negative.");
        }
    }

    @Override
    public void setCardinality(Set<Integer> cardinality) {
        // There can only be one constraint for the cardinality:
        if(cardinality == null || cardinality.size() != 1) {
            throw new IllegalArgumentException("There can be at most one owl:cardinality constraint per restriction.");
        }
        if(cardinality.iterator().next() < 0) {
            throw new IllegalArgumentException("The cardinality of a property must be non-negative.");
        }
    }

    @Override
    public void setHasValue(Set<Object> values) {
        for (Object value : values) {
            if(value instanceof RDFSClazz) {
                throw new IllegalArgumentException("The value of owl:hasValue must be an instance, not a class.");
            }
            if(value instanceof RDFSProperty) {
                throw new IllegalArgumentException("The value of owl:hasValue must be an instance, not a property.");
            }
        }
    }
}
