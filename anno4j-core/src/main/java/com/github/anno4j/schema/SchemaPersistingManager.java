package com.github.anno4j.schema;

import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.reflections.Reflections;

/**
 * Persists schema information provided via Java annotations to
 * a RDF triplestore.
 */
public abstract class SchemaPersistingManager {

    /**
     * Signalizes that the schema annotations made are inconsistent.
     */
    public static class InconsistentAnnotationException extends RuntimeException {
        /**
         * {@inheritDoc}
         */
        public InconsistentAnnotationException() {
        }

        /**
         * {@inheritDoc}
         */
        public InconsistentAnnotationException(String message) {
            super(message);
        }
    }

    /**
     * Signalizes that the schema information imposed by schema annotations found
     * contradicts the schema information that is already present in the triplestore.
     */
    public static class ContradictorySchemaException extends RepositoryException {
        /**
         * {@inheritDoc}
         */
        public ContradictorySchemaException() {
            super();
        }

        /**
         * {@inheritDoc}
         */
        public ContradictorySchemaException(String message) {
            super(message);
        }
    }

    /**
     * Connection to the triplestore, which will receive the schema information.
     */
    private final ObjectConnection connection;

    /**
     * Persists the schema information implied by schema annotations to the default graph of the connected triplestore.
     * Performs a validation that the schema annotations are consistent.
     * @param types The types which methods and field should be scanned for schema information.
     * @throws RepositoryException Thrown if an error occurs while persisting schema information.
     * @throws InconsistentAnnotationException Thrown if the schema annotations are inconsistent.
     * @throws ContradictorySchemaException Thrown if the schema information imposed by annotations contradicts with
     * schema information that is already present in the connected triplestore.
     */
    public abstract void persistSchema(Reflections types) throws RepositoryException, InconsistentAnnotationException;

    /**
     * @param connection Connection to the triplestore that should receive schema information.
     */
    public SchemaPersistingManager(ObjectConnection connection) {
        this.connection = connection;
    }

    /**
     * @return Returns the connection to the triplestore that should receive schema information.
     */
    ObjectConnection getConnection() {
        return connection;
    }
}
