package com.github.anno4j.rdfs_parser.building;

import com.squareup.javapoet.MethodSpec;

/**
 * Provides support for building Java methods for RDF properties.
 */
public interface BuildableOntologyProperty extends BuildableOntologyResource {

    /**
     * Generates a JavaPoet method specification for a Anno4j resource object getter
     * for this property.
     * JavaDoc and method name are picked from rdfs:comment/rdfs:label according to the
     * configuration provided.
     * @param config The configuration for building the method specification.
     * @return The JavaPoet method specification for this property or null
     * if no range was provided for this property.
     */
    MethodSpec buildGetter(OntGenerationConfig config);

    /**
     * Generates a JavaPoet method specification for a Anno4j resource object setter
     * for this property.
     * JavaDoc and method name are picked from rdfs:comment/rdfs:label according to the
     * configuration provided.
     * @param config The configuration for building the method specification.
     * @return The JavaPoet method specification for this property or null
     * if no range was provided for this property.
     */
    MethodSpec buildSetter(OntGenerationConfig config);

    /**
     * Generates a JavaPoet method specification for a Support class setter
     * for this property.
     * Checks whether the provided values are in range are added to the methods
     * definition.
     * @param config The configuration for building the method specification.
     * @return The JavaPoet method specification for this property or null
     * if no range was provided for this property.
     */
    MethodSpec buildSetterImplementation(OntGenerationConfig config);

    /**
     * Generates a JavaPoet method specification for a Anno4j resource object add-method
     * for this property.
     * JavaDoc and method name are picked from rdfs:comment/rdfs:label according to the
     * configuration provided.
     * @param config The configuration for building the method specification.
     * @return The JavaPoet method specification for this property or null
     * if no range was provided for this property.
     */
    MethodSpec buildAdder(OntGenerationConfig config);

    /**
     * Generates a JavaPoet method specification for a Support class add-method
     * for this property.
     * JavaDoc and method name are picked from rdfs:comment/rdfs:label according to the
     * configuration provided.
     * @param config The configuration for building the method specification.
     * @return The JavaPoet method specification for this property or null
     * if no range was provided for this property or an error occurs.
     */
    MethodSpec buildAdderImplementation(OntGenerationConfig config);

    /**
     * Generates a JavaPoet method specification for a Anno4j resource object add-method
     * for this property.
     * JavaDoc and method name are picked from rdfs:comment/rdfs:label according to the
     * configuration provided.
     * @param config The configuration for building the method specification.
     * @return The JavaPoet method specification for this property or null
     * if no range was provided for this property.
     */
    MethodSpec buildAdderAll(OntGenerationConfig config);

    /**
     * Generates a JavaPoet method specification for a Support class add-method
     * for this property.
     * JavaDoc and method name are picked from rdfs:comment/rdfs:label according to the
     * configuration provided.
     * @param config The configuration for building the method specification.
     * @return The JavaPoet method specification for this property or null
     * if no range was provided for this property or an error occurs.
     */
    MethodSpec buildAdderAllImplementation(OntGenerationConfig config);

    /**
     * Generates a JavaPoet method specification for a Anno4j resource object remove-method
     * for this property.
     * JavaDoc and method name are picked from rdfs:comment/rdfs:label according to the
     * configuration provided.
     * @param config The configuration for building the method specification.
     * @return The JavaPoet method specification for this property or null
     * if no range was provided for this property.
     */
    MethodSpec buildRemover(OntGenerationConfig config);

    /**
     * Generates a JavaPoet method specification for a Support class remove-method
     * for this property.
     * JavaDoc and method name are picked from rdfs:comment/rdfs:label according to the
     * configuration provided.
     * @param config The configuration for building the method specification.
     * @return The JavaPoet method specification for this property or null
     * if no range was provided for this property or an error occurs.
     */
    MethodSpec buildRemoverImplementation(OntGenerationConfig config);

    /**
     * Generates a JavaPoet method specification for a Anno4j resource object removeAll-method
     * for this property.
     * JavaDoc and method name are picked from rdfs:comment/rdfs:label according to the
     * configuration provided.
     * @param config The configuration for building the method specification.
     * @return The JavaPoet method specification for this property or null
     * if no range was provided for this property.
     */
    MethodSpec buildRemoverAll(OntGenerationConfig config);

    /**
     * Generates a JavaPoet method specification for a Support class removeAll-method
     * for this property.
     * JavaDoc and method name are picked from rdfs:comment/rdfs:label according to the
     * configuration provided.
     * @param config The configuration for building the method specification.
     * @return The JavaPoet method specification for this property or null
     * if no range was provided for this property or an error occurs.
     */
    MethodSpec buildRemoverAllImplementation(OntGenerationConfig config);
}
