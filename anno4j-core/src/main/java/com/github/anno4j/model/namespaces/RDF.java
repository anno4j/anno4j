package com.github.anno4j.model.namespaces;

/**
 * Ontology Class for the Resource Description Framework (rdf:).
 * See <a href="http://www.w3.org/TR/rdf-schema/">http://www.w3.org/TR/rdf-schema/</a>
 */
public class RDF {

    /**
     * Textual representation of the namespace.
     */
    public final static String NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    /**
     * Textual prefix of the ontology.
     */
    public final static String PREFIX = "rdf";

    /**
     * Refers to http://www.w3.org/TR/rdf-schema/#ch_type
     * rdf:type is an instance of rdf:Property that is used to state that a resource is an instance of a class.
     */
    public final static String TYPE = NS + "type";

    /**
     * Refers to http://www.w3.org/TR/rdf-schema/#ch_value
     * rdf:value is an instance of rdf:Property that may be used in describing structured values. rdf:value has no meaning on its own.
     */
    public final static String VALUE = NS + "value";

    /**
     * Refers to http://www.w3.org/TR/rdf-schema/#ch_statement
     * rdf:Statement is an instance of rdfs:Class. It is intended to represent the class of RDF statements.
     * An RDF statement is the statement made by a token of an RDF triple. The subject of an RDF statement is the
     * instance of rdfs:Resource identified by the subject of the triple. The predicate of an RDF statement is the
     * instance of rdf:Property identified by the predicate of the triple. The object of an RDF statement is the
     * instance of rdfs:Resource identified by the object of the triple. rdf:Statement is in the domain of the
     * properties rdf:predicate, rdf:subject and rdf:object. Different individual rdf:Statement instances may have the
     * same values for their rdf:predicate, rdf:subject and rdf:object properties.
     */
    public final static String STATEMENT = NS + "Statement";

    /**
     * Refers to http://www.w3.org/TR/rdf-schema/#ch_subject
     * rdf:subject is an instance of rdf:Property that is used to state the subject of a statement.
     */
    public final static String SUBJECT = NS + "subject";

    /**
     * Refers to http://www.w3.org/TR/rdf-schema/#ch_predicate
     * rdf:predicate is an instance of rdf:Property that is used to state the predicate of a statement.
     */
    public final static String PREDICATE = NS + "predicate";

    /**
     * Refers to http://www.w3.org/TR/rdf-schema/#ch_object
     * rdf:object is an instance of rdf:Property that is used to state the object of a statement.
     */
    public final static String OBJECT = NS + "object";

    /**
     * Refers to https://www.w3.org/TR/rdf-schema/#ch_property
     * rdf:Property is the class of RDF properties. rdf:Property is an instance of rdfs:Class.
     */
    public final static String PROPERTY = NS + "Property";
}
