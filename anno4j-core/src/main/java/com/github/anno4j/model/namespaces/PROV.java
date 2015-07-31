package com.github.anno4j.model.namespaces;

/**
 * Ontology class for the PROV ontology (prov:).
 * See <a href="http://www.w3.org/TR/prov-o/">http://www.w3.org/TR/prov-o/</a>
 */
public class PROV {

    /**
     * Textual representation of the namespace.
     */
    public final static String NS = "http://www.w3.org/ns/prov/";

    /**
     * Textual prefix of the ontology.
     */
    public final static String PREFIX = "prov";

    /**
     * Refers to http://www.w3.org/TR/prov-o/#Agent
     * An agent is something that bears some form of responsibility for an activity taking place, for the existence of an entity, or for another agent's activity.
     */
    public final static String AGENT = NS + "Agent";

    /**
     * Refers to http://www.w3.org/TR/prov-o/#SoftwareAgent
     * A software agent is running software.
     */
    public final static String SOFTWARE_AGENT = NS + "SoftwareAgent";

    /**
     * Refers to http://www.w3.org/TR/prov-o/#generatedAtTime
     * Generation is the completion of production of a new entity by an activity. This entity did not exist before generation and becomes available for usage after this generation.
     */
    public final static String GENERATED_AT_TIME = NS + "generatedAtTime";

    /**
     * Refers to http://www.w3.org/TR/prov-o/#wasGeneratedBy
     * Generation is the completion of production of a new entity by an activity. This entity did not exist before generation and becomes available for usage after this generation.
     */
    public final static String WAS_GENERATED_BY = NS + "wasGeneratedBy";

    /**
     * Refers to http://www.w3.org/TR/prov-o/#invalidatedAtTime
     * Invalidation is the start of the destruction, cessation, or expiry of an existing entity by an activity. The entity is no longer available for use (or further invalidation) after invalidation. Any generation or usage of an entity precedes its invalidation.
     */
    public final static String INVALIDATED_AT_TIME = NS + "invalidatedAtTime";
}
