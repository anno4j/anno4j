package com.github.anno4j.model;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.Anno4jNS;
import com.github.anno4j.model.namespaces.DCTERMS;
import com.github.anno4j.model.namespaces.OADM;
import org.openrdf.annotations.Iri;

import java.util.Set;

/**
 * Interface for the Annotation, Body, and Target class, introducing provenance information: created, creator, and modified properties
 */
@Iri(Anno4jNS.CREATION_PROVENANCE)
public interface CreationProvenance extends ResourceObject {

    /**
     * Gets the value of the http://purl.org/dc/terms/creator relationship.
     *
     * @return The Agent of the dcterms:creator relationship.
     */
    @Iri(DCTERMS.CREATOR)
    Agent getCreator();

    /**
     * Set the value of the http://purl.org/dc/terms/creator relationship.
     *
     * @param agent The Agent to set for the dcterms:creator relationship.
     */
    @Iri(DCTERMS.CREATOR)
    void setCreator(Agent agent);

    /**
     * Gets the value of the http://purl.org/dc/terms/created property.
     *
     * @return  The timestamp value of the dcterms:created property.
     */
    String getCreated();

    /**
     * Sets the value of the http://purl.org/dc/terms/created property.
     *
     * @param created   The timestamp to set for the dcterms:created property.
     */
    void setCreated(String created);

    /**
     * Set the value of the http://purl.org/dc/terms/created property.
     *
     * @param year          The year to set.
     * @param month         The month to set.
     * @param day           The day to set.
     * @param hours         The hours to set.
     * @param minutes       The minutes to set.
     * @param seconds       The seconds to set.
     * @param timezoneID    The timezone to set.
     */
    void setCreated(int year, int month, int day, int hours, int minutes, int seconds, String timezoneID);

    /**
     * Set the value of the http://purl.org/dc/terms/created property.
     *
     * @param year          The year to set.
     * @param month         The month to set.
     * @param day           The day to set.
     * @param hours         The hours to set.
     * @param minutes       The minutes to set.
     * @param seconds       The seconds to set.
     * @param milliseconds  The milliseconds to set.
     * @param timezoneID    The timezone to set.
     */
    void setCreated(int year, int month, int day, int hours, int minutes, int seconds, int milliseconds, String timezoneID);

    /**
     * Gets the value of the http://purl.org/dc/terms/modified property.
     *
     * @return  The timestamp value of the dcterms:modified property.
     */
    String getModified();

    /**
     * Set the value of the http://purl.org/dc/terms/modified property.
     *
     * @param modification  The timestamp value to set for the dcterms:modified property.
     */
    void setModified(String modification);

    /**
     * Set the value of the http://purl.org/dc/terms/modified property.
     *
     * @param year          The year to set.
     * @param month         The month to set.
     * @param day           The day to set.
     * @param hours         The hours to set.
     * @param minutes       The minutes to set.
     * @param seconds       The seconds to set.
     * @param timezoneID    The timezone to set.
     */
    void setModified(int year, int month, int day, int hours, int minutes, int seconds, String timezoneID);

    /**
     * Set the value of the http://purl.org/dc/terms/modified property.
     *
     * @param year          The year to set.
     * @param month         The month to set.
     * @param day           The day to set.
     * @param hours         The hours to set.
     * @param minutes       The minutes to set.
     * @param seconds       The seconds to set.
     * @param milliseconds  The milliseconds to set.
     * @param timezoneID    The timezone to set.
     */
    void setModified(int year, int month, int day, int hours, int minutes, int seconds, int milliseconds, String timezoneID);

    /**
     * Sets the values for the http://purl.org/dc/terms/rights relationship.
     *
     * Information about rights held in and over the resource.
     *
     * @param rights    The Set of values to set for the http://purl.org/dc/terms/rights relationship.
     */
    @Iri(DCTERMS.RIGHTS)
    void setRights(Set<ResourceObject> rights);

    /**
     * Gets the Set of values currently defined for the http://purl.org/dc/terms/rights relationship.
     *
     * Information about rights held in and over the resource.
     *
     * @return  The Set of values currently defined for the http://purl.org/dc/terms/rights relationship.
     */
    @Iri(DCTERMS.RIGHTS)
    Set<ResourceObject> getRights();

    /**
     * Adds a single value to the Set of values currently defined for the http://purl.org/dc/terms/rights relationship.
     *
     * @param right The value to add to the http://purl.org/dc/terms/rights relationship.
     */
    void addRight(ResourceObject right);

    /**
     * Sets the value for the http://www.w3.org/ns/oa#canonical relationship.
     *
     * A object of the relationship is the canonical IRI that can always be used to deduplicate the Annotation,
     * regardless of the current IRI used to access the representation.
     *
     * @param canonicalResource The value to set for the http://www.w3.org/ns/oa#canonical relationship.
     */
    @Iri(OADM.CANONICAL)
    void setCanonical(ResourceObject canonicalResource);

    /**
     * Gets the value currently defined for the http://www.w3.org/ns/oa#canonical relationship.
     *
     * A object of the relationship is the canonical IRI that can always be used to deduplicate the Annotation,
     * regardless of the current IRI used to access the representation.
     *
     * @return  The value currently defined for the http://www.w3.org/ns/oa#canonical relationship.
     */
    @Iri(OADM.CANONICAL)
    ResourceObject getCanonical();

    /**
     * Sets the values for the http://www.w3.org/ns/oa#via relationship.
     *
     * A object of the relationship is a resource from which the source resource was retrieved by the providing system.
     *
     * @param viaSet    The Set of values to set for the http://www.w3.org/ns/oa#via relationship.
     */
    @Iri(OADM.VIA)
    void setVia(Set<ResourceObject> viaSet);

    /**
     * Gets the Set of values currently defined for the http://www.w3.org/ns/oa#via relationship.
     *
     * A object of the relationship is a resource from which the source resource was retrieved by the providing system.
     *
     * @return  The Set of values currently defined for the http://www.w3.org/ns/oa#via relationship.
     */
    @Iri(OADM.VIA)
    Set<ResourceObject> getVia();

    /**
     * Adds a single value to the Set of values currently defined for the http://www.w3.org/ns/oa#via relationship.
     *
     * @param via   The single value to add to the Set of values currently defined for the http://www.w3.org/ns/oa#via
     *              relationship.
     */
    void addVia(ResourceObject via);
}
