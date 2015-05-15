package com.github.anno4j.persistence.impl;

import com.github.anno4j.persistence.IDGenerator;
import org.openrdf.model.Resource;
import org.openrdf.sail.memory.model.MemValueFactory;

import java.util.UUID;

/**
 * A IDGenerators using an urn:anno4j prefix. Not intended for real world applications.
 */
public class IDGeneratorAnno4jURN implements IDGenerator {

    /**
     * Generates a unique resource with an urn:anno4j prefix.
     * @return a Resource containing a unique identifier.
     */
    @Override
    public Resource generateID() {
        return new MemValueFactory().createURI("urn:anno4j:" + UUID.randomUUID());
    }
}
