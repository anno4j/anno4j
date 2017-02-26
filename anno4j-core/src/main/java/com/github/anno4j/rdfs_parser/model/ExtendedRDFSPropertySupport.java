package com.github.anno4j.rdfs_parser.model;

import com.github.anno4j.annotations.Partial;

import java.util.HashSet;
import java.util.Set;


@Partial
public abstract class ExtendedRDFSPropertySupport extends RDFSPropertySupport implements ExtendedRDFSProperty {

    private Set<ExtendedRDFSProperty> superProperties = new HashSet<>();

    @Override
    public void addDomainClazz(ExtendedRDFSClazz clazz) {
        addDomainClazz(clazz, true);
    }

    @Override
    public void addDomainClazz(ExtendedRDFSClazz clazz, boolean updateInverse) {
        if(updateInverse) {
            clazz.addOutgoingProperty(this, false);
        }
        super.addDomain(clazz);
    }

    @Override
    public void addRangeClazz(ExtendedRDFSClazz clazz) {
        addRangeClazz(clazz, true);
    }

    @Override
    public void addRangeClazz(ExtendedRDFSClazz clazz, boolean updateInverse) {
        if(updateInverse) {
            clazz.addIncomingProperty(this, false);
        }
        super.addRange(clazz);
    }

    @Override
    public Set<ExtendedRDFSProperty> getSuperproperties() {
        return superProperties;
    }

    @Override
    public void addSuperproperty(ExtendedRDFSProperty superProperty) {
        addSuperproperty(superProperty, true);
    }

    @Override
    public void addSuperproperty(ExtendedRDFSProperty superProperty, boolean updateInverse) {
        if(updateInverse) {
            superProperty.addSubProperty(this);
        }
        superProperties.add(superProperty);
    }
}