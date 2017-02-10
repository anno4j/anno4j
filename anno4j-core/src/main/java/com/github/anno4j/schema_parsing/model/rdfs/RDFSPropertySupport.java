package com.github.anno4j.schema_parsing.model.rdfs;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Support class for the RDFSProperty interface.
 */
@Partial
public abstract class RDFSPropertySupport extends RDFSSchemaResourceSupport implements RDFSProperty {

    @Override
    public void addRangeClazz(RDFSClazz clazz) {
        Set<RDFSClazz> range = new HashSet<>();

        if (getRange() != null) {
            range.addAll(getRange());
        }

        range.add(clazz);
        setRange(range);
    }

    @Override
    public void addDomainClazz(RDFSClazz clazz) {
        Set<RDFSClazz> domain = new HashSet<>();

        if(getDomain() != null) {
            domain.addAll(getDomain());
        }

        domain.add(clazz);
        setDomain(domain);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSubProperty(RDFSProperty subProperty) {
        Set<RDFSProperty> subProperties = new HashSet<RDFSProperty>();

        if(this.getSubProperties() != null) {
            subProperties.addAll(this.getSubProperties());
        }

        subProperties.add(subProperty);
        this.setSubProperties(subProperties);
    }
}
