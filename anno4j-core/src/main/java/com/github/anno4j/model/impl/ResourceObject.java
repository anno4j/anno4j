package com.github.anno4j.model.impl;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.namespaces.RDFS;
import org.openrdf.annotations.Iri;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.RDFObject;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.rio.ntriples.NTriplesWriter;
import org.openrdf.sail.memory.MemoryStore;

import javax.swing.plaf.nimbus.State;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

/**
 * Class to implement RDF in order to create a baseline for every object that we use in Anno4j.
 */
@Iri(RDFS.RESOURCE)
public class ResourceObject implements RDFObject {

    /**
     * Unique identifier for the instance.
     */
    private Resource resource = Anno4j.getInstance().getIdGenerator().generateID();

    /**
     * The current {@link org.openrdf.repository.object.ObjectConnection} this object is attached to. Will be implemented by the proxy object.
     */
    @Override
    public ObjectConnection getObjectConnection() {
        // will be implemented by the proxy object
        return null;
    }

    /**
     * Default constructor
     */
    public ResourceObject() {
    }

    ;

    /**
     * Constructor also setting the resource, which is supported by the corresponding String.
     *
     * @param resource String representation of the resource to set.
     */
    public ResourceObject(String resource) {
        this.setResourceAsString(resource);
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
     *
     * @return identifier as String.
     */
    public String getResourceAsString() {
        return this.resource.stringValue();
    }

    @Deprecated
    public String getNTriples() {
        StringWriter stringWriter = new StringWriter();
        NTriplesWriter rdfWriter = new NTriplesWriter(stringWriter);
        try {
            MemoryStore store = new MemoryStore();
            Repository sailRepository = new SailRepository(store);
            sailRepository.initialize();
            ObjectRepository objectRepository = new ObjectRepositoryFactory().createRepository(sailRepository);
            ObjectConnection connection = objectRepository.getConnection();
            connection.addObject(this);


            GraphQueryResult result = sailRepository.getConnection().prepareGraphQuery(QueryLanguage.SPARQL, "CONSTRUCT { ?s ?p ?o. } WHERE { ?s ?p ?o. } ").evaluate();

            rdfWriter.startRDF();

            while (result.hasNext()) {
                Statement item = result.next();
                rdfWriter.handleStatement(item);
            }

            result.close();
            connection.close();
            rdfWriter.endRDF();

        } catch (RepositoryException e) {
            e.printStackTrace();
        } catch (RepositoryConfigException e) {
            e.printStackTrace();
        } catch (MalformedQueryException e) {
            e.printStackTrace();
        } catch (QueryEvaluationException e) {
            e.printStackTrace();
        } catch (RDFHandlerException e) {
            e.printStackTrace();
        }

        return stringWriter.toString();
    }

    /**
     * Method returns a textual representation of the given ResourceObject in a supported serialisation format.
     *
     * @param format    The format which should be printed.
     * @return          A textual representation if this object in the format.
     */
    public String getTriples(RDFFormat format) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        RDFWriter writer = Rio.createWriter(format, out);

        try {
            MemoryStore store = new MemoryStore();
            Repository sailRepository = new SailRepository(store);
            sailRepository.initialize();
            ObjectRepository objectRepository = new ObjectRepositoryFactory().createRepository(sailRepository);
            ObjectConnection connection = objectRepository.getConnection();
            connection.addObject(this);

            RepositoryResult<Statement> statements = connection.getStatements(null, null, null, false);

            writer.startRDF();

            while(statements.hasNext()) {
                Statement statement = statements.next();
                writer.handleStatement(statement);
            }

            writer.endRDF();
        } catch (RepositoryException e) {
            e.printStackTrace();
        } catch (RepositoryConfigException e) {
            e.printStackTrace();
        } catch (RDFHandlerException e) {
            e.printStackTrace();
        }

        return out.toString();
    }
}
