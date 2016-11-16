package com.github.anno4j.schema_parsing.model.owl;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.OWL;
import org.openrdf.annotations.Iri;

/**
 * Created by Manu on 16/11/16.
 */
@Iri(OWL.RESTRICTION)
public interface OWLRestriction extends ResourceObject {

    @Iri(OWL.ON_PROPERTY)
    void setOnProperty(OWLObjectProperty property);

    @Iri(OWL.ON_PROPERTY)
    OWLObjectProperty getOnProperty();

    @Iri(OWL.SOME_VALUES_FROM)
    void setSomeValuesFrom(OWLClazz clazz);

    @Iri(OWL.SOME_VALUES_FROM)
    OWLClazz getSomeValuesFrom();

    @Iri(OWL.MIN_CARDINALITY)
    void setMinCardinality(int minCardinality);

    @Iri(OWL.MIN_CARDINALITY)
    int getMinCardinality();

    @Iri(OWL.MAX_CARDINALITY)
    void setMaxCardinality(int maxCardinality);

    @Iri(OWL.MAX_CARDINALITY)
    int getMaxCardinality();
}
