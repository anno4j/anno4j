package com.github.anno4j.rdfs_parser.model;

import com.github.anno4j.rdfs_parser.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.model.rdfs.RDFSProperty;
import com.squareup.javapoet.MethodSpec;
import org.openrdf.annotations.Iri;
import com.github.anno4j.model.namespaces.RDF;

/**
 * Represents a rdfs:Property.
 * This class has additional members in order to enable Java file generation
 * from RDF data.
 */
@Iri(RDF.PROPERTY)
public interface ExtendedRDFSProperty extends RDFSProperty {

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

    MethodSpec buildGetter(OntGenerationConfig config);

    MethodSpec buildSetter(OntGenerationConfig config);

}
