package com.github.anno4j.model.impl.agent;

import com.github.anno4j.model.Agent;
import com.github.anno4j.model.namespaces.FOAF;
import com.github.anno4j.model.namespaces.PROV;
import org.openrdf.annotations.Iri;

/**
 * Conforms to http://www.w3.org/ns/prov#SoftwareAgent
 *
 * A software agent is running software.
 */
@Iri(PROV.SOFTWARE_AGENT)
public class Software extends Agent {

    /**
     * Refers to http://xmlns.com/foaf/spec/#term_homepage
     * homepage - A homepage for some thing.
     */
    @Iri(FOAF.HOMEPAGE) private String homepage;

    /**
     * Standard constructor.
     */
    public Software() {};

    @Override
    public String toString() {
        return "Software{" +
                "resource='" + this.getResource() + "'" +
                ", name='" + this.getName() + "'" +
                ", homepage='" + homepage + '\'' +
                "}";
    }

    /**
     * Sets new Refers to http:xmlns.comfoafspec#term_homepage
     * homepage - A homepage for some thing..
     *
     * @param homepage New value of Refers to http:xmlns.comfoafspec#term_homepage
     *                 homepage - A homepage for some thing..
     */
    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    /**
     * Gets Refers to http:xmlns.comfoafspec#term_homepage
     * homepage - A homepage for some thing..
     *
     * @return Value of Refers to http:xmlns.comfoafspec#term_homepage
     * homepage - A homepage for some thing..
     */
    public String getHomepage() {
        return homepage;
    }
}
