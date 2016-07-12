package com.github.anno4j.model;

import com.github.anno4j.model.namespaces.AS;
import com.github.anno4j.model.namespaces.DCTERMS;
import com.github.anno4j.model.namespaces.OADM;
import com.github.anno4j.model.namespaces.SCHEMA;
import org.openrdf.annotations.Iri;

import java.util.Set;

/**
 * Conforms to oa:Annotation (http://www.openannotation.org/spec/core/core.html)
 */
@Iri(OADM.ANNOTATION)
public interface Annotation extends CreationProvenance {

    /**
     * Gets http:www.w3.org/ns/oa#hasBody relationship.
     *
     * @return Value of http:www.w3.org/ns/oa#hasBody.
     */
    @Iri(OADM.HAS_BODY)
    Body getBody();

    /**
     * Sets http:www.w3.org/ns/oa#hasBody.
     *
     * @param body New value of http:www.w3.org/ns/oa#hasBody.
     */
    @Iri(OADM.HAS_BODY)
    void setBody(Body body);

    /**
     * Gets http:www.w3.org/ns/oa#hasTarget relationships.
     *
     * @return Values of http:www.w3.org/ns/oa#hasTarget.
     */
    @Iri(OADM.HAS_TARGET)
    Set<Target> getTarget();

    /**
     * Sets http:www.w3.org/ns/oa#hasTarget.
     *
     * @param targets New value of http:www.w3.org/ns/oa#hasTarget.
     */
    @Iri(OADM.HAS_TARGET)
    void setTarget(Set<Target> targets);

    /**
     * Adds a http:www.w3.org/ns/oa#hasTarget relationship.
     *
     * @param target New http:www.w3.org/ns/oa#hasTarget relationship.
     */
    void addTarget(Target target);

    /**
     * Gets http:www.w3.org/ns/oa#motivatedBy relationship.
     *
     * @return Value of http:www.w3.org/ns/oa/#motivatedBy.
     */
    @Iri(OADM.MOTIVATED_BY)
    Set<Motivation> getMotivatedBy();

    /**
     * Sets http:www.w3.org/ns/oa#motivatedBy.
     *
     * @param motivatedBy New value of http:www.w3.org/ns/oa#motivatedBy.
     */
    @Iri(OADM.MOTIVATED_BY)
    void setMotivatedBy(Set<Motivation> motivatedBy);

    /**
     * Add a single http:www.w3.org/ns/oa#motivatedBy relationship.
     *
     * @param motivation    The Motivation to add to this Annotation node.
     */
    void addMotivation(Motivation motivation);

    /**
     * Refers to http://www.w3.org/ns/oa#bodyText
     *
     * @return  Returns the currently set bodyText properties.
     */
    @Iri(OADM.BODY_TEXT)
    Set<String> getBodyTexts();

    /**
     * Refers to http://www.w3.org/ns/oa#bodyText
     *
     * @param texts A Set of Strings to set for this nodes bodyText property.
     */
    @Iri(OADM.BODY_TEXT)
    void setBodyTexts(Set<String> texts);

    /**
     * Refers to http://www.w3.org/ns/oa#bodyText
     *
     * @param text  Adds the single text as textBody property.
     */
    void addBodyText(String text);

    /**
     * Gets the value of the http://www.w3.org/ns/activitystreams#generator relationship.
     *
     * @return  The Agent of the as:generator relationship.
     */
    @Iri(AS.GENERATOR)
    Agent getGenerator();

    /**
     * Sets the value of the http://www.w3.org/ns/activitystreams#generator relationship.
     *
     * @param generator The Agent to set for the as:generator relationship.
     */
    @Iri(AS.GENERATOR)
    void setGenerator(Agent generator);

    /**
     * Gets the value of the http://purl.org/dc/terms/issued property.
     *
     * @return  The timestamp value of the dcterms:issued property.
     */
    @Iri(DCTERMS.ISSUED)
    String getGenerated();

    /**
     * Sets the value of the http://purl.org/dc/terms/issued property.
     *
     * @param generated The timestamp value to set for the dcterms:issued property.
     */
    @Iri(DCTERMS.ISSUED)
    void setGenerated(String generated);

    /**
     * Sets the value of the http://purl.org/dc/terms/issued property.
     *
     * @param year          The year to set.
     * @param month         The month to set.
     * @param day           The day to set.
     * @param hours         The hours to set.
     * @param minutes       The minutes to set.
     * @param seconds       The seconds to set.
     * @param timezoneID    The timezone to set.
     */
    void setGenerated(int year, int month, int day, int hours, int minutes, int seconds, String timezoneID);

