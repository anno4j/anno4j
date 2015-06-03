package com.github.anno4j.model.impl;

import com.github.anno4j.Anno4j;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.RDFObject;

/**
 * Class to implement RDF in order to create a baseline for every object that we use in Anno4j.
 */
public class ResourceObject implements RDFObject {

    /**
     * Unique identifier for the instance.
     */
    private Resource resource = Anno4j.getInstance().getIdGenerator().generateID();

    /**
     *  The current {@link org.openrdf.repository.object.ObjectConnection} this object is attached to. Will be implemented by the proxy object.
     */
    @Override
    public ObjectConnection getObjectConnection() {
        // will be implemented by the proxy object
        return null;
    }

    /**
     * Gets Unique identifier for the instance.
     *
     * @return Value of Unique identifier for the instance..
     */
    public Resource getResource() {
        return resource;
    }

    /**
     * Sets new Unique identifier for the instance.
     *
     * @param resource New value of Unique identifier for the instance..
     */
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    /**
     * Sets new Unique identifier for the instance by a given String.
     *
     * @param resourceAsString Textual representation of the new value of Unique identifier for the instance.
     */
    public void setResourceAsString(String resourceAsString) {
        this.resource = new URIImpl(resourceAsString);
    }

    /**
     * Gets new identifier for this instance as String.
     * @return identifier as String.
     */
    public String getResourceAsString() {
        return this.resource.stringValue();
    }
}
