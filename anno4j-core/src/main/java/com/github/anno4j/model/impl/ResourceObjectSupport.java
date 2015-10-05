package com.github.anno4j.model.impl;

import com.github.anno4j.persistence.annotation.Partial;
import com.github.anno4j.persistence.impl.StaticIDGenerator;
import org.openrdf.annotations.ParameterTypes;
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
import org.openrdf.repository.object.traits.ObjectMessage;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.ntriples.NTriplesWriter;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sail.memory.model.MemValueFactory;

import java.io.StringWriter;

@Partial
public abstract class ResourceObjectSupport implements ResourceObject, RDFObject {

    private Resource resource = StaticIDGenerator.getInstance().getIdGenerator().generateID();;

    @Override
    public String getNTriples(){
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


    @Override
    public void setResource(Resource resource) {
        this.resource = resource;

    }

    @Override
    public ObjectConnection getObjectConnection() {
        return null;
    }

    /**
     * Gets Unique identifier for the instance.
     *
     * @return Value of Unique identifier for the instance..
     */
    @ParameterTypes({})
    public Resource getResource(ObjectMessage msg) throws Exception {
        Resource proceed = (Resource) msg.proceed();

        if(proceed == null ) {
            return this.resource;
        } else if (proceed.equals(new MemValueFactory().createURI("urn:anno4j:BLANK"))) {
            return this.resource;
        } else  {
            return proceed;
        }
    }

    /**
     * Sets new Unique identifier for the instance by a given String.
     *
     * @param resourceAsString Textual representation of the new value of Unique identifier for the instance.
     */
    @Override
    public void setResourceAsString(String resourceAsString) {
        this.setResource(new URIImpl(resourceAsString));
    }

    /**
     * Gets new identifier for this instance as String.
     * @return identifier as String.
     */
    public String getResourceAsString() {
        return getResource().stringValue();
    }

}
