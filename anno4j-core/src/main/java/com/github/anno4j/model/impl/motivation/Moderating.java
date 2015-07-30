package com.github.anno4j.model.impl.motivation;

import com.github.anno4j.model.Motivation;
import com.github.anno4j.model.namespaces.OADM;
import org.openrdf.annotations.Iri;

/**
 * Conforms to http://www.w3.org/ns/oa#moderating
 *
 * The motivation that represents an assignment of value or quality to the target resource(s). For example annotating an Annotation to moderate it up in a trust network or threaded discussion.
 */
@Iri(OADM.MOTIVATION_MODERATING)
public class Moderating extends Motivation{

    /**
     * Print method.
     *
     * @return Returns a textual representation of this class.
     */
    @Override
    public String toString() {
        return "Moderating{}" +
                ", resource='" + getResource() + "'";
    }
}
