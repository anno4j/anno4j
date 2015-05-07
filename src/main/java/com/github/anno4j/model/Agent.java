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

    private Resource resource = Anno4j.getInstance().getIdGenerator().generateID();

    @Iri(FOAF.NAME)
    private String name;

    public Agent() {};

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public ObjectConnection getObjectConnection() {
        // will be implemented by the proxy object
        return null;
    }

    @Override
    public Resource getResource() {
        return this.resource;
    }
}
