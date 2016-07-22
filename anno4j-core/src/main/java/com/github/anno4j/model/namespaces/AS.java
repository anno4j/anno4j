package com.github.anno4j.model.namespaces;

/**
 * Ontology class for the activity streams vocabulary.
 * @see <a href="https://www.w3.org/TR/activitystreams-vocabulary/</a>
 */
public class AS {

    /**
     * Textual representation of the namespace.
     */
    public final static String NS = "http://www.w3.org/ns/activitystreams#";

    /**
     * Textual prefix of the ontology.
     */
    public final static String PREFIX = "as";

    /**
     * Refers to http://www.w3.org/ns/activitystreams#Application
     * Describes a software application.
     */
    public final static String APPLICATION = NS + "Application";

    /**
     * Refers to http://www.w3.org/ns/activitystreams#OrderedCollection
     * A subclass of Collection in which members of the logical collection are assumed to always be strictly ordered.
     */
    public final static String ORDERED_COLLECTION = NS + "OrderedCollection";

    /**
     * Refers to http://www.w3.org/ns/activitystreams#OrderedCollectionPage
     * Used to represent ordered subsets of items from an OrderedCollection. Refer to the Activity Streams 2.0 Core for
     * a complete description of the OrderedCollectionPage object.
     */
    public final static String ORDERED_COLLECTION_PAGE = NS + "OrderedCollectionPage";

    /**
     * Refers to http://www.w3.org/ns/activitystreams#generator
     */
    public final static String GENERATOR = NS + "generator";

    /**
     * Refers to http://www.w3.org/ns/activitystreams#items
     */
    public final static String ITEMS = NS + "items";

    /**
     * Refers to http://www.w3.org/ns/activitystreams#partOf
     */
    public final static String PART_OF = NS + "partOf";

    /**
     * Refers to http://www.w3.org/ns/activitystreams#first
     */
    public final static String FIRST = NS + "first";

    /**
     * Refers to http://www.w3.org/ns/activitystreams#last
     */
    public final static String LAST = NS + "last";

    /**
     * Refers to http://www.w3.org/ns/activitystreams#next
     */
    public final static String NEXT = NS + "next";

    /**
     * Refers to http://www.w3.org/ns/activitystreams#prev
     */
    public final static String PREV = NS + "prev";

    /**
     * Refers to http://www.w3.org/ns/activitystreams#totalItems
     */
    public final static String TOTAL_ITEMS = NS + "totelItems";

    /**
     * Refers to http://www.w3.org/ns/activitystreams#startIndex
     */
    public final static String START_INDEX = NS + "startIndex";
}
