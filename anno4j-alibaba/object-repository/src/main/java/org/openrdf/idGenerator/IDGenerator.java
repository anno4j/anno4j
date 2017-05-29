package org.openrdf.idGenerator;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.sail.memory.model.MemValueFactory;

import java.util.Set;

/**
 * Interface for IDGenerators. IDGenerators provide a method to generate a unique ressource for internal RDF nodes.
 */
public interface IDGenerator {

    public static final Resource BLANK_RESOURCE = new MemValueFactory().createURI("urn:anno4j:BLANK");

    /**
     * Generates a unique resource.
     * @return a Resource containing a unique identifier.
     * @param types
     */
    Resource generateID(Set<URI> types);
}
