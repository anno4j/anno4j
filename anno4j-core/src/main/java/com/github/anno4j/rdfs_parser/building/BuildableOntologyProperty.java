package com.github.anno4j.rdfs_parser.building;

import com.github.anno4j.rdfs_parser.naming.IdentifierBuilder;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;

/**
 * Provides support for building Java methods for RDF properties.
 */
public interface BuildableOntologyProperty extends BuildableOntologyResource {

    /**
     * Returns the JavaPoet class name of the most specific common superclass of all
     * classes defined as the domain of this property.
     * @param config The configuration for building the class name.
     * @return The class name of the properties domains common superclass based on <code>config</code>
     * or null if no such class can be determined.
     */
    ClassName getDomainJavaPoetClassName(OntGenerationConfig config);

    /**
     * Returns the JavaPoet class name of the most specific common superclass of all
     * classes defined as the range of this property.
     * @param config The configuration for building the class name.
     * @return The class name of the properties ranges common superclass based on <code>config</code>
     * or null if no such class can be determined.
     */
    ClassName getRangeJavaPoetClassName(OntGenerationConfig config);

    /**
     * Returns an {@link IdentifierBuilder} instance for generating identifiers for this
     * property based on the natural language preferences set in <code>config</code>.
     * @param config The configuration object on which generation of identifiers will be based on.
     * @return An identifier builder for this property suiting the preferences made in <code>config</code>.
     */
    IdentifierBuilder getIdentifierBuilder(OntGenerationConfig config);

    /**
     * Generates a lowercase identifier for this property based on the natural language preferences
     * set in <code>config</code>.
     * @param config The configuration object on basis of which the identifier will be built.
     * @param plural Whether the identifier should be in plural form.
     * @return A lowercase identifier for this property suiting the preferences made in <code>config</code>.
     */
    String getLowercaseIdentifier(OntGenerationConfig config, boolean plural);


    /**
     * Generates a uppercase identifier for this property based on the natural language preferences
     * set in <code>config</code>.
     * @param config The configuration object on basis of which the identifier will be built.
     * @param plural Whether the identifier should be in plural form.
     * @return A uppercase identifier for this property suiting the preferences made in <code>config</code>.
     */
    String getCapitalizedIdentifier(OntGenerationConfig config, boolean plural);

    /**
     * Generates a JavaPoet specification of an <code>@Iri</code>-annotated field for this property,
     * which can be used in Anno4j support classes.
     * @param config The configuration for building the method specification.
     * @return The JavaPoet field specification for this property or null
     * if no range was provided for this property.
     */
    FieldSpec buildAnnotatedField(OntGenerationConfig config);

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
     * Generates a JavaPoet method specification for a Support class getter
     * for this property.
     * @param config The configuration for building the method specification.
     * @return The JavaPoet method specification for this property or null
     * if no range was provided for this property.
     */
    MethodSpec buildGetterImplementation(OntGenerationConfig config);

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
