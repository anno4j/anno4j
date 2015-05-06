package com.github.anno4j.model.ontologies;

/**
 * Ontology class for the Open Annotation Data Model (oa:).
 */
public class OADM {

    public final static String NS = "http://www.w3.org/ns/oa#";

    public final static String PREFIX = "oa:";

    /**
     * Classes
     */
    public final static String ANNOTATION = NS + "Annotation";

    public final static String TAG = NS + "Tag";

    public final static String SEMANTIC_TAG = NS + "SemanticTag";

    public final static String SPECIFIC_RESOURCE = NS + "SpecificResource";

    // Motivation
    public final static String MOTIVATION = NS + "Motivation";

    public final static String MOTIVATION_BOOKMARKING = NS + "bookmarking";

    public final static String MOTIVATION_CLASSIFYING = NS + "classifying";

    public final static String MOTIVATION_COMMENTING = NS + "commenting";

    public final static String MOTIVATION_DESCRIBING = NS + "describing";

    public final static String MOTIVATION_EDITING = NS + "editing";

    public final static String MOTIVATION_HIGHLIGHTING = NS + "highlighting";

    public final static String MOTIVATION_IDENTIFYING = NS + "identifying";

    public final static String MOTIVATION_LINKING = NS + "linking";

    public final static String MOTIVATION_MODERATING = NS + "moderating";

    public final static String MOTIVATION_QUESTIONING = NS + "questioning";

    public final static String MOTIVATION_REPLYING = NS + "replying";

    public final static String MOTIVATION_TAGGING = NS + "tagging";

    // Selector
    public final static String SELECTOR = NS + "Selector";

    public final static String SELECTOR_FRAGMENT = NS + "FragmentSelector";

    public final static String SELECTOR_SVG = NS + "SvgSelector";

    public final static String SELECTOR_DATA_POSITION = NS + "DataPositionSelector";

    public final static String SELECTOR_TEXT_POSITION = NS + "TextPositionSelector";

    public final static String SELECTOR_TEXT_QUOTE = NS + "TextQuoteSelector";

    /**
     * Relationships
     */
    public final static String HAS_BODY = NS + "hasBody";

    public final static String HAS_TARGET = NS + "hasTarget";

    public final static String ANNOTATED_BY = NS + "annotatedBy";

    public final static String SERIALIZED_BY = NS + "serializedBy";

    public final static String HAS_SELECTOR = NS + "hasSelector";

    public final static String HAS_SOURCE = NS + "hasSource";

    public final static String HAS_SCOPE = NS + "hasScope";

    public final static String MOTIVATED_BY = NS + "motivatedBy";

    /**
     * Data Properties
     */
    public final static String SERIALIZED_AT = NS + "serializedAt";

    public final static String ANNOTATED_AT = NS + "annotatedAt";

    public final static String END = NS + "end";

    public final static String EXACT = NS + "exact";

    public final static String START = NS + "start";

    public final static String TEXT_PREFIX = NS + "prefix";

    public final static String SUFFIX = NS + "suffix";

    public final static String WHEN = NS + "when";
}
