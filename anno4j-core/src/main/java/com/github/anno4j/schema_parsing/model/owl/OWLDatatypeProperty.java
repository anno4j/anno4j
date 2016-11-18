package com.github.anno4j.schema_parsing.model.owl;

import com.github.anno4j.model.namespaces.OWL;
import org.openrdf.annotations.Iri;

/**
 * Refers to http://www.w3.org/2002/07/owl#DatatypeProperty
 * A property linking to a value, rather than another RDF node.
 */
@Iri(OWL.DATATYPE_PROPERTY)
public interface OWLDatatypeProperty extends OWLObjectProperty {
}
