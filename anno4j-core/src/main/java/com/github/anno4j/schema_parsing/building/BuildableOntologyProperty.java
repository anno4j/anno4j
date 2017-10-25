package com.github.anno4j.schema_parsing.building;

import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import org.openrdf.repository.RepositoryException;

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
    ClassName getDomainJavaPoetClassName(OntGenerationConfig config) throws RepositoryException;

    /**
     * Returns the JavaPoet class name of the most specific common superclass of all
     * classes defined as the range of this property.
     * @param config The configuration for building the class name.
     * @return The class name of the properties ranges common superclass based on <code>config</code>
     * or null if no such class can be determined.
     */
    ClassName getRangeJavaPoetClassName(OntGenerationConfig config) throws RepositoryException;


    /**
     * Generates a lowercase identifier for this property based on the natural language preferences
     * set in <code>config</code>.
     * @param config The configuration object on basis of which the identifier will be built.
     * @param plural Whether the identifier should be in plural form.
     * @return A lowercase identifier for this property suiting the preferences made in <code>config</code>.
     */
    String getLowercaseIdentifier(OntGenerationConfig config, boolean plural) throws RepositoryException;


    /**
     * Generates a uppercase identifier for this property based on the natural language preferences
     * set in <code>config</code>.
     * @param config The configuration object on basis of which the identifier will be built.
     * @param plural Whether the identifier should be in plural form.
     * @return A uppercase identifier for this property suiting the preferences made in <code>config</code>.
     */
    String getCapitalizedIdentifier(OntGenerationConfig config, boolean plural) throws RepositoryException;

    /**
     * Generates a JavaPoet method specification for a Anno4j resource object getter
     * for this property.
     * JavaDoc and method name are picked from rdfs:comment/rdfs:label according to the
     * configuration provided.
     * @param domainClazz The class in which context the method should be generated.
     * @param config The configuration for building the method specification.
     * @return The JavaPoet method specification for this property or null
     * if no range was provided for this property.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    MethodSpec buildGetter(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException;

    /**
     * Generates a JavaPoet method specification for a Anno4j resource object setter
     * for this property.
     * JavaDoc and method name are picked from rdfs:comment/rdfs:label according to the
     * configuration provided.
     * @param domainClazz The class in which context the method should be generated.
     * @param config The configuration for building the method specification.
     * @return The JavaPoet method specification for this property or null
     * if no range was provided for this property.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    MethodSpec buildSetter(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException;

    /**
     * Generates a JavaPoet method specification for a Anno4j resource object variable argument setter
     * for this property.
     * JavaDoc and method name are picked from rdfs:comment/rdfs:label according to the
     * configuration provided.
     * @param domainClazz The class in which context the method should be generated.
     * @param config The configuration for building the method specification.
     * @return The JavaPoet method specification for this property or null
     * if no range was provided for this property.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    MethodSpec buildVarArgSetter(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException;

    /**
     * Generates a JavaPoet method specification for a Support class variable argument setter
     * for this property.
     * Checks whether the provided values are in range are added to the methods
     * definition.
     * @param domainClazz The class in which context the method should be generated.
     * @param config The configuration for building the method specification.
     * @return The JavaPoet method specification for this property or null
     * if no range was provided for this property.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    MethodSpec buildVarArgSetterImplementation(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException;

    /**
     * Generates a JavaPoet method specification for a Anno4j resource object add-method
     * for this property.
     * JavaDoc and method name are picked from rdfs:comment/rdfs:label according to the
     * configuration provided.
     * @param domainClazz The class in which context the method should be generated.
     * @param config The configuration for building the method specification.
     * @return The JavaPoet method specification for this property or null
     * if no range was provided for this property.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    MethodSpec buildAdder(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException;

    /**
     * Generates a JavaPoet method specification for a Support class add-method
     * for this property.
     * JavaDoc and method name are picked from rdfs:comment/rdfs:label according to the
     * configuration provided.
     * @param domainClazz The class in which context the method should be generated.
     * @param config The configuration for building the method specification.
     * @return The JavaPoet method specification for this property or null
     * if no range was provided for this property or an error occurs.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    MethodSpec buildAdderImplementation(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException;

    /**
     * Generates a JavaPoet method specification for a Anno4j resource object add-method
     * for this property.
     * JavaDoc and method name are picked from rdfs:comment/rdfs:label according to the
     * configuration provided.
     * @param domainClazz The class in which context the method should be generated.
     * @param config The configuration for building the method specification.
     * @return The JavaPoet method specification for this property or null
     * if no range was provided for this property.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    MethodSpec buildAdderAll(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException;

    /**
     * Generates a JavaPoet method specification for a Support class add-method
     * for this property.
     * JavaDoc and method name are picked from rdfs:comment/rdfs:label according to the
     * configuration provided.
     * @param domainClazz The class in which context the method should be generated.
     * @param config The configuration for building the method specification.
     * @return The JavaPoet method specification for this property or null
     * if no range was provided for this property or an error occurs.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    MethodSpec buildAdderAllImplementation(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException;

    /**
     * Generates a JavaPoet method specification for a Anno4j resource object remove-method
     * for this property.
     * JavaDoc and method name are picked from rdfs:comment/rdfs:label according to the
     * configuration provided.
     * @param config The configuration for building the method specification.
     * @return The JavaPoet method specification for this property or null
     * if no range was provided for this property.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    MethodSpec buildRemover(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException;

    /**
     * Generates a JavaPoet method specification for a Support class remove-method
     * for this property.
     * JavaDoc and method name are picked from rdfs:comment/rdfs:label according to the
     * configuration provided.
     * @param domainClazz The class in which context the method should be generated.
     * @param config The configuration for building the method specification.
     * @return The JavaPoet method specification for this property or null
     * if no range was provided for this property or an error occurs.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    MethodSpec buildRemoverImplementation(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException;

    /**
     * Generates a JavaPoet method specification for a Anno4j resource object removeAll-method
     * for this property.
     * JavaDoc and method name are picked from rdfs:comment/rdfs:label according to the
     * configuration provided.
     * @param domainClazz The class in which context the method should be generated.
     * @param config The configuration for building the method specification.
     * @return The JavaPoet method specification for this property or null
     * if no range was provided for this property.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    MethodSpec buildRemoverAll(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException;

    /**
     * Generates a JavaPoet method specification for a Support class removeAll-method
     * for this property.
     * JavaDoc and method name are picked from rdfs:comment/rdfs:label according to the
     * configuration provided.
     * @param domainClazz The class in which context the method should be generated.
     * @param config The configuration for building the method specification.
     * @return The JavaPoet method specification for this property or null
     * if no range was provided for this property or an error occurs.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    MethodSpec buildRemoverAllImplementation(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException;
}
