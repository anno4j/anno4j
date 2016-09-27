package com.github.anno4j.mico.model;

import com.github.anno4j.mico.namespace.MMM;
import com.github.anno4j.model.Body;
import org.openrdf.annotations.Iri;

/**
 * Class represents the overall Body class for MICO specific bodies.
 */
@Iri(MMM.BODY)
public interface BodyMMM extends Body {
}
