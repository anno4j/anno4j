package com.github.anno4j.model.impl.motivation;

import com.github.anno4j.model.Motivation;
import com.github.anno4j.model.ontologies.OADM;
import org.openrdf.annotations.Iri;

/**
 * Conforms to http://www.w3.org/ns/oa#commenting
 *
 * The motivation that represents a commentary about or review of the target resource(s). For example to provide a commentary about a particular PDF.
 */
@Iri(OADM.MOTIVATION_COMMENTING)
public class Commenting extends Motivation {

    @Override
    public String toString() {
        return "Commenting{}" +
            ", resource='" + getResource();
    }
}
