package com.github.anno4j.model.namespaces;

/**
 * Ontology class for the SKOS vocabulary.
 * @see <a href="https://www.w3.org/TR/skos-reference/</a>
 */
public class SKOS {

    /**
     * Textual representation of the namespace.
     */
    public final static String NS = "http://www.w3.org/2004/02/skos/core#";

    /**
     * Textual prefix of the ontology.
     */
    public final static String PREFIX = "skos";

    /**
     * Refers to http://www.w3.org/2004/02/skos/core#notation
     * A notation is a string of characters such as "T58.5" or "303.4833" used to uniquely identify a concept within
     * the scope of a given concept scheme.

     A notation is different from a lexical label in that a notation is not normally recognizable as a word or sequence
     of words in any natural language.
     */
    public final static String NOTATION = NS + "notation";

}
