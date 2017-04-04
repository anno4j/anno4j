package com.github.anno4j.schema_parsing.model;

import com.github.anno4j.annotations.Partial;

import java.util.HashSet;
import java.util.Set;


@Partial
public abstract class ExtendedRDFSPropertySupport extends RDFSPropertySupport implements ExtendedRDFSProperty {

    /**
     * The direct superproperties of this property, i.e. the objects of statements
     * with this property as subject and a rdfs:subPropertyOf predicate.
     */
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

    @Override
    public Set<ExtendedRDFSProperty> getSubpropertyClosure() {
        Set<ExtendedRDFSProperty> closure = new HashSet<>();

        for (RDFSProperty subProperty : getSubProperties()) {
            if(subProperty instanceof ExtendedRDFSProperty) {
                // Add the subproperty:
                closure.add((ExtendedRDFSProperty) subProperty);
                // Recursively add all its subproperties:
                closure.addAll(((ExtendedRDFSProperty) subProperty).getSubpropertyClosure());
            }
        }

        return closure;
    }
}