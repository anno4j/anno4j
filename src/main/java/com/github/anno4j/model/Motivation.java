package com.github.anno4j.model;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import org.openrdf.model.Resource;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.RDFObject;

/**
 * Conforms to OADM Motivation (http://www.openannotation.org/spec/core/core.html#Motivations)
 */
public abstract class Motivation extends ResourceObject {

    /**
     * Standard constructor.
     */
    public Motivation() {}

    @Override
    public String toString() {
        return "Motivation{" +
                "resource='" + this.getResource() + "'" +
                "}";
    }
}
