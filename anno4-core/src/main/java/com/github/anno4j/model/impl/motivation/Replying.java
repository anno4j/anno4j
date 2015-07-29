package com.github.anno4j.model.impl.motivation;

import com.github.anno4j.model.Motivation;
import com.github.anno4j.model.ontologies.OADM;
import org.openrdf.annotations.Iri;

/**
 * Conforms to http://www.w3.org/ns/oa#replying
 *
 * The motivation that represents a reply to a previous statement, either an Annotation or another resource. For example providing the assistance requested in the above.
 */
@Iri(OADM.MOTIVATION_REPLYING)
public class Replying extends Motivation{

    /**
     * Print method.
     *
     * @return Returns a textual representation of this class.
     */
    @Override
    public String toString() {
        return "Replying{}" +
                ", resource='" + getResource() + "'";
    }
}
