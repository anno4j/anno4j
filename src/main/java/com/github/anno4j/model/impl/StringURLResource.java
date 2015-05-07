package com.github.anno4j.model.impl;

import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.RDFObject;

/**
 *
 */
public class StringURLResource implements RDFObject {

    private Resource resource;

    public StringURLResource(String resourceURI) {
        this.resource = new URIImpl(resourceURI);
    }

    @Override
    public ObjectConnection getObjectConnection() {
        return null;
    }

    @Override
    public Resource getResource() {
        return this.resource;
    }
}
