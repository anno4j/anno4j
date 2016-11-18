package com.github.anno4j.model.namespaces;

/**
 * Namespace class containing various classes, relationships, and properties associated with the OWL specification.
 */
public class OWL {

    public final static String NS = "http://www.w3.org/2002/07/owl#";

    public final static String PREFIX = "owl";

    /**
     * Refers to http://www.w3.org/2002/07/owl#Class
     * A class defines a group of individuals that belong together because they share some properties.
     */
    public final static String CLAZZ = NS + "Class";

    /**
     * Refers to http://www.w3.org/2002/07/owl#Restriction
     * A restriction can support limitations and boundaries for properties.
     */
    public final static String RESTRICTION = NS + "Restriction";

    /**
     * Refers to http://www.w3.org/2002/07/owl#ObjectProperty
     * A property is a binary relation.
     */
    public final static String OBJECT_PROPERTY = NS + "ObjectProperty";

    /**
     * Refers to http://www.w3.org/2002/07/owl#InverseFunctionalProperty
     * Properties may be stated to be inverse functional. If a property is inverse functional then the inverse of the
     * property is functional. Thus the inverse of the property has at most one value for each individual. This
     * characteristic has also been referred to as an unambiguous property.
     */
    public final static String INVERSE_FUNCTIONAL_PROPERTY = NS + "InverseFunctionalProperty";

    /**
     * Refers to http://www.w3.org/2002/07/owl#FunctionalProperty
     * Properties may be stated to have a unique value. If a property is a FunctionalProperty, then it has no more than
     * one value for each individual (it may have no values for an individual). This characteristic has been referred
     * to as having a unique property. FunctionalProperty is shorthand for stating that the property's minimum
     * cardinality is zero and its maximum cardinality is 1.
     */
    public final static String FUNCTIONAL_PROPERTY = NS + "FunctionalProperty";

    /**
     * Refers to http://www.w3.org/2002/07/owl#SymmetricProperty
     * Properties may be stated to be symmetric. If a property is symmetric, then if the pair (x,y) is an instance of
     * the symmetric property P, then the pair (y,x) is also an instance of P.
     */
    public final static String SYMMETRIC_PROPERTY = NS + "SymmetricProperty";

    /**
     * Refers to http://www.w3.org/2002/07/owl#TransitiveProperty
     * Properties may be stated to be transitive. If a property is transitive, then if the pair (x,y) is an instance of
     * the transitive property P, and the pair (y,z) is an instance of P, then the pair (x,z) is also an instance of P.
     */
    public final static String TRANSITIVE_PROPERTY = NS + "TransitiveProperty";

    /**
     * Refers to http://www.w3.org/2002/07/owl#DatatypeProperty
     * A property linking to a value, rather than another RDF node.
     */
    public final static String DATATYPE_PROPERTY = NS + "DatatypeProperty";

    /**
     * Refers to http://www.w3.org/2002/07/owl#inverseOf
     * One property may be stated to be the inverse of another property. If the property P1 is stated to be the inverse
     * of the property P2, then if X is related to Y by the P2 property, then Y is related to X by the P1 property.
     */
    public final static String INVERSE_OF = NS + "inverseOf";

    /**
     * Refers to http://www.w3.org/2002/07/owl#onProperty
     * Associated with a owl:Restriction, indicating on which property the Restriction is targeted at.
     */
    public final static String ON_PROPERTY = NS + "onProperty";

    /**
     * Refers to http://www.w3.org/2002/07/owl#someValuesFrom
     * The restriction someValuesFrom is stated on a property with respect to a class. A particular class may have a
     * restriction on a property that at least one value for that property is of a certain type.
     */
    public final static String SOME_VALUES_FROM = NS + "someValuesFrom";

    /**
     * Refers to http://www.w3.org/2002/07/owl#minCardinality
     * Cardinality is stated on a property with respect to a particular class. If a minCardinality of 1 is stated on a
     * property with respect to a class, then any instance of that class will be related to at least one individual by
     * that property. This restriction is another way of saying that the property is required to have a value for all
     * instances of the class.
     */
    public final static String MIN_CARDINALITY = NS + "minCardinality";

    /**
     * Refers to http://www.w3.org/2002/07/owl#maxCardinality
     * Cardinality is stated on a property with respect to a particular class. If a maxCardinality of 1 is stated on a
     * property with respect to a class, then any instance of that class will be related to at most one individual by
     * that property. A maxCardinality 1 restriction is sometimes called a functional or unique property.
     */
    public final static String MAX_CARDINALITY = NS + "maxCardinality";
}
