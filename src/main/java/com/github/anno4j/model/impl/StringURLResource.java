package com.github.anno4j.model.impl;

import org.openrdf.model.Resource;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.RDFObject;

/**
 * This object is used when an entity is solely represented by its resource/resourceURI.
 */
public class StringURLResource implements RDFObject {

    /**
     * Unique identifier for the instance.
     */
    private Resource resource;

    /**
     * Constructor
     */
    public StringURLResource() {
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
