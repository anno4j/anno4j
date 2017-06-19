package com.github.anno4j.schema_parsing.naming;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.squareup.javapoet.MethodSpec;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectRepository;

/**
 * Generates Java compliant method names for resources.
 */
public class MethodNameBuilder extends IdentifierBuilder {

    /**
     * Initializes the builder with a connection to a repository.
     * @param connection The connection to use later.
     */
    private MethodNameBuilder(ObjectConnection connection) {
        super(connection);
    }

    /**
     * Returns a builder object that operates on the given repository.
     * @param repository The repository to use, e.g. for name disambiguation.
     * @return An instance of the builder.
     * @throws RepositoryException Thrown if an error occurs while opening a connection to the repository.
     */
    public static MethodNameBuilder forObjectRepository(ObjectRepository repository) throws RepositoryException {
        return forObjectRepository(repository.getConnection());
    }

    /**
     * Returns a builder object that operates on the connected repository.
     * @param connection A connection to the repository to use, e.g. for name disambiguation.
     * @return An instance of the builder.
     */
    public static MethodNameBuilder forObjectRepository(ObjectConnection connection) {
        return new MethodNameBuilder(connection);
    }

    /**
     * Returns a JavaPoet {@link MethodSpec} object for a property described by the builders resource.
     * Only the name of the method is set in the method specification, i.e. this is a equivalent to
     * <code>MethodSpec.methodBuilder(prefix + ci).build();</code>, where
     * <code>ci</code> is a capitalized identifier for the resource.
     * The generation of the methods name if preferably done on basis of the RDFS label set.
     * If it is not possible to derive a meaningful name, then an unambiguous name is picked.
     * @param prefix The part preceding the properties name in the method name, e.g. "set", "addAll", ...
     * @param resource The property resource for which to generate a (name-only) method specification.
     * @param config The configuration according to which to chose the name.
     * @param pluralName Whether a plural form should be picked for the methods name.
     * @return A {@link MethodSpec} object for a method for the resource.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    public MethodSpec getJavaPoetMethodSpec(String prefix, ResourceObject resource, OntGenerationConfig config, boolean pluralName) throws RepositoryException {
        StringBuilder methodName = new StringBuilder(prefix);

        if(pluralName) {
            methodName.append(capitalizedPluralIdentifier(resource, config));
        } else {
            methodName.append(capitalizedIdentifier(resource, config));
        }

        return MethodSpec.methodBuilder(methodName.toString()).build();
    }
}
