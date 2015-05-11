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

    private Resource resource = Anno4j.getInstance().getIdGenerator().generateID();

    @Iri(OADM.HAS_BODY)      private Body body;
    @Iri(OADM.HAS_TARGET)    private Target target;
    @Iri(OADM.MOTIVATED_BY)  private Motivation motivatedBy;
    @Iri(OADM.SERIALIZED_BY) private Agent serializedBy;
    @Iri(OADM.SERIALIZED_AT) private String serializedAt;
    @Iri(OADM.ANNOTATED_BY)  private Agent annotatedBy;
    @Iri(OADM.ANNOTATED_AT)  private String annotatedAt;

    public Annotation() {
    }

    @Override
    public ObjectConnection getObjectConnection() {
        // will be implemented by the proxy object
        return null;
    }

    public Resource getResource() {
        return this.resource;
    }

    public void setResource(Resource resource) { this.resource = resource; }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public Motivation getMotivatedBy() {
        return motivatedBy;
    }

    public void setMotivatedBy(Motivation motivatedBy) {
        this.motivatedBy = motivatedBy;
    }

    public Agent getSerializedBy() {
        return serializedBy;
    }

    public void setSerializedBy(Agent serializedBy) {
        this.serializedBy = serializedBy;
    }

    public Agent getAnnotatedBy() {
        return annotatedBy;
    }

    public void setAnnotatedBy(Agent annotatedBy) {
        this.annotatedBy = annotatedBy;
    }

    public String getSerializedAt() {
        return serializedAt;
    }

    public void setSerializedAt(String serializedAt) {
        this.serializedAt = serializedAt;
    }

    public String getAnnotatedAt() {
        return annotatedAt;
    }

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
