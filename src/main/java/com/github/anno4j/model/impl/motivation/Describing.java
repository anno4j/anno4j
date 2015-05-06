package com.github.anno4j.model.impl.motivation;

import com.github.anno4j.model.Motivation;
import com.github.anno4j.model.ontologies.OADM;
import org.openrdf.annotations.Iri;

/**
 * Conforms to http://www.w3.org/ns/oa#describing
 *
 * The motivation that represents a description of the target resource(s), as opposed to a comment about them. For example describing the above PDF's contents, rather than commenting on their accuracy.
 */
@Iri(OADM.MOTIVATION_DESCRIBING)
public class Describing extends Motivation {

    @Override
    public String toString() {
        return "Describing{}" +
                ", resource='" + getResource();
    }
}
