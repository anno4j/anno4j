package com.github.anno4j.model.impl.agent;

import com.github.anno4j.model.Agent;
import com.github.anno4j.model.ontologies.FOAF;
import com.github.anno4j.model.ontologies.PROV;
import org.openrdf.annotations.Iri;

/**
 * Conforms to http://www.w3.org/ns/prov#Agent
 *
 * An agent is something that bears some form of responsibility for an activity taking place, for the existence of an entity, or for another agent's activity.
 */
@Iri(PROV.AGENT)
public class AgentDefault extends Agent {

    @Iri(FOAF.NAME) private String name;

    public AgentDefault() {};

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "AgentDefault{" +
                "name='" + name + '\'' +
                ", resource='" + getResource() +
                '}';
    }
}
