package com.github.anno4j.model.impl.motivation;

import com.github.anno4j.model.Motivation;
import com.github.anno4j.model.namespaces.OADM;
import org.openrdf.annotations.Iri;

/**
 * http://www.w3.org/ns/oa#tagging
 *
 * The motivation that represents adding a Tag on the target resource(s). One or more of the bodies of the annotation should be typed as a oa:Tag or oa:SemanticTag.
 */
@Iri(OADM.MOTIVATION_TAGGING)
public interface Tagging extends Motivation{

}
