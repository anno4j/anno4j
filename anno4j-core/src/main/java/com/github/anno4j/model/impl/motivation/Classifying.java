package com.github.anno4j.model.impl.motivation;

import com.github.anno4j.model.Motivation;
import com.github.anno4j.model.namespaces.OADM;
import org.openrdf.annotations.Iri;

/**
 * Conforms to http://www.w3.org/ns/oa#classifying
 *
 * The motivation that represents the assignment of a classification type, typically from a controlled vocabulary, to the target resource(s). For example to classify an Image resource as a Portrait.
 */
@Iri(OADM.MOTIVATION_CLASSIFYING)
public interface Classifying extends Motivation {

}
