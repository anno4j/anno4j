package com.github.anno4j.model.ontologies;

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
}
