package com.github.anno4j.schema_parsing.model;

import com.github.anno4j.model.namespaces.RDF;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.github.anno4j.schema.model.rdfs.RDFSProperty;
import com.github.anno4j.schema_parsing.building.BuildableOntologyProperty;
import org.openrdf.annotations.Iri;
import org.openrdf.repository.RepositoryException;

import java.util.Set;

/**
 * Represents a rdfs:Property.
 * This class has additional members in order to enable Java file generation
 * from RDF data.
 */
@Iri(RDF.PROPERTY)
public interface BuildableRDFSProperty extends RDFSProperty, BuildableOntologyProperty {

    /**
     * Returns the superproperties of this property.
     * Thus this property denotes the subject and each of the returned resources denotes the
     * object in a statement with predicate rdfs:subPropertyOf.
     * @return The superproperties of this property.
     */
    Set<RDFSProperty> getSubProperties() throws RepositoryException;

    /**
     * Returns the cardinality set for the property declared at the given class.
     * @return Returns the cardinality or null if there is no fixed cardinality set.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    Integer getCardinality(RDFSClazz domainClazz) throws RepositoryException;
}
