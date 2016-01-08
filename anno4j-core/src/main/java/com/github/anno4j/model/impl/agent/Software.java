package com.github.anno4j.model.impl.agent;

import com.github.anno4j.model.Agent;
import com.github.anno4j.model.namespaces.FOAF;
import com.github.anno4j.model.namespaces.PROV;
import java.io.ByteArrayOutputStream;
import org.openrdf.annotations.Iri;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;

/**
 * Conforms to http://www.w3.org/ns/prov#SoftwareAgent
 *
 * A software agent is running software.
 */
@Iri(PROV.SOFTWARE_AGENT)
public abstract class Software implements Agent {
    /**
     * Sets new Refers to http:xmlns.comfoafspec#term_homepage
     * homepage - A homepage for some thing..
     *
     * @param homepage New value of Refers to http:xmlns.comfoafspec#term_homepage
     *                 homepage - A homepage for some thing..
     */
    @Iri(FOAF.HOMEPAGE)
    public abstract void setHomepage(String homepage);

    /**
     * Gets Refers to http:xmlns.comfoafspec#term_homepage
     * homepage - A homepage for some thing..
     *
     * @return Value of Refers to http:xmlns.comfoafspec#term_homepage
     * homepage - A homepage for some thing..
     */
    @Iri(FOAF.HOMEPAGE)
    public abstract String getHomepage();

    @Override
    public String getTriples(RDFFormat format) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            RDFWriter writer = Rio.createWriter(format, out);
            this.getObjectConnection().exportStatements(this.getResource(), null, null, true, writer);

        } catch (RepositoryException | RDFHandlerException e) {
            e.printStackTrace();
        }
        return out.toString();
    }
}
