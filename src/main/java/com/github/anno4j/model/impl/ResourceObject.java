package com.github.anno4j.model.impl;

import com.github.anno4j.Anno4j;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.RDFObject;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

/**
 * Class to implement RDF in order to create a baseline for every object that we use in Anno4j.
 */
public class ResourceObject implements RDFObject {

    /**
     * Unique identifier for the instance.
     */
    private Resource resource = Anno4j.getInstance().getIdGenerator().generateID();

    /**
     *  The current {@link org.openrdf.repository.object.ObjectConnection} this object is attached to. Will be implemented by the proxy object.
     */
    @Override
    public ObjectConnection getObjectConnection() {
        // will be implemented by the proxy object
        return null;
    }

    /**
     * Gets Unique identifier for the instance.
     *
     * @return Value of Unique identifier for the instance..
     */
    public Resource getResource() {
        return resource;
    }

    /**
     * Sets new Unique identifier for the instance.
     *
     * @param resource New value of Unique identifier for the instance..
     */
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    /**
     * Sets new Unique identifier for the instance by a given String.
     *
     * @param resourceAsString Textual representation of the new value of Unique identifier for the instance.
     */
    public void setResourceAsString(String resourceAsString) {
        this.resource = new URIImpl(resourceAsString);
    }

    /**
     * Gets new identifier for this instance as String.
     * @return identifier as String.
     */
    public String getResourceAsString() {
        return this.resource.stringValue();
    }

    public String getNTriples(){
        StringBuilder sb = new StringBuilder();
        try {
            MemoryStore store = new MemoryStore();
            Repository sailRepository = new SailRepository(store);
            sailRepository.initialize();
            ObjectRepository objectRepository = new ObjectRepositoryFactory().createRepository(sailRepository);
            ObjectConnection connection = objectRepository.getConnection();
            connection.addObject(this);
            GraphQueryResult result = sailRepository.getConnection().prepareGraphQuery(QueryLanguage.SPARQL, "CONSTRUCT { ?s ?p ?o. } WHERE { ?s ?p ?o. } ").evaluate();
            while (result.hasNext()) {
                Statement item = result.next();

                sb
                        .append(item.getSubject())
                        .append(" ")
                        .append(item.getPredicate())
                        .append(" ")
                        .append(item.getObject())
                        .append(".")
                        .append(System.getProperty("line.separator"));
            }
            result.close();
            connection.close();
        } catch (RepositoryException e) {
            e.printStackTrace();
        } catch (RepositoryConfigException e) {
            e.printStackTrace();
        } catch (MalformedQueryException e) {
            e.printStackTrace();
        } catch (QueryEvaluationException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}
