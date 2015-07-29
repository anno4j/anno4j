package com.github.anno4j.model;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.ontologies.FOAF;
import org.openrdf.annotations.Iri;
import org.openrdf.model.Resource;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.RDFObject;

/**
 * Conforms to http://www.w3.org/ns/prov#Agent
 * An agent is something that bears some form of responsibility for an activity taking place, for the existence of an entity, or for another agent's activity.
 */
public abstract class Agent extends ResourceObject {

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

    @Override
    public String toString() {
        return "Agent{" +
                "resource='" + this.getResource() + "'" +
                ", name='" + name + '\'' +
                '}';
    }
}
