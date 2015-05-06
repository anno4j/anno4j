package com.github.anno4j.model;

import com.github.anno4j.Anno4j;
import org.openrdf.model.Resource;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.RDFObject;

/**
 * Created by schlegel on 06/05/15.
 */
public abstract class Annotation implements RDFObject {

    private Resource resource = Anno4j.getInstance().getIdGenerator().generateID();

    public Annotation() {
    }

    abstract public void setBody(Body body);
    abstract public Body getBody();

    abstract public void setTarget(Target target);
    abstract public Target getTarget();

    abstract public void setMotivatedBy(Motivation motivation);
    abstract public Motivation getMotivatedBy();

    abstract public void setAnnotatedBy(Agent agent);
    abstract public Agent getAnnotatedBy();

    abstract public void setAnnotatedAt(String timestamp);
    abstract public String getAnnotatedAt();

    abstract public void setSerializedBy(Agent agent);
    abstract public Agent getSerializedBy();

    abstract public void setSerializedAt(String timestamp);
    abstract public String getSerializedAt();



    @Override
    public ObjectConnection getObjectConnection() {
        // will be implemented by the proxy object
        return null;
    }

    @Override
    public Resource getResource() {
        return this.resource;
    }
}
