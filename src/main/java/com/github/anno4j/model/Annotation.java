package com.github.anno4j.model;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.ontologies.OADM;
import org.openrdf.annotations.Iri;
import org.openrdf.model.Resource;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.RDFObject;

/**
 * Conforms to oa:Annotation (http://www.openannotation.org/spec/core/core.html)
 */
@Iri(OADM.ANNOTATION)
public class Annotation implements RDFObject {

    /**
     * Unique identifier for the instance.
     */
    private Resource resource = Anno4j.getInstance().getIdGenerator().generateID();

    /**
     * Refers to http://www.w3.org/ns/oa#hasBody
     */
    @Iri(OADM.HAS_BODY)      private Body body;
    /**
     * Refers to http://www.w3.org/ns/oa#hasTarget
     */
    @Iri(OADM.HAS_TARGET)    private Target target;
    /**
     * Refers to http://www.w3.org/ns/oa#motivatedBy
     */
    @Iri(OADM.MOTIVATED_BY)  private Motivation motivatedBy;
    /**
     * Refers to http://www.w3.org/ns/oa#serializedBy
     */
    @Iri(OADM.SERIALIZED_BY) private Agent serializedBy;
    /**
     * Refers to http://www.w3.org/ns/oa#serializedAt
     */
    @Iri(OADM.SERIALIZED_AT) private String serializedAt;
    /**
     * Refers to http://www.w3.org/ns/oa#annotatedBy
     */
    @Iri(OADM.ANNOTATED_BY)  private Agent annotatedBy;
    /**
     * Refers to http://www.w3.org/ns/oa#annotatedAt
     */
    @Iri(OADM.ANNOTATED_AT)  private String annotatedAt;

    /**
     * Constructor.
     */
    public Annotation() {
    }

    /**
     *  The current {@link ObjectConnection} this object is atached to. Will be implemented by the proxy object.
     */
    @Override
    public ObjectConnection getObjectConnection() {
        // will be implemented by the proxy object
        return null;
    }

    /**
     * Getter for the unique identifier.
     * @return a unique identifier for this instance.
     */
    public Resource getResource() {
        return this.resource;
    }

    /**
     * Setter for the unique identifier.
     * @param resource the unique identifier.
     */
    public void setResource(Resource resource) { this.resource = resource; }

    /**
     * Gets http:www.w3.org/ns/oa#hasBody relationship.
     *
     * @return Value of http:www.w3.org/ns/oa#hasBody.
     */
    public Body getBody() {
        return body;
    }

    /**
     * Sets http:www.w3.org/ns/oa#hasBody.
     *
     * @param body New value of http:www.w3.orgnsoa#hasBody.
     */
    public void setBody(Body body) {
        this.body = body;
    }

    /**
     * Gets http:www.w3.org/ns/oa#hasTarget relationship.
     *
     * @return Value of http:www.w3.org/ns/oa#hasTarget.
     */
    public Target getTarget() {
        return target;
    }

    /**
     * Sets http:www.w3.org/ns/oa#hasTarget.
     *
     * @param target New value of http:www.w3.org/ns/oa#hasTarget.
     */
    public void setTarget(Target target) {
        this.target = target;
    }

    /**
     * Gets http:www.w3.org/ns/oa#motivatedBy relationship.
     *
     * @return Value of http:www.w3.org/ns/oa/#motivatedBy.
     */
    public Motivation getMotivatedBy() {
        return motivatedBy;
    }

    /**
     * Sets http:www.w3.org/ns/oa#motivatedBy.
     *
     * @param motivatedBy New value of http:www.w3.org/ns/oa#motivatedBy.
     */
    public void setMotivatedBy(Motivation motivatedBy) {
        this.motivatedBy = motivatedBy;
    }

    /**
     * Gets http:www.w3.org/ns/oa#serializedBy relationship.
     *
     * @return Value of http:www.w3.org/ns/oa#serializedBy.
     */
    public Agent getSerializedBy() {
        return serializedBy;
    }

    /**
     * Sets http:www.w3.org/ns/oa#serializedBy.
     *
     * @param serializedBy New value of http:www.w3.org/ns/oa#serializedBy.
     */
    public void setSerializedBy(Agent serializedBy) {
        this.serializedBy = serializedBy;
    }

    /**
     * Gets http:www.w3.org/ns/oa#annotatedBy relationship.
     *
     * @return Value of http:www.w3.org/ns/oa#annotatedBy.
     */
    public Agent getAnnotatedBy() {
        return annotatedBy;
    }

    /**
     * Sets http:www.w3.org/ns/oa#annotatedBy.
     *
     * @param annotatedBy New value of http:www.w3.org/ns/oa#annotatedBy.
     */
    public void setAnnotatedBy(Agent annotatedBy) {
        this.annotatedBy = annotatedBy;
    }

    /**
     * Gets http:www.w3.org/ns/oa#serializedAt relationship.
     *
     * @return Value of http:www.w3.org/ns/oa#serializedAt.
     */
    public String getSerializedAt() {
        return serializedAt;
    }

    /**
     * Sets http:www.w3.org/ns/oa#serializedAt.
     *
     * @param serializedAt New value of http:www.w3.org/ns/oa#serializedAt.
     */
    public void setSerializedAt(String serializedAt) {
        this.serializedAt = serializedAt;
    }

    /**
     * Gets http:www.w3.org/ns/oa#annotatedAt relationship.
     *
     * @return Value of http:www.w3.org/ns/oa#annotatedAt.
     */
    public String getAnnotatedAt() {
        return annotatedAt;
    }

    /**
     * Sets http:www.w3.org/ns/oa#annotatedAt.
     *
     * @param annotatedAt New value of http:www.w3.org/ns/oa#annotatedAt.
     */
    public void setAnnotatedAt(String annotatedAt) {
        this.annotatedAt = annotatedAt;
    }

    @Override
    public String toString() {
        return "AnnotationDefault{" +
                "body=" + body +
                ", target=" + target +
                ", motivatedBy=" + motivatedBy +
                ", serializedBy=" + serializedBy +
                ", serializedAt='" + serializedAt + '\'' +
                ", annotatedBy=" + annotatedBy +
                ", annotatedAt='" + annotatedAt + '\'' +
                ", resource='" + getResource() + '\'' +
                '}';
    }
}