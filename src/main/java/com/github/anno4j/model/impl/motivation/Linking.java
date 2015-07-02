package com.github.anno4j.model.impl.motivation;

import com.github.anno4j.model.Motivation;
import com.github.anno4j.model.ontologies.OADM;
import org.openrdf.annotations.Iri;

/**
 * Conforms to http://www.w3.org/ns/oa#linking
 *
 * The motivation that represents an untyped link to a resource related to the target.
 */
@Iri(OADM.MOTIVATION_LINKING)
public class Linking extends Motivation {

    /**
     * Print method.
     *
     * @return Returns a textual representation of this class.
     */
    @Override
    public String toString() {
        return "Linking{}" +
                ", resource='" + getResource() + "'";
    }
}
