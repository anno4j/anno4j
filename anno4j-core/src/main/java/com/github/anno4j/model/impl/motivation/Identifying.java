package com.github.anno4j.model.impl.motivation;

import com.github.anno4j.model.Motivation;
import com.github.anno4j.model.namespaces.OADM;
import org.openrdf.annotations.Iri;

/**
 * Conforms to http://www.w3.org/ns/oa#identifying
 *
 * The motivation that represents the assignment of an identity to the target resource(s). For example, annotating the name of a city in a string of text with the URI that identifies it.
 */
@Iri(OADM.MOTIVATION_IDENTIFYING)
public class Identifying extends Motivation {

    /**
     * Print method.
     *
     * @return Returns a textual representation of this class.
     */
    @Override
    public String toString() {
        return "Identifying{}" +
                ", resource='" + getResource() + "'";
    }
}
