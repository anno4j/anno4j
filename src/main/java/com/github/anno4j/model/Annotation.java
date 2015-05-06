package com.github.anno4j.model;

/**
 * Created by schlegel on 06/05/15.
 */
public interface Annotation {

    public void setBody(Body body);
    public Body getBody();

    public void setTarget(Target target);
    public Target getTarget();

    public void setMotivatedBy(Motivation motivation);
    public Motivation getMotivatedBy();

    public void setAnnotatedBy(Agent agent);
    public Agent getAnnotatedBy();

    public void setSerializedBy(Agent agent);
    public Agent getSerializedBy();
}
