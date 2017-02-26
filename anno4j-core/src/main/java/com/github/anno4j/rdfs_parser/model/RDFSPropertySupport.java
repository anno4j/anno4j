package com.github.anno4j.rdfs_parser.model;

import com.github.anno4j.annotations.Partial;

import java.util.HashSet;
import java.util.Set;

/**
 * Support class for the RDFSProperty interface.
 */
@Partial
public abstract class RDFSPropertySupport extends RDFSSchemaResourceSupport implements RDFSProperty {

    @Override
    public void addRange(RDFSClazz clazz) {
        Set<RDFSClazz> range = new HashSet<>();

        if (getRanges() != null) {
            range.addAll(getRanges());
        }

        range.add(clazz);
        setRanges(range);
    }

    @Override
    public void addDomain(RDFSClazz clazz) {
        Set<RDFSClazz> domain = new HashSet<>();

        if(getDomains() != null) {
            domain.addAll(getDomains());
        }

        domain.add(clazz);
        setDomains(domain);
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
