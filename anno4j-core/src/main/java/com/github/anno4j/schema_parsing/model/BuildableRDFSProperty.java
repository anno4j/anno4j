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
     * The cardinality can be specified explicitly through {@code owl:cardinality} or implicitly
     * through {@code owl:minCardinality} and {@code owl:maxCardinality} with the same value.
     * @param domainClazz The class for which to retrieve the cardinality.
     * @return Returns the cardinality or null if there is no fixed cardinality set.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    Integer getCardinality(RDFSClazz domainClazz) throws RepositoryException;

    /**
     * Returns the maximum cardinality of the property declared at the given class.
     * The maximum cardinality can be specified explicitly through {@code owl:maxCardinality} or
     * implicitly through a fixed cardinality ({@code owl:cardinality}).
     * @param domainClazz The class for which to retrieve the minimum cardinality.
     * @return Returns the cardinality or null if there is no upper bound on the cardinality of this property
     * in context of the given class.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    Integer getMaximumCardinality(RDFSClazz domainClazz) throws RepositoryException;
}
