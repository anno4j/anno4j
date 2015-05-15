package com.github.anno4j.model.impl.agent;

import com.github.anno4j.model.Agent;
import com.github.anno4j.model.ontologies.FOAF;
import com.github.anno4j.model.ontologies.PROV;
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

    /**
     * Print method.
     *
     * @return Returns a textual representation of this class.
     */
    @Override
    public String toString() {
        return "Software{" +
                "name='" + this.getName() + '\'' +
                ", homepage='" + homepage + '\'' +
                ", resource='" + getResource() +
                '}';
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
