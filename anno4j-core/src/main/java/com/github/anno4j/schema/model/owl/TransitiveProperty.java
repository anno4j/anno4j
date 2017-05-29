package com.github.anno4j.schema.model.owl;

import com.github.anno4j.model.namespaces.OWL;
import org.openrdf.annotations.Iri;

/**
 * When one defines a property P to be a transitive property, this means that if a pair (x,y) is an instance of P,
 * and the pair (y,z) is also instance of P, then we can infer the the pair (x,z) is also an instance of P.
 */
@Iri(OWL.TRANSITIVE_PROPERTY)
public interface TransitiveProperty extends ObjectProperty {
}
