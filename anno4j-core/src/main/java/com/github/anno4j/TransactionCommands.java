package com.github.anno4j;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.querying.QueryService;
import com.github.anno4j.querying.objectqueries.ObjectQueryService;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.object.ObjectRepository;

import java.util.List;

public interface TransactionCommands {
    /**
     * Writes the resource object to the configured SPARQL endpoint with a corresponding INSERT query.
     * @param resource resource object to write to the SPARQL endpoint
     * @throws RepositoryException
     */
    void persist(ResourceObject resource) throws RepositoryException;

    <T extends ResourceObject> T findByID(Class<T> type, String id) throws RepositoryException;

    <T extends ResourceObject> T findByID(Class<T> type, URI id) throws RepositoryException;

    /**
     * Removes all triples from the given context.
     * @param context context to clear
     * @throws RepositoryException
     */
    void clearContext(URI context) throws RepositoryException;

    /**
     * Removes all triples from the given context.
     * @param context context to clear
     * @throws RepositoryException
     */
    void clearContext(String context) throws RepositoryException;

    /**
     * Queries for all instances of the RDF class connected with the given class
     * @param type Class with connected RDF type
     * @return All instances of the given RDF type
     * @throws RepositoryException
     */
    <T extends ResourceObject> List<T> findAll(Class<T> type) throws RepositoryException;

    /**
     * Create query service
     *
     * @return query service object for specified type
     */
    QueryService createQueryService() throws RepositoryException;

    ObjectQueryService createObjectQueryService() throws RepositoryException, RepositoryConfigException;

    <T> T createObject(Class<T> clazz) throws RepositoryException, IllegalAccessException, InstantiationException;

    <T> T createObject(Class<T> clazz, Resource id) throws RepositoryException, IllegalAccessException, InstantiationException;
}
