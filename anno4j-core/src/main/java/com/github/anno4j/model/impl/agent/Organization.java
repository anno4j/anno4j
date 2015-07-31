package com.github.anno4j.model.impl.agent;

import com.github.anno4j.model.Agent;
import com.github.anno4j.model.namespaces.FOAF;
import org.openrdf.annotations.Iri;

/**
 * Conforms to http://xmlns.com/foaf/spec/#term_Organization
 *
 * An organization.
 */
@Iri(FOAF.ORGANIZATION)
public class Organization extends Agent {

    /**
     * Standard constructor.
     */
    public Organization() {}

    @Override
    public String toString() {
        return "Organization{" +
                "resource='" + this.getResource() + "'" +
                ", name='" + this.getName() +
                "'}";
    }
}
