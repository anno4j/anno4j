package com.github.anno4j.model.impl.motivation;

import com.github.anno4j.model.Motivation;
import com.github.anno4j.model.ontologies.OADM;
import org.openrdf.annotations.Iri;

/**
 * Conforms to http://www.w3.org/ns/oa#questioning
 *
 * The motivation that represents asking a question about the target resource(s). For example to ask for assistance with a particular section of text, or question its veracity.
 */
@Iri(OADM.MOTIVATION_QUESTIONING)
public class Questioning extends Motivation{

    /**
     * Print method.
     *
     * @return Returns a textual representation of this class.
     */
    @Override
    public String toString() {
        return "Questioning{}" +
                ", resource='" + getResource();
    }
}
