package com.github.anno4j.model.impl;

import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.RDFObject;

/**
 * This object is used when an entity is solely represented by its resource/resourceURI.
 */
public class StringURLResource implements RDFObject {

    /**
     * The resource URI of the entity.
     */
    private Resource resource;

    /**
     * Constructor also setting the resource URI.
     * @param resourceURI
     */
    public StringURLResource(String resourceURI) {
        this.resource = new URIImpl(resourceURI);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectConnection getObjectConnection() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Resource getResource() {
        return this.resource;
    }
}
