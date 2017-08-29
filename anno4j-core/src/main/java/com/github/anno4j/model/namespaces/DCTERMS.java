package com.github.anno4j.model.namespaces;

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

    public final static String FORMAT = NS + "format";

    /**
     * Refers to http://purl.org/dc/terms/creator
     */
    public final static String CREATOR = NS + "creator";

    /**
     * Refers to http://purl.org/dc/terms/rights
     */
    public final static String RIGHTS = NS + "rights";

    /**
     * Refers to http://purl.org/dc/terms/created
     */
    public final static String CREATED = NS + "created";

    /**
     * Refers to http://purl.org/dc/terms/modified
     */
    public final static String MODIFIED = NS + "modified";

    /**
     * Refers to http://purl.org/dc/terms/issued
     */
    public final static String ISSUED = NS + "issued";
}
