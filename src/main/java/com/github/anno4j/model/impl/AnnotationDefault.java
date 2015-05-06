package com.github.anno4j.model.impl;

import com.github.anno4j.model.*;
import org.openrdf.annotations.Iri;
import org.openrdf.model.Resource;

/**
 * Created by schlegel on 06/05/15.
 */
@Iri()
public class AnnotationDefault extends Annotation {

    private Body body;
    private Target target;
    private Motivation motivatedBy;
    private Agent serializedBy;
    private Agent annotatedBy;

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
    public Resource getResource() {
        return null;
    }
}
