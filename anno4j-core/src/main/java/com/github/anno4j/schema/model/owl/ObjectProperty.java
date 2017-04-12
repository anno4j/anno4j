package com.github.anno4j.schema.model.owl;

import com.github.anno4j.model.namespaces.OWL;
import com.github.anno4j.schema.model.rdfs.RDFSProperty;
import org.openrdf.annotations.Iri;

/**
 * An object property is a property linking an individual to an individual.
 */
@Iri(OWL.OBJECT_PROPERTY)
public interface ObjectProperty extends RDFSProperty {
}
