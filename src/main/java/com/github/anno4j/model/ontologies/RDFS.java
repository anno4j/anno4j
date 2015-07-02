package com.github.anno4j.model.ontologies;

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
}
