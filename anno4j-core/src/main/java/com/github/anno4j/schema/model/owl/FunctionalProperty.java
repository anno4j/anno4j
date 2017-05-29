package com.github.anno4j.schema.model.owl;

import com.github.anno4j.model.namespaces.OWL;
import com.github.anno4j.schema.model.rdfs.RDFSProperty;
import org.openrdf.annotations.Iri;

/**
 * A functional property is a property that can have only one (unique) value y for each instance x,
 * i.e. there cannot be two distinct values y1 and y2 such that the pairs (x,y1) and (x,y2) are both instances of this property.
 */
@Iri(OWL.FUNCTIONAL_PROPERTY)
public interface FunctionalProperty extends RDFSProperty {
}
