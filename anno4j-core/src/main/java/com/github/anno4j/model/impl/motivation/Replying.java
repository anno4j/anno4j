package com.github.anno4j.model.impl.motivation;

import com.github.anno4j.model.Motivation;
import com.github.anno4j.model.namespaces.OADM;
import org.openrdf.annotations.Iri;

/**
 * Conforms to http://www.w3.org/ns/oa#replying
 *
 * The motivation that represents a reply to a previous statement, either an Annotation or another resource. For example providing the assistance requested in the above.
 */
@Iri(OADM.MOTIVATION_REPLYING)
public interface Replying extends Motivation{

}
