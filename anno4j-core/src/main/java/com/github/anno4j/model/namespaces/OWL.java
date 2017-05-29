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
     * Refers to http://www.w3.org/2002/07/owl#Thing
     * The class extension of owl:Thing is the set of all individuals.
     */
    public final static String THING = NS + "Thing";

    /**
     * Refers to http://www.w3.org/2002/07/owl#Nothing
     * The class extension of owl:Nothing is the empty set.
     */
    public final static String NOTHING = NS + "Nothing";

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
     * Refers to https://www.w3.org/2002/07/owl#onClass
     * The property that determines the class that a qualified object cardinality restriction refers to.
     */
    public final static String ON_CLAZZ = NS + "onClass";

    /**
     * Refers to https://www.w3.org/TR/owl-guide/#owl_allValuesFrom
     * The restriction allValuesFrom is stated on a property with respect to a class. It means that this property on
     * this particular class has a local range restriction associated with it.
     * Thus if an instance of the class is related by the property to a second individual, then the second individual
     * can be inferred to be an instance of the local range restriction class.
     */
    public final static String ALL_VALUES_FROM = NS + "allValuesFrom";

    /**
     * Refers to http://www.w3.org/2002/07/owl#someValuesFrom
     * The restriction someValuesFrom is stated on a property with respect to a class. A particular class may have a
     * restriction on a property that at least one value for that property is of a certain type.
     */
    public final static String SOME_VALUES_FROM = NS + "someValuesFrom";

    /**
     * Refers to https://www.w3.org/TR/owl-guide/#owl_minCardinality
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

    /**
     * Refers to http://www.w3.org/2002/07/owl#cardinality
     * Cardinality is provided as a convenience when it is useful to state that a property on a class has
     * both minCardinality 0 and maxCardinality 0 or both minCardinality 1 and maxCardinality 1.
     */
    public final static String CARDINALITY = NS + "cardinality";

    /**
     * Refers to http://www.w3.org/2002/07/owl#hasValue
     * hasValue allows us to specify classes based on the existence of particular property values.
     * Hence, an individual will be a member of such a class whenever at least one of its property values is equal to
     * the hasValue resource.
     */
    public final static String HAS_VALUE = NS + "hasValue";

    /**
     * Refers to http://www.w3.org/2002/07/owl#equivalentClass
     * owl:equivalentClass is a built-in property that links a class description to another class description.
     */
    public final static String EQUIVALENT_CLASS = NS + "equivalentClass";

    /**
     * Refers to http://www.w3.org/2002/07/owl#disjointWith
     * Each owl:disjointWith statement asserts that the class extensions of the two class descriptions involved have
     * no individuals in common.
     */
    public final static String DISJOINT_WITH = NS + "disjointWith";

    /**
     * Refers to http://www.w3.org/2002/07/owl#complementOf
     * owl:complementOf is analogous to logical negation: the class extension consists of those individuals that are
     * NOT members of the class extension of the complement class.
     */
    public final static String COMPLEMENT_OF = NS + "complementOf";
}
