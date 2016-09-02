package com.github.anno4j;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.querying.QueryService;
import com.github.anno4j.querying.evaluation.LDPathEvaluatorConfiguration;
import org.openrdf.idGenerator.IDGenerator;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectFactory;
import org.openrdf.repository.object.ObjectRepository;

import java.util.List;

public class Transaction implements TransactionCommands {

    private static final URI[] ALL_CONTEXTS = new URI[0];

    private final ObjectConnection connection;
    private final LDPathEvaluatorConfiguration evaluatorConfiguration;

    public Transaction(ObjectRepository objectRepository, LDPathEvaluatorConfiguration evaluatorConfiguration) throws RepositoryException {
        this.connection = objectRepository.getConnection();
        this.evaluatorConfiguration = evaluatorConfiguration;
    }

    /**
     * Indicates if a transaction is currently active on the connection. A
     * transaction is active if {@link #begin()} has been called, and becomes
     * inactive after {@link #commit()} or {@link #rollback()} has been called.
     * @return <code>true</code> iff a transaction is active, <code>false</code> iff no transaction is active.
     */
    public boolean isActive() throws RepositoryException {
        return connection.isActive();
    }

    public void begin() throws RepositoryException {
        connection.begin();
    }

    /**
     * Commits the active transaction. This operation ends the active
     * transaction.
     */
    public void commit() throws RepositoryException {
        connection.commit();
    }

    /**
     * Rolls back all updates in the active transaction. This operation ends the
     * active transaction.
     */
    public void rollback() throws RepositoryException {
        connection.rollback();
    }

    /**
     * {@inheritDoc }
     */
    public void setReadContexts(URI... contexts) {
        if(contexts != null) {
            connection.setReadContexts(contexts);
        } else {
            connection.setReadContexts(ALL_CONTEXTS);
        }
    }

    /**
     * {@inheritDoc }
     */
    public void setInsertContext(URI context) {
        connection.setInsertContext(context);
     }

    /**
     * {@inheritDoc }
     */
    public void setRemoveContexts(URI... contexts) {
        if(contexts != null) {
            connection.setRemoveContexts(contexts);
        } else {
            connection.setRemoveContexts(ALL_CONTEXTS);
        }
    }

    /**
     * {@inheritDoc }
     */
    public void setAllContexts(URI context) {
        if(context != null) {
            connection.setReadContexts(context);
            connection.setInsertContext(context);
            connection.setRemoveContexts(context);
        } else {
            connection.setReadContexts(ALL_CONTEXTS);
            connection.setInsertContext(null);
            connection.setRemoveContexts(ALL_CONTEXTS);
        }
    }

    /**
     * {@inheritDoc }
     */
    public URI[] getReadContexts() {
        return connection.getReadContexts();
    }

    /**
     * {@inheritDoc }
     */
    public URI getInsertContext() {
        return connection.getInsertContext();
    }

    /**
     * {@inheritDoc }
     */
    public URI[] getRemoveContexts() {
        return connection.getRemoveContexts();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void persist(ResourceObject resource) throws RepositoryException {
        connection.addObject(resource);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public <T extends ResourceObject> T findByID(Class<T> type, String id) throws RepositoryException {
        try {
            return this.getConnection().findObject(type, new URIImpl(id));
        } catch (QueryEvaluationException e) {
            throw new RepositoryException("Couldn't evaluate query", e);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public <T extends ResourceObject> T findByID(Class<T> type, URI id) throws RepositoryException {
        return findByID(type, id.toString());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void clearContext(URI context) throws RepositoryException {
        connection.clear(context);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void clearContext(String context) throws RepositoryException {
        this.clearContext(new URIImpl(context));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public <T extends ResourceObject> List<T> findAll(Class<T> type) throws RepositoryException {
        try {
            return connection.getObjects(type).asList();
        } catch (QueryEvaluationException e) {
            throw new RepositoryException("Couldn't evaluate query" , e);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public QueryService createQueryService() {
        return new QueryService(connection, evaluatorConfiguration);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public <T> T createObject(Class<T> clazz) throws RepositoryException, IllegalAccessException, InstantiationException {
        return createObject(clazz, null);
    }

    @Override
    public <T> T createObject(Class<T> clazz, Resource id) throws RepositoryException, IllegalAccessException, InstantiationException {
        ObjectFactory objectFactory = connection.getObjectFactory();

        Resource resource = (id != null) ? id : IDGenerator.BLANK_RESOURCE;

        T object = objectFactory.createObject(resource, clazz);
        return connection.addDesignation(object, clazz);
    }

    public void close() throws RepositoryException {
        connection.close();
    }

    public ObjectConnection getConnection() {
        return this.connection;
    }
}
