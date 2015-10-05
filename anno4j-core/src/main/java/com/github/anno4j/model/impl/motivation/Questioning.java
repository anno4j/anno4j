package com.github.anno4j.model.impl.motivation;

import com.github.anno4j.model.Motivation;
import com.github.anno4j.model.namespaces.OADM;
import org.openrdf.annotations.Iri;

/**
 * Conforms to http://www.w3.org/ns/oa#questioning
 *
 * The motivation that represents asking a question about the target resource(s). For example to ask for assistance with a particular section of text, or question its veracity.
 */
@Iri(OADM.MOTIVATION_QUESTIONING)
public interface Questioning extends Motivation{

}
