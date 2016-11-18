package com.github.anno4j.schema_parsing.model.owl;

import com.github.anno4j.model.namespaces.OWL;
import org.openrdf.annotations.Iri;

/**
 * Refers to http://www.w3.org/2002/07/owl#InverseFunctionalProperty
 * Properties may be stated to be inverse functional. If a property is inverse functional then the inverse of the
 * property is functional. Thus the inverse of the property has at most one value for each individual. This
 * characteristic has also been referred to as an unambiguous property.
 */
@Iri(OWL.INVERSE_FUNCTIONAL_PROPERTY)
public interface OWLInverseFunctionalProperty extends OWLObjectProperty {
}
