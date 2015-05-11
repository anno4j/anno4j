package com.github.anno4j.persistence;

import com.github.anno4j.model.Annotation;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectRepository;

/**
 * This class provides write access for annotated POJOs. Annotated objects will be converted to corresponding RDF content and then transmitted to the connected SPARQL endpoint.
 */
public class PersistenceService {

    /**
     * Local/Remote SPARQL endpoint connection
     */
    private ObjectRepository objectRepository;

    /**
     * Constructor
     * @param objectRepository Local/Remote SPARQL endpoint connection
     */
    public PersistenceService(ObjectRepository objectRepository) {
        this.objectRepository = objectRepository;
    }

    /**
     * Writes the annotation to the configured SPARQL endpoint with a corresponding INSERT query.
     * @param annotation annotation to write to the SPARQL endpoint
     * @throws RepositoryException
     */
    public void persistAnnotation(Annotation annotation) throws RepositoryException {
        ObjectConnection connection = objectRepository.getConnection();
        connection.addObject(annotation);
        connection.close();
    }
}