    /**
     * Gets http:www.w3.org/ns/oa#serializedBy relationship.
     *
     * @return Value of http:www.w3.org/ns/oa#serializedBy.
     */
    @Deprecated
    @Iri(OADM.SERIALIZED_BY)
    Agent getSerializedBy();

    /**
     * Sets http:www.w3.org/ns/oa#serializedBy.
     *
     * @param serializedBy New value of http:www.w3.org/ns/oa#serializedBy.
     */
    @Deprecated
    @Iri(OADM.SERIALIZED_BY)
    void setSerializedBy(Agent serializedBy);

    /**
     * Gets http:www.w3.org/ns/oa#annotatedBy relationship.
     *
     * @return Value of http:www.w3.org/ns/oa#annotatedBy.
     */
    @Deprecated
    @Iri(OADM.ANNOTATED_BY)
    Agent getAnnotatedBy();

    /**
     * Sets http:www.w3.org/ns/oa#annotatedBy.
     *
     * @param annotatedBy New value of http:www.w3.org/ns/oa#annotatedBy.
     */
    @Deprecated
    @Iri(OADM.ANNOTATED_BY)
    void setAnnotatedBy(Agent annotatedBy);

    /**
     * Gets http:www.w3.org/ns/oa#serializedAt relationship.
     *
     * @return Value of http:www.w3.org/ns/oa#serializedAt.
     */
    @Deprecated
    @Iri(OADM.SERIALIZED_AT)
    String getSerializedAt();

    /**
     * Sets http:www.w3.org/ns/oa#serializedAt.
     *
     * @param serializedAt New value of http:www.w3.org/ns/oa#serializedAt.
     */
    @Deprecated
    @Iri(OADM.SERIALIZED_AT)
    void setSerializedAt(String serializedAt);

    /**
     * Sets http:www.w3.org/ns/oa#serializedAt according to the format year-month-dayThours:minutes:secondsZ, e.g. 2015-12-16T12:00:00Z.
     *
     * @param year          The year to set.
     * @param month         The month to set.
     * @param day           The day to set.
     * @param hours         The hours to set.
     * @param minutes       The minutes to set.
     * @param seconds       The seconds to set.
     * @param timezoneID    The timezone to set.
     */
    @Deprecated
    void setSerializedAt(int year, int month, int day, int hours, int minutes, int seconds, String timezoneID);

    /**
     * Gets http:www.w3.org/ns/oa#annotatedAt relationship.
     *
     * @return Value of http:www.w3.org/ns/oa#annotatedAt.
     */
    @Deprecated
    @Iri(OADM.ANNOTATED_AT)
    String getAnnotatedAt();

    /**
     * Sets http:www.w3.org/ns/oa#annotatedAt.
     *
     * @param annotatedAt New value of http:www.w3.org/ns/oa#annotatedAt.
     */
    @Deprecated
    @Iri(OADM.ANNOTATED_AT)
    void setAnnotatedAt(String annotatedAt);

    /**
     * Sets http:www.w3.org/ns/oa#annotatedAt according to the format year-month-dayThours:minutes:secondsZ, e.g. 2015-12-16T12:00:00Z.
     *
     * @param year          The year to set.
     * @param month         The month to set.
     * @param day           The day to set.
     * @param hours         The hours to set.
     * @param minutes       The minutes to set.
     * @param seconds       The seconds to set.
     * @param timezoneID    The timezone to set.
     */
    @Deprecated
    void setAnnotatedAt(int year, int month, int day, int hours, int minutes, int seconds, String timezoneID);

    /**
     * Sets the set of values for the http://schema.org/audience relationship.
     *
     * @param audiences The audiences to set for the http://schema.org/audience relationship.
     */
    @Iri(SCHEMA.AUDIENCE_RELATIONSHIP)
    void setAudiences(Set<Audience> audiences);

    /**
     * Gets the set of values for the http://schema.org/audience relationship.
     *
     * @return  A set of Audience objects related to the http://schema.org/audience relationship.
     */
    @Iri(SCHEMA.AUDIENCE_RELATIONSHIP)
    Set<Audience> getAudiences();

    /**
     * Adds a single Audience object to the set of Audiences.
     *
     * @param audience  The Audience to add
     */
    void addAudience(Audience audience);
}