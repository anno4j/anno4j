package com.github.anno4j.schema_parsing.model.owl;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.OWL;
import org.openrdf.annotations.Iri;

/**
 * Refers to http://www.w3.org/2002/07/owl#Restriction
 * A restriction can support limitations and boundaries for properties.
 */
@Iri(OWL.RESTRICTION)
public interface OWLRestriction extends ResourceObject {

    /**
     * Refers to http://www.w3.org/2002/07/owl#onProperty
     * Associated with a owl:Restriction, indicating on which property the Restriction is targeted at.
     */
    @Iri(OWL.ON_PROPERTY)
    void setOnProperty(OWLObjectProperty property);

    /**
     * Refers to http://www.w3.org/2002/07/owl#onProperty
     * Associated with a owl:Restriction, indicating on which property the Restriction is targeted at.
     */
    @Iri(OWL.ON_PROPERTY)
    OWLObjectProperty getOnProperty();

    /**
     * Refers to http://www.w3.org/2002/07/owl#someValuesFrom
     * The restriction someValuesFrom is stated on a property with respect to a class. A particular class may have a
     * restriction on a property that at least one value for that property is of a certain type.
     */
    @Iri(OWL.SOME_VALUES_FROM)
    void setSomeValuesFrom(OWLClazz clazz);

    /**
     * Refers to http://www.w3.org/2002/07/owl#someValuesFrom
     * The restriction someValuesFrom is stated on a property with respect to a class. A particular class may have a
     * restriction on a property that at least one value for that property is of a certain type.
     */
    @Iri(OWL.SOME_VALUES_FROM)
    OWLClazz getSomeValuesFrom();

    /**
     * Refers to http://www.w3.org/2002/07/owl#minCardinality
     * Cardinality is stated on a property with respect to a particular class. If a minCardinality of 1 is stated on a
     * property with respect to a class, then any instance of that class will be related to at least one individual by
     * that property. This restriction is another way of saying that the property is required to have a value for all
     * instances of the class.
     */
    @Iri(OWL.MIN_CARDINALITY)
    void setMinCardinality(int minCardinality);

    /**
     * Refers to http://www.w3.org/2002/07/owl#minCardinality
     * Cardinality is stated on a property with respect to a particular class. If a minCardinality of 1 is stated on a
     * property with respect to a class, then any instance of that class will be related to at least one individual by
     * that property. This restriction is another way of saying that the property is required to have a value for all
     * instances of the class.
     */
    @Iri(OWL.MIN_CARDINALITY)
    int getMinCardinality();

    /**
     * Refers to http://www.w3.org/2002/07/owl#maxCardinality
     * Cardinality is stated on a property with respect to a particular class. If a maxCardinality of 1 is stated on a
     * property with respect to a class, then any instance of that class will be related to at most one individual by
     * that property. A maxCardinality 1 restriction is sometimes called a functional or unique property.
     */
    @Iri(OWL.MAX_CARDINALITY)
    void setMaxCardinality(int maxCardinality);

    /**
     * Refers to http://www.w3.org/2002/07/owl#maxCardinality
     * Cardinality is stated on a property with respect to a particular class. If a maxCardinality of 1 is stated on a
     * property with respect to a class, then any instance of that class will be related to at most one individual by
     * that property. A maxCardinality 1 restriction is sometimes called a functional or unique property.
     */
    @Iri(OWL.MAX_CARDINALITY)
    int getMaxCardinality();
}
