package com.github.anno4j.model.namespaces;

/**
 * Ontology class for the schema.org vocabulary.
 * @see <a href="http://schema.org/</a>
 */
public class SCHEMA {

    /**
     * Textual representation of the namespace.
     */
    public final static String NS = "http://schema.org/";

    /**
     * Textual prefix of the ontology.
     */
    public final static String PREFIX = "schema";

    /**
     * Refers to http://schema.org/Audience
     * Intended audience for an item, i.e. the group for whom the item was created.
     */
    public final static String AUDIENCE_CLASS = NS + "Audience";

    /**
     * Refers to http://schema.org/audience
     * An intended audience, i.e. a group for whom something was created.
     */
    public final static String AUDIENCE_RELATIONSHIP = NS + "audience";
}
