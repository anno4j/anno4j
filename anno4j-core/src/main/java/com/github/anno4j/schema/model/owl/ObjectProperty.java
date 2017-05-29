package com.github.anno4j.schema.model.owl;

import com.github.anno4j.model.namespaces.OWL;
import com.github.anno4j.schema.model.rdfs.RDFSProperty;
import org.openrdf.annotations.Iri;

import java.util.Set;

/**
 * An object property is a property linking an individual to an individual.
 */
@Iri(OWL.OBJECT_PROPERTY)
public interface ObjectProperty extends RDFSProperty {

    /**
     * Refers to http://www.w3.org/2002/07/owl#inverseOf
     * @return Returns those properties that are inverse to this one.
     */
    @Iri(OWL.INVERSE_OF)
    public Set<ObjectProperty> getInverseOf();

    /**
     * Refers to http://www.w3.org/2002/07/owl#inverseOf
     * @param inverseProperties Those properties that are inverse to this one.
     */
    public void setInverseOf(Set<ObjectProperty> inverseProperties);
}
