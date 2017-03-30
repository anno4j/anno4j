package com.github.anno4j.rdfs_parser.building;

import com.github.anno4j.rdfs_parser.model.RDFSClazz;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;
import org.openrdf.annotations.Iri;

import java.util.List;
import java.util.Set;

/**
 * Provides support for transforming a ontology class to a Anno4j
 * resource object and a corresponding support object.
 */
public interface BuildableOntologyClazz extends BuildableOntologyResource {

    /**
     * Resolves a package name for a Java class representing this RDFS class.
     * The package name will be extraced from the hostname portion of the resources URI.
     * @return The package name for this class or the default package (empty string) if
     * no package name can be extracted from the resources URI (e.g. because it is a blank node).
     */
    String getJavaPackageName();

    /**
     * Returns a JavaPoet {@link ClassName} object of a Java class for this RDFS class.
     * Tries to resolve a package name and a identifier name from the classes URI.
     * The generation of the classes name is enhanced by specifying a rdfs:label literal
     * for the resource with {@link RDFSClazz#setLabels(Set)}.
     * If no package name can be extracted for this resource (e.g. because it is a blank node)
     * then the default package is set.
     * @param config The configuration for building the class name
     *               (see {@link OntGenerationConfig#setIdentifierLanguagePreference(List)}).
     * @return The class name of a Java class for this RDFS class.
     * @throws com.github.anno4j.rdfs_parser.mapping.IllegalMappingException Thrown if a
     * {@link com.github.anno4j.rdfs_parser.mapping.DatatypeMapper} returns a Java type
     * that is not supported by Anno4j.
     */
    ClassName getJavaPoetClassName(OntGenerationConfig config);

    /**
     * Generates a JavaPoet {@link TypeSpec} of a resource object interface for this RDFS class.
     * The interface receives appropriate {@link Iri} annotations on both the interface itself
     * and all methods representing properties which this class is domain of.
     * @param config Configuration for the generation. E.g. specifies which language to use for JavaDoc.
     * @return The type specification for a resource object interface for this RDFS class.
     */
    TypeSpec buildTypeSpec(OntGenerationConfig config);

    /**
     * Generates a JavaPoet {@link TypeSpec} of a support class for this RDFS class.
     * The set* and add* methods implement checks for range validity if such a constraint is
     * imposed by e.g. a XSD datatype.
     * @param config Configuration for the generation. E.g. specifies which language to use for JavaDoc.
     * @return The type specification for a support class for this RDFS class.
     */
    TypeSpec buildSupportTypeSpec(OntGenerationConfig config);
}
