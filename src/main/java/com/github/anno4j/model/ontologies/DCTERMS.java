package com.github.anno4j.model.ontologies;

/**
 * Ontology class for the Dublin Core Terms ontology (dcterms:).
 * See <a href="http://dublincore.org/documents/dcmi-terms/">http://dublincore.org/documents/dcmi-terms/</a>
 */
public class DCTERMS {

    /**
     * Textual representation of the namespace.
     */
    public final static String NS = "http://purl.org/dc/terms/";

    /**
     * Textual prefix of the ontology.
     */
    public final static String PREFIX = "dcterms";

    /**
     * Refers to http://dublincore.org/documents/dcmi-terms/#terms-conformsTo
     * An established standard to which the described resource conforms.
     */
    public final static String CONFORMS_TO = NS + "conformsTo";
}
