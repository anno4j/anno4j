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
    @Iri(DCTERMS.CREATED)
    String getCreated();

    /**
     * Sets the value of the http://purl.org/dc/terms/created property.
     *
     * @param created   The timestamp to set for the dcterms:created property.
     */
    @Iri(DCTERMS.CREATED)
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
     * Gets the value of the http://purl.org/dc/terms/modified property.
     *
     * @return  The timestamp value of the dcterms:modified property.
     */
    @Iri(DCTERMS.MODIFIED)
    String getModified();

    /**
     * Set the value of the http://purl.org/dc/terms/modified property.
     *
     * @param modification  The timestamp value to set for the dcterms:modified property.
     */
    @Iri(DCTERMS.MODIFIED)
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
}
