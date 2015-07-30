package com.github.anno4j.model.namespaces;

/**
 * Ontology class for the Representing Content in RDF ontology (cnt:).
 * See <a href="http://www.w3.org/TR/Content-in-RDF10/">http://www.w3.org/TR/Content-in-RDF10/</a>
 */
public class CNT {

    /**
     * Textual representation of the namespace.
     */
    public final static String NS = "http://www.w3.org/2011/content#";

    /**
     * Textual prefix of the ontology.
     */
    public final static String PREFIX = "cnt";

    /**
     * Refers to http://www.w3.org/TR/Content-in-RDF10/#ContentAsTextClass
     * The cnt:ContentAsText class is a subclass of the cnt:Content class for any type of textual content.
     */
    public final static String CONTENT_AS_TEXT = NS + "ContentAsText";

    /**
     * Refers to http://www.w3.org/TR/Content-in-RDF10/#charsProperty
     * The character sequence of the given content.
     */
    public final static String CHARS = NS + "chars";
}
