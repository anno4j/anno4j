package com.github.anno4j.schema_parsing.model.owl;

import com.github.anno4j.model.namespaces.OWL;
import org.openrdf.annotations.Iri;

/**
 * Refers to http://www.w3.org/2002/07/owl#SymmetricProperty
 * Properties may be stated to be symmetric. If a property is symmetric, then if the pair (x,y) is an instance of
 * the symmetric property P, then the pair (y,x) is also an instance of P.
 */
@Iri(OWL.SYMMETRIC_PROPERTY)
public interface OWLSymmetricProperty extends OWLObjectProperty {
}
