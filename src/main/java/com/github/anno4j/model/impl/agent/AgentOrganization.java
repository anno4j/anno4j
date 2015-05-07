package com.github.anno4j.model.impl.agent;

import com.github.anno4j.model.Agent;
import com.github.anno4j.model.ontologies.FOAF;
import org.openrdf.annotations.Iri;

/**
 * Conforms to http://xmlns.com/foaf/spec/#term_Organization
 *
 * An organization.
 */
@Iri(FOAF.ORGANIZATION)
public class AgentOrganization extends Agent {

    public AgentOrganization() {};

    @Override
    public String toString() {
        return "AgentOrganization{}"  + '\'' +
                "name='" + this.getName() + '\'' +
                ", resource='" + getResource() +
                '}';
    }
}
