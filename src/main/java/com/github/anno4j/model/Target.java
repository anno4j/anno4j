package com.github.anno4j.model;

import com.github.anno4j.Anno4j;
import org.openrdf.model.Resource;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.RDFObject;

/**
 * Conforms to OADM Target concept (http://www.openannotation.org/spec/core/core.html#BodyTargetType)
 */
public abstract class Target implements RDFObject {

    private Resource resource = Anno4j.getInstance().getIdGenerator().generateID();

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
