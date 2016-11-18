package com.github.anno4j.schema_parsing.model.owl;

import com.github.anno4j.model.namespaces.OWL;
import org.openrdf.annotations.Iri;

/**
 * Refers to http://www.w3.org/2002/07/owl#TransitiveProperty
 * Properties may be stated to be transitive. If a property is transitive, then if the pair (x,y) is an instance of
 * the transitive property P, and the pair (y,z) is an instance of P, then the pair (x,z) is also an instance of P.
 */
@Iri(OWL.TRANSITIVE_PROPERTY)
public interface OWLTransitiveProperty extends OWLObjectProperty {
}
