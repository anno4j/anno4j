package com.github.anno4j.model;

import com.github.anno4j.Anno4j;
import org.openrdf.model.Resource;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.RDFObject;

/**
 * Conforms to OADM Motivation (http://www.openannotation.org/spec/core/core.html#Motivations)
 */
public abstract class Motivation implements RDFObject {

    /**
     * Unique identifier for the instance.
     */
    private Resource resource = Anno4j.getInstance().getIdGenerator().generateID();

    /**
     * Constructor.
     */
    public Motivation() {
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
    @Override
    public Resource getResource() {
        return this.resource;
    }

    /**
     * Setter for the unique identifier.
     * @param resource the unique identifier.
     */
    public void setResource(Resource resource) { this.resource = resource; }
}
