package com.github.anno4j.schema_parsing.naming;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectRepository;

/**
 * Generates Java compliant class or interface names for resources.
 * Furthermore JavaPoet interface and class objects can be created.
 */
public class ClassNameBuilder extends IdentifierBuilder {

    /**
     * Initializes the builder with a connection to a repository.
     * @param connection The connection to use later.
     */
    private ClassNameBuilder(ObjectConnection connection) {
        super(connection);
    }

    /**
     * Returns a builder object that operates on the given repository.
     * @param repository The repository to use, e.g. for name disambiguation.
     * @return An instance of the builder.
     * @throws RepositoryException Thrown if an error occurs while opening a connection to the repository.
     */
    public static ClassNameBuilder forObjectRepository(ObjectRepository repository) throws RepositoryException {
        return forObjectRepository(repository.getConnection());
    }

    /**
     * Returns a builder object that operates on the connected repository.
     * @param connection A connection to the repository to use, e.g. for name disambiguation.
     * @return An instance of the builder.
     */
    public static ClassNameBuilder forObjectRepository(ObjectConnection connection) {
        return new ClassNameBuilder(connection);
    }

    /**
     * Returns the JavaPoet class name object for the resource.
     * @param config The configuration object specifying the language preference.
     * @param resource The class resource to get a class name for.
     * @return The JavaPoet class name.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    public ClassName className(ResourceObject resource, OntGenerationConfig config) throws RepositoryException {
        return ClassName.get(packageName(resource), capitalizedIdentifier(resource, config));
    }

    /**
     * Generates a JavaPoet type spec for an empty interface representing the resource.
     * @param config The configuration object specifying the language preference.
     * @param resource The class resource to get a class name for.
     * @return JavaPoet object representing an interface for the resource. Can be modified by calling
     * {@link TypeSpec#toBuilder()} on it.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    public TypeSpec interfaceSpec(ResourceObject resource, OntGenerationConfig config) throws RepositoryException {
        return TypeSpec.interfaceBuilder(className(resource, config)).build();
    }
}
