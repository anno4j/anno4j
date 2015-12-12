package com.github.anno4j.persistence;

import com.github.anno4j.model.Annotation;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectRepository;

/**
 * This class provides write access for annotated POJOs. Annotated objects will
 * be converted to corresponding RDF content and then transmitted to the
 * connected SPARQL endpoint.
 */
public class PersistenceService {

    /**
     * Local/Remote SPARQL endpoint connection
     */
    private ObjectRepository objectRepository;
    private URI graph;

    /**
     * Constructor
     *
     * @param objectRepository Local/Remote SPARQL endpoint connection
     */
    public PersistenceService(ObjectRepository objectRepository) {
        this.objectRepository = objectRepository;
    }

    /**
     * Constructor
     *
     * @param objectRepository Local/Remote SPARQL endpoint connection
     * @param graph Graph context to query
     */
    public PersistenceService(ObjectRepository objectRepository, URI graph) {
        this.objectRepository = objectRepository;
        this.graph = graph;

    }

    /**
     * Writes the annotation to the configured SPARQL endpoint with a
     * corresponding INSERT query.
     *
     * @param annotation annotation to write to the SPARQL endpoint
     * @throws RepositoryException
     */
    public void persistAnnotation(Annotation annotation) throws RepositoryException {
        ObjectConnection connection = objectRepository.getConnection();

        if (graph != null) {
            connection.setReadContexts(graph);
            connection.setInsertContext(graph);
            connection.setRemoveContexts(graph);
        }

        connection.addObject(annotation);
        connection.close();
    }

    /**
     * Removes the annotation from the configured SPARQL endpoint with a
     * corresponding DELETE query.
     *
     * @param annotation annotation to remove from the SPARQL endpoint
     * @throws RepositoryException
     */
    public void removeAnnotation(Annotation annotation) throws RepositoryException {
        ObjectConnection connection = objectRepository.getConnection();

        if (graph != null) {
            connection.setReadContexts(graph);
            connection.setInsertContext(graph);
            connection.setRemoveContexts(graph);
        }

        // Set all fields of the annotation to null
        annotation.setBody(null);
        annotation.setTargets(null);
        annotation.setAnnotatedBy(null);
        annotation.setAnnotatedAt(null);
        annotation.setMotivatedBy(null);
        annotation.setSerializedBy(null);
        annotation.setSerializedAt(null);

        // Now remove the annotation
        connection.removeDesignation(annotation, Annotation.class);

        connection.close();
    }

    /**
     * Updates the old annotation with the new one for the configured SPARQL
     * endpoint with a corresponding DELETE query and INSERT query Keep the same
     * resource for the new annotation
     *
     * @param toUpdate annotation to update from the SPARQL endpoint
     * @param anno the new annotation
     * @throws RepositoryException
     */
    public void updateAnnotation(Annotation toUpdate, Annotation anno) throws RepositoryException {
        ObjectConnection connection = objectRepository.getConnection();

        if (graph != null) {
            connection.setReadContexts(graph);
            connection.setInsertContext(graph);
            connection.setRemoveContexts(graph);
        }

        // Keep the same resource for the annotation
        Resource oldResource = toUpdate.getResource();
        anno.setResource(oldResource);

        this.removeAnnotation(toUpdate);
        this.persistAnnotation(anno);

        connection.close();
    }
}
