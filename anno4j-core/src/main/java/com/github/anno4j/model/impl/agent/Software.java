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
public interface Software extends Agent {
    /**
     * Sets new Refers to http:xmlns.comfoafspec#term_homepage
     * homepage - A homepage for some thing..
     *
     * @param homepage New value of Refers to http:xmlns.comfoafspec#term_homepage
     *                 homepage - A homepage for some thing..
     */
    @Iri(FOAF.HOMEPAGE)
    void setHomepage(String homepage);

    /**
     * Gets Refers to http:xmlns.comfoafspec#term_homepage
     * homepage - A homepage for some thing..
     *
     * @return Value of Refers to http:xmlns.comfoafspec#term_homepage
     * homepage - A homepage for some thing..
     */
    @Iri(FOAF.HOMEPAGE)
    String getHomepage();
}
