package eu.mico.platform.anno4j.model;

import com.github.anno4j.model.*;
import com.github.anno4j.model.namespaces.OADM;
import eu.mico.platform.anno4j.model.namespaces.MMM;
import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.RDFObject;

import java.util.Set;

/**
 * Class represents a Part. A Part resembles an extractor step and consecutively an (intermediary)
 * result of an Item and its extraction chain.
 */
@Iri(MMM.PART)
public interface Part extends Annotation {

    /**
     * Gets http:www.w3.org/ns/oa#hasBody relationship.
     *
     * @return Value of http:www.w3.org/ns/oa#hasBody.
     */
    @Iri(MMM.HAS_BODY)
    @Override
    Body getBody();

    /**
     * Sets http:www.w3.org/ns/oa#hasBody.
     *
     * @param body New value of http:www.w3.orgnsoa#hasBody.
     */
    @Iri(MMM.HAS_BODY)
    @Override
    void setBody(Body body);

    /**
     * Gets http:www.w3.org/ns/oa#hasTarget relationships.
     *
     * @return Values of http:www.w3.org/ns/oa#hasTarget.
     */
    @Iri(MMM.HAS_TARGET)
    @Override
    Set<Target> getTarget();

    /**
     * Sets http:www.w3.org/ns/oa#hasTarget.
     *
     * @param targets New value of http:www.w3.org/ns/oa#hasTarget.
     */
    @Iri(MMM.HAS_TARGET)
    void setTarget(Set<Target> targets);

    /**
     * Gets the objects that were the semantical input for this Part.
     *
     * @return A set of objects that are used as semantical input for creating this Part.
     */
    @Iri(MMM.HAS_INPUT)
    Set<RDFObject> getInputs();

    /**
     * Sets the Set of objects that are the semantical input for this Part.
     *
     * @param inputs    The set of objects that form the semantical input for this Part
     */
    @Iri(MMM.HAS_INPUT)
    void setInputs(Set<RDFObject> inputs);

    /**
     * Adds a single object to the set of objects, that form the semantical input for this Part.
     *
     * @param input The object that is to be added to the set of objects, that form the semantical input for this part.
     */
    void addInput(RDFObject input);

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
    Motivation getMotivatedBy();

    /**
     * Sets http:www.w3.org/ns/oa#motivatedBy.
     *
     * @param motivatedBy New value of http:www.w3.org/ns/oa#motivatedBy.
     */
    @Iri(OADM.MOTIVATED_BY)
    void setMotivatedBy(Motivation motivatedBy);

    /**
     * Gets http:www.w3.org/ns/oa#serializedBy relationship.
     *
     * @return Value of http:www.w3.org/ns/oa#serializedBy.
     */
    @Iri(OADM.SERIALIZED_BY)
    Agent getSerializedBy();

    /**
     * Sets http:www.w3.org/ns/oa#serializedBy.
     *
     * @param serializedBy New value of http:www.w3.org/ns/oa#serializedBy.
     */
    @Iri(OADM.SERIALIZED_BY)
    void setSerializedBy(Agent serializedBy);

    /**
     * Gets http:www.w3.org/ns/oa#annotatedBy relationship.
     *
     * @return Value of http:www.w3.org/ns/oa#annotatedBy.
     */
    @Iri(OADM.ANNOTATED_BY)
    Agent getAnnotatedBy();

    /**
     * Sets http:www.w3.org/ns/oa#annotatedBy.
     *
     * @param annotatedBy New value of http:www.w3.org/ns/oa#annotatedBy.
     */
    @Iri(OADM.ANNOTATED_BY)
    void setAnnotatedBy(Agent annotatedBy);

    /**
     * Gets http:www.w3.org/ns/oa#serializedAt relationship.
     *
     * @return Value of http:www.w3.org/ns/oa#serializedAt.
     */
    @Iri(OADM.SERIALIZED_AT)
    String getSerializedAt();

    /**
     * Sets http:www.w3.org/ns/oa#serializedAt.
     *
     * @param serializedAt New value of http:www.w3.org/ns/oa#serializedAt.
     */
    @Iri(OADM.SERIALIZED_AT)
    void setSerializedAt(String serializedAt);

    /**
     * Sets http:www.w3.org/ns/oa#serializedAt according to the format year-month-dayThours:minutes:secondsZ, e.g. 2015-12-16T12:00:00Z.
     *
     * @param year      The year to set.
     * @param month     The month to set.
     * @param day       The day to set.
     * @param hours     The hours to set.
     * @param minutes   The minutes to set.
     * @param seconds   The seconds to set.
     */
    void setSerializedAt(int year, int month, int day, int hours, int minutes, int seconds);

    /**
     * Gets http:www.w3.org/ns/oa#annotatedAt relationship.
     *
     * @return Value of http:www.w3.org/ns/oa#annotatedAt.
     */
    @Iri(OADM.ANNOTATED_AT)
    String getAnnotatedAt();

    /**
     * Sets http:www.w3.org/ns/oa#annotatedAt.
     *
     * @param annotatedAt New value of http:www.w3.org/ns/oa#annotatedAt.
     */
    @Iri(OADM.ANNOTATED_AT)
    void setAnnotatedAt(String annotatedAt);

    /**
     * Sets http:www.w3.org/ns/oa#annotatedAt according to the format year-month-dayThours:minutes:secondsZ, e.g. 2015-12-16T12:00:00Z.
     *
     * @param year      The year to set.
     * @param month     The month to set.
     * @param day       The day to set.
     * @param hours     The hours to set.
     * @param minutes   The minutes to set.
     * @param seconds   The seconds to set.
     */
    void setAnnotatedAt(int year, int month, int day, int hours, int minutes, int seconds);
}
