package com.github.anno4j.model;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.ontologies.FOAF;
import org.openrdf.annotations.Iri;
import org.openrdf.model.Resource;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.RDFObject;

/**
 * Conforms to http://www.w3.org/ns/prov#Agent
 * An agent is something that bears some form of responsibility for an activity taking place, for the existence of an entity, or for another agent's activity.
 */
public abstract class Agent implements RDFObject {

    /**
     * Unique identifier for the instance.
     */
    private Resource resource = Anno4j.getInstance().getIdGenerator().generateID();

    /**
     * Refers to http://xmlns.com/foaf/spec/#term_name. The name of the agent.
     */
    @Iri(FOAF.NAME) private String name;

    /**
     * Constructor.
     */
    public Agent() {

    }

    /**
     *  The current {@link ObjectConnection} this object is atached to. Will be implemented by the proxy object.
     */
    @Override
    public ObjectConnection getObjectConnection() {
        // will be implemented by the proxy object
        return null;
    }

    /**
     * Getter for the unique identifier.
     * @return a unique identifier for this instance.
     */
    @Override
    public Resource getResource() {
        return this.resource;
    }

    /**
     * Setter for the unique identifier.
     * @param resource the unique identifier.
     */
    public void setResource(Resource resource) { this.resource = resource; }

    /**
     * Sets new The name of the agent..
     *
     * @param name New value of The name of the agent..
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets The name of the agent..
     *
     * @return Value of The name of the agent..
     */
    public String getName() {
        return name;
    }
}
