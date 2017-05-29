package com.github.anno4j.schema.model.owl;

import com.github.anno4j.model.namespaces.OWL;
import org.openrdf.annotations.Iri;

/**
 * If a property is declared to be inverse-functional,
 * then the object of a property statement uniquely determines the subject (some individual).
 */
@Iri(OWL.INVERSE_FUNCTIONAL_PROPERTY)
public interface InverseFunctionalProperty extends ObjectProperty {
}
