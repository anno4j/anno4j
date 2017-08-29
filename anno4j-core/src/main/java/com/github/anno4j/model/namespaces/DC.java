package com.github.anno4j.model.namespaces;

/**
 * Ontology class for the Dublin Core ontology (dc:).
 * See <a href="http://dublincore.org/documents/dcmi-terms/">http://dublincore.org/documents/dcmi-terms/</a>
 */
public class DC {

    /**
     * Textual representation of the namespace.
     */
    public final static String NS = "http://purl.org/dc/elements/1.1/";

    /**
     * Textual prefix of the ontology.
     */
    public final static String PREFIX = "dc";

    /**
     * Refers to http://dublincore.org/documents/dcmi-terms/#terms-format
     * The file format, physical medium, or dimensions of the resource.
     */
    public final static String FORMAT = NS + "format";

    /**
     * Refers to http://dublincore.org/documents/dcmi-terms/#terms-language
     * A language of the resource.
     */
    public final static String LANGUAGE = NS + "language";
}
