package com.github.anno4j.model;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.OADM;
import org.openrdf.annotations.Iri;

/**
 * Conforms to OADM Motivation (http://www.openannotation.org/spec/core/core.html#Motivations)
 */
@Iri(OADM.MOTIVATION)
public interface Motivation extends ResourceObject {

}
