package com.github.anno4j.model.namespaces;

/**
 * Ontology class for the Resource Description Framework Schema (rdfs:)
 * See <a href="http://www.w3.org/TR/rdf-schema/">http://www.w3.org/TR/rdf-schema/</a>
 */
public class RDFS {

    /**
     * Namespace for rdfs:
     */
    public final static String NS = "http://www.w3.org/2000/01/rdf-schema#";

    /**
     * Refers to http://www.w3.org/TR/rdf-schema/#ch_resource
     * All things described by RDF are called resources, and are instances of the class rdfs:Resource.
     * This is the class of everything. All other classes are subclasses of this class. rdfs:Resource is an instance of rdfs:Class.
     */
    public final static String RESOURCE = NS + "Resource";

    /**
     * Refers to https://www.w3.org/TR/rdf-schema/#ch_class
     * This is the class of resources that are RDF classes. rdfs:Class is an instance of rdfs:Class.
     */
    public final static String CLAZZ = NS + "Class";

    /**
     * https://www.w3.org/TR/rdf-schema/#ch_label
     * rdfs:label is an instance of rdf:Property that may be used to provide a human-readable version of a resource's name.
     */
    public final static String LABEL = NS + "label";

    /**
     * Refers to https://www.w3.org/TR/rdf-schema/#ch_comment
     * rdfs:comment is an instance of rdf:Property that may be used to provide a human-readable description of a resource.
     */
    public final static String COMMENT = NS + "comment";

    /**
     * Refers to https://www.w3.org/TR/rdf-schema/#ch_subclassof
     * The property rdfs:subClassOf is an instance of rdf:Property that is used to state that all the instances of one class are instances of another.
     */
    public final static String SUB_CLASS_OF = NS + "subClassOf";

    /**
     * Refers to https://www.w3.org/TR/rdf-schema/#ch_subpropertyof
     * The property rdfs:subPropertyOf is an instance of rdf:Property that is used to state that all resources related by one property are also related by another.
     */
    public final static String SUB_PROPERTY_OF = NS + "subPropertyOf";

    /**
     * Refers to https://www.w3.org/TR/rdf-schema/#ch_range
     * rdfs:range is an instance of rdf:Property that is used to state that the values of a property are instances of one or more classes.
     */
    public final static String RANGE = NS + "range";

    /**
     * Refers to https://www.w3.org/TR/rdf-schema/#ch_domain
     * rdfs:domain is an instance of rdf:Property that is used to state that any resource that has a given property is an instance of one or more classes.
     */
    public final static String DOMAIN = NS + "domain";
}
