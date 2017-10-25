package com.github.anno4j.model.namespaces;

/**
 * Ontology class for the Dublin Core Types ontology (dctypes:).
 * See <a href="http://dublincore.org/documents/dcmi-terms/">http://dublincore.org/documents/dcmi-terms/</a>
 */
public class DCTYPES {

    /**
     * Textual representation of the namespace.
     */
    public final static String NS = "http://purl.org/dc/dcmitype/";

    /**
     * Textual prefix of the ontology.
     */
    public final static String PREFIX = "dctypes";

    /**
     * Refers to http://dublincore.org/documents/dcmi-terms/#dcmitype-Dataset
     * Data encoded in a defined structure.
     */
    public final static String DATASET = NS + "Dataset";

    /**
     * Refers to http://dublincore.org/documents/dcmi-terms/#dcmitype-Image
     * A visual representation other than text.
     */
    public final static String IMAGE = NS + "Image";

    /**
     * Refers to http://dublincore.org/documents/2012/06/14/dcmi-terms/?v=dcmitype#StillImage
     * 	A static visual representation.
     */
    public final static String STILL_IMAGE = NS + "StillImage";

    /**
     * Refers to http://dublincore.org/documents/dcmi-terms/#dcmitype-MovingImage
     * A series of visual representations imparting an impression of motion when shown in succession.
     */
    public final static String MOVING_IMAGE = NS + "MovingImage";

    /**
     * Refers to http://dublincore.org/documents/dcmi-terms/#dcmitype-Sound
     * A resource primarily intended to be heard.
     */
    public final static String SOUND = NS + "Sound";

    /**
     * Refers to http://dublincore.org/documents/dcmi-terms/#dcmitype-Text
     * A resource consisting primarily of words for reading.
     */
    public final static String TEXT = NS + "Text";
}
