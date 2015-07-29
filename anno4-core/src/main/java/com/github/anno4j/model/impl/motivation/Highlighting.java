package com.github.anno4j.model.impl.motivation;

import com.github.anno4j.model.Motivation;
import com.github.anno4j.model.ontologies.OADM;
import org.openrdf.annotations.Iri;

/**
 * Conforms to http://www.w3.org/ns/oa#highlighting
 *
 * The motivation that represents a highlighted section of the target resource or segment. For example to draw attention to the selected text that the annotator disagrees with. A Highlight may or may not have a Body resource.
 */
@Iri(OADM.MOTIVATION_HIGHLIGHTING)
public class Highlighting extends Motivation {

    /**
     * Print method.
     *
     * @return Returns a textual representation of this class.
     */
    @Override
    public String toString() {
        return "Highlighting{}" +
                ", resource='" + getResource() + "'";
    }
}
