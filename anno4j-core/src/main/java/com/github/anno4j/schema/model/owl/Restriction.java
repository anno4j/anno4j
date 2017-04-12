package com.github.anno4j.schema.model.owl;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.OWL;
import com.github.anno4j.schema.model.rdfs.RDFSProperty;
import org.openrdf.annotations.Iri;

import java.util.Set;

/**
 * A restriction can support limitations and boundaries for properties.
 */
@Iri(OWL.RESTRICTION)
public interface Restriction extends ResourceObject {

    @Iri(OWL.ON_PROPERTY)
    Set<RDFSProperty> getOnProperty();

    @Iri(OWL.ON_PROPERTY)
    void setOnProperty(Set<RDFSProperty> properties);

    @Iri(OWL.ON_CLAZZ)
    Set<OWLClazz> getOnClazz();

    @Iri(OWL.ON_CLAZZ)
    void setOnClazz(Set<OWLClazz> clazzes);

    @Iri(OWL.MIN_CARDINALITY)
    Set<Integer> getMinCardinality();

    @Iri(OWL.MIN_CARDINALITY)
    void setMinCardinality(Set<Integer> minCardinality);

    @Iri(OWL.MAX_CARDINALITY)
    Set<Integer> getMaxCardinality();

    @Iri(OWL.MAX_CARDINALITY)
    void setMaxCardinality(Set<Integer> maxCardinality);

    @Iri(OWL.CARDINALITY)
    Set<Integer> getCardinality();

    @Iri(OWL.CARDINALITY)
    void setCardinality(Set<Integer> cardinality);

    @Iri(OWL.ALL_VALUES_FROM)
    Set<OWLClazz> getAllValuesFrom();

    @Iri(OWL.ALL_VALUES_FROM)
    void setAllValuesFrom(Set<OWLClazz> clazzes);

    @Iri(OWL.SOME_VALUES_FROM)
    Set<OWLClazz> getSomeValuesFrom();

    @Iri(OWL.SOME_VALUES_FROM)
    void setSomeValuesFrom(Set<OWLClazz> clazzes);
}
