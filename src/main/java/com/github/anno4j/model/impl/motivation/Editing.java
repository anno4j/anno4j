package com.github.anno4j.model.impl.motivation;

import com.github.anno4j.model.Motivation;
import com.github.anno4j.model.ontologies.OADM;
import org.openrdf.annotations.Iri;

/**
 * Conforms to http://www.w3.org/ns/oa#editing
 *
 * The motivation that represents a request for a modification or edit to the target resource. For example, an Annotation that requests a typo to be corrected.
 */
@Iri(OADM.MOTIVATION_EDITING)
public class Editing extends Motivation{

    /**
     * Print method.
     *
     * @return Returns a textual representation of this class.
     */
    @Override
    public String toString() {
        return "Editing{}" +
                ", resource='" + getResource() + "'";
    }
}
