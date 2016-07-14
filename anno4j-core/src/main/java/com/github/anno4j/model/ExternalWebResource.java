package com.github.anno4j.model;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.DC;
import com.github.anno4j.model.namespaces.OADM;
import org.openrdf.annotations.Iri;

import java.util.Set;

/**
 * The Body and the Target in the Web Annotation Data Model are defined to be an external web resource. The target
 * always has to be, the body can be included in the Annotation.
 *
 * Web Resources are identified with a IRI and have various properties, often including a format or language for the
 * resource's content. This information may be recorded as part of the Annotation, even if the representation of the
 * resource must be retrieved from the Web.
 */
public interface ExternalWebResource extends CreationProvenance {

    /**
     * Sets the value for the http://www.w3.org/ns/oa#processingLanguage property.
     *
     * The object of the property is the language that should be used for textual processing algorithms when dealing
     * with the content of the resource, including hyphenation, line breaking, which font to use for rendering and so
     * forth. The value must follow the recommendations of [BCP47].
     *
     * @param processingLanguage    The value to set for the http://www.w3.org/ns/oa#processingLanguage property.
     */
    @Iri(OADM.PROCESSING_LANGUAGE)
    void setProcessingLanguage(String processingLanguage);

    /**
     * Gets the value of the http://www.w3.org/ns/oa#processingLanguage property.
     *
     * The object of the property is the language that should be used for textual processing algorithms when dealing
     * with the content of the resource, including hyphenation, line breaking, which font to use for rendering and so
     * forth. The value must follow the recommendations of [BCP47].
     *
     * @return  The value currently set for the http://www.w3.org/ns/oa#processingLanguage property.
     */
    @Iri(OADM.PROCESSING_LANGUAGE)
    String getProcessingLanguage();

    /**
     * Sets the values for the http://purl.org/dc/elements/1.1/language property.
     *
     * The dc:language predicate recommends the use of a controlled vocabulary. The use of dc:language in the
     * Annotation model further specifies that the vocabulary to use is [BCP47].
     *
     * @param language  The values to set for the http://purl.org/dc/elements/1.1/language property.
     */
    @Iri(DC.LANGUAGE)
    void setLanguages(Set<String> language);

    /**
     * Gets the values currently set for the http://purl.org/dc/elements/1.1/language property.
     *
     * The dc:language predicate recommends the use of a controlled vocabulary. The use of dc:language in the
     * Annotation model further specifies that the vocabulary to use is [BCP47].
     *
     * @return  The set of values currently set for the http://purl.org/dc/elements/1.1/language property.
     */
    @Iri(DC.LANGUAGE)
    Set<String> getLanguages();

    /**
     * Adds a single value to the set of currently set values for the http://purl.org/dc/elements/1.1/language property.
     *
     * @param language  The language value to add to the currently set of values for the
     *                  http://purl.org/dc/elements/1.1/language property.
     */
    void addLanguage(String language);

    /**
     * Sets the value for the http://www.w3.org/ns/oa#textDirection relationship.
     *
     * The direction of the text of the subject resource. There must only be one text direction associated with any
     * given resource.
     *
     * @param direction The value to set for the http://www.w3.org/ns/oa#textDirection relationship.
     */
    @Iri(OADM.TEXT_DIRECTION)
    void setTextDirection(ResourceObject direction);

    /**
     * Gets the value currently set for the http://www.w3.org/ns/oa#textDirection relationship.
     *
     * The direction of the text of the subject resource. There must only be one text direction associated with any
     * given resource.
     *
     * @return  The value currently set for the http://www.w3.org/ns/oa#textDirection relationship.
     */
    @Iri(OADM.TEXT_DIRECTION)
    ResourceObject getTextDirection();
}
