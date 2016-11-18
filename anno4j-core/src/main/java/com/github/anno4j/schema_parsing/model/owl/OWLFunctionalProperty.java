package com.github.anno4j.schema_parsing.model.owl;

import com.github.anno4j.model.namespaces.OWL;
import org.openrdf.annotations.Iri;

/**
 * Refers to http://www.w3.org/2002/07/owl#FunctionalProperty
 * Properties may be stated to have a unique value. If a property is a FunctionalProperty, then it has no more than
 * one value for each individual (it may have no values for an individual). This characteristic has been referred
 * to as having a unique property. FunctionalProperty is shorthand for stating that the property's minimum
 * cardinality is zero and its maximum cardinality is 1.
 */
@Iri(OWL.FUNCTIONAL_PROPERTY)
public interface OWLFunctionalProperty extends OWLObjectProperty {
}
