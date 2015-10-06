package org.openrdf.idGenerator;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.sail.memory.model.MemValueFactory;

import java.util.Set;
import java.util.UUID;

/**
 * A IDGenerators using an urn:anno4j prefix. Not intended for real world applications.
 */
public class IDGeneratorAnno4jURN implements IDGenerator {

    /**
     * Generates a unique resource with an urn:anno4j prefix.
     * @return a Resource containing a unique identifier.
     * @param types
     */
    @Override
    public Resource generateID(Set<URI> types) {
        return new MemValueFactory().createURI("urn:anno4j:" + UUID.randomUUID());
    }
}
