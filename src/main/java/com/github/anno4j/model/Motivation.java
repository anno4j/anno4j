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
     * The resource URI of the entity.
     */
    private Resource resource = Anno4j.getInstance().getIdGenerator().generateID();

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectConnection getObjectConnection() {
        // will be implemented by the proxy object
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Resource getResource() {
        return this.resource;
    }

    /**
     * Setter for the resource URI.
     * @param resource  The resource URI to set.
     */
    public void setResource(Resource resource) { this.resource = resource; }
}
