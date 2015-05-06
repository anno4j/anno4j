package com.github.anno4j.model.impl.annotation;

import com.github.anno4j.model.*;
import com.github.anno4j.model.ontologies.OADM;
import org.openrdf.annotations.Iri;

/**
 * Default implementation for {@link Annotation}
 */
@Iri(OADM.ANNOTATION)
public class AnnotationDefault extends Annotation {


    @Iri(OADM.HAS_BODY)      private Body body;
    @Iri(OADM.HAS_TARGET)    private Target target;
    @Iri(OADM.MOTIVATED_BY)  private Motivation motivatedBy;
    @Iri(OADM.SERIALIZED_BY) private Agent serializedBy;
    @Iri(OADM.SERIALIZED_AT) private String serializedAt;
    @Iri(OADM.ANNOTATED_BY)  private Agent annotatedBy;
    @Iri(OADM.ANNOTATED_AT)  private String annotatedAt;

    public AnnotationDefault() {
    }

    @Override
    public Body getBody() {
        return body;
    }

    @Override
    public void setBody(Body body) {
        this.body = body;
    }

    @Override
    public Target getTarget() {
        return target;
    }

    @Override
    public void setTarget(Target target) {
        this.target = target;
    }

    @Override
    public Motivation getMotivatedBy() {
        return motivatedBy;
    }

    @Override
    public void setMotivatedBy(Motivation motivatedBy) {
        this.motivatedBy = motivatedBy;
    }

    @Override
    public Agent getSerializedBy() {
        return serializedBy;
    }

    @Override
    public void setSerializedBy(Agent serializedBy) {
        this.serializedBy = serializedBy;
    }

    @Override
    public Agent getAnnotatedBy() {
        return annotatedBy;
    }

    @Override
    public void setAnnotatedBy(Agent annotatedBy) {
        this.annotatedBy = annotatedBy;
    }

    @Override
    public String getSerializedAt() {
        return serializedAt;
    }

    @Override
    public void setSerializedAt(String serializedAt) {
        this.serializedAt = serializedAt;
    }

    @Override
    public String getAnnotatedAt() {
        return annotatedAt;
    }

    @Override
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
