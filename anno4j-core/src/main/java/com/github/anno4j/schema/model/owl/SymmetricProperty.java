package com.github.anno4j.schema.model.owl;

import com.github.anno4j.model.namespaces.OWL;
import org.openrdf.annotations.Iri;

/**
 * A symmetric property is a property for which holds that if the pair (x,y) is an instance of P,
 * then the pair (y,x) is also an instance of P.
 */
@Iri(OWL.SYMMETRIC_PROPERTY)
public interface SymmetricProperty extends ObjectProperty {
}
