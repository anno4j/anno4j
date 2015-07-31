package com.github.anno4j.model;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import org.openrdf.model.Resource;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.RDFObject;

/**
 * Conforms to OADM Body concept (http://www.openannotation.org/spec/core/core.html#BodyTargetType)
 */
public abstract class Body extends ResourceObject {

    /**
     * Basic constructor.
     */
    public Body() {}

    @Override
    public String toString() {
        return "Body{" +
                "resource=" + this.getResource() + "'" +
                "}";
    }
}
