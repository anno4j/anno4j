package com.github.anno4j.schema_parsing.model;

import com.github.anno4j.model.namespaces.RDF;
import com.github.anno4j.schema.model.rdfs.RDFSProperty;
import com.github.anno4j.schema_parsing.building.BuildableOntologyProperty;
import org.openrdf.annotations.Iri;

import java.util.Set;

/**
 * Represents a rdfs:Property.
 * This class has additional members in order to enable Java file generation
 * from RDF data.
 */
@Iri(RDF.PROPERTY)
public interface ExtendedRDFSProperty extends RDFSProperty, BuildableOntologyProperty {

    /**
     * Adds a class to the domain of the property.
     * Resources denoted by the subject of triples with the property as predicate are instances
     * of ALL classes set as domain.
     * Also adds this property as an outgoing property to clazz.
     * @param clazz The class to add.
     */
    void addDomainClazz(ExtendedRDFSClazz clazz);

    /**
     * Adds a class to the domain of the property.
     * Resources denoted by the subject of triples with the property as predicate are instances
     * of ALL classes set as domain.
     * @param clazz The class to add.
     * @param updateInverse Whether the property should be added as an outgoing property of clazz.
     */
    void addDomainClazz(ExtendedRDFSClazz clazz, boolean updateInverse);

    /**
     * Adds a class to the range of the property.
     * Resources denoted by the object of triples with the property as predicate are instances
     * of ALL classes set as range.
     * Also adds this property as an incoming property to clazz.
     * @param clazz The class to add.
     */
    void addRangeClazz(ExtendedRDFSClazz clazz);

    /**
     * Adds a class to the range of the property.
     * Resources denoted by the object of triples with the property as predicate are instances
     * of ALL classes set as range.
     * Also adds this property as an incoming property to clazz.
     * @param clazz The class to add.
     * @param updateInverse Whether the property should be added as an incoming property of clazz.
     */
    void addRangeClazz(ExtendedRDFSClazz clazz, boolean updateInverse);

    /**
     * Returns the superproperties of this property.
     * Thus this property denotes the subject and each of the returned resources denotes the
     * object in a statement with predicate rdfs:subPropertyOf.
     * @return The superproperties of this property.
     */
    Set<ExtendedRDFSProperty> getSuperproperties();

    /**
     * Adds a property as a superproperty of this one.
     * Thus this property denotes the subject and <code>superProperty</code> denotes the
     * object in a statement with predicate rdfs:subPropertyOf.
     * Also adds this property as a subproperty to <code>superProperty</code> with
     * {@link RDFSProperty#addSubProperty(RDFSProperty)}.
     * @param superProperty The property to add as a superproperty.
     */
    void addSuperproperty(ExtendedRDFSProperty superProperty);

    /**
     * Adds a property as a superproperty of this one.
     * Thus this property denotes the subject and <code>superProperty</code> denotes the
     * object in a statement with predicate rdfs:subPropertyOf.
     * @param superProperty The property to add as a superproperty.
     * @param updateInverse Whether this property should be added as subproperty to
     *                      <code>superProperty</code> with {@link RDFSProperty#addSubProperty(RDFSProperty)}.
     */
    void addSuperproperty(ExtendedRDFSProperty superProperty, boolean updateInverse);

    /**
     * Returns the collection of all {@link ExtendedRDFSProperty} instances that are direct
     * or indirect subproperties of this property, i.e. properties that are connected over one or more
     * rdfs:subPropertyOf edges in the graph and are instances of {@link ExtendedRDFSProperty}.
     * @return The transitive closure of subproperties of this property.
     */
    Set<ExtendedRDFSProperty> getSubpropertyClosure();
}
