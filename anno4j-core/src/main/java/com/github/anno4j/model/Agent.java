package com.github.anno4j.model;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.Anno4jNS;
import com.github.anno4j.model.namespaces.FOAF;
import org.openrdf.annotations.Iri;

/**
 * Conforms to http://www.w3.org/ns/prov#Agent
 * An agent is something that bears some form of responsibility for an activity taking place, for the existence of an entity, or for another agent's activity.
 */
@Iri(Anno4jNS.AGENT)
public interface Agent extends ResourceObject {
    /**
     * Sets new The name of the agent.
     * Refers to http://xmlns.com/foaf/spec/#term_name. The name of the agent.
     * @param name New value of The name of the agent..
     */
    @Iri(FOAF.NAME)
    void setName(String name);

    /**
     * Gets The name of the agent..
     * Refers to http://xmlns.com/foaf/spec/#term_name. The name of the agent.
     *
     * @return Value of The name of the agent..
     */
    @Iri(FOAF.NAME)
    String getName();
}
