package com.github.anno4j.persistence.impl;

import com.github.anno4j.persistence.IDGenerator;
import org.openrdf.model.Resource;
import org.openrdf.sail.memory.model.MemValueFactory;

import java.util.UUID;

/**
 * Created by schlegel on 06/05/15.
 */
public class IDGeneratorLocalURN implements IDGenerator {

    @Override
    public Resource generateID() {
        return new MemValueFactory().createURI("urn:anno4j:" + UUID.randomUUID());
    }
}
