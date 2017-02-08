package com.github.anno4j.rdfs_parser.naming;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;

import java.net.URISyntaxException;

/**
 * Generates Java compliant class or interface names for resources.
 * Furthermore JavaPoet interface and class objects can be created.
 */
public class ClassNameBuilder extends IdentifierBuilder {

    /**
     * @param resource The resource to build a name for.
     */
    protected ClassNameBuilder(String resource) {
        super(resource);
    }

    /**
     * Creates a new ClassNameBuilder object.
     * @param resource The resource to build a name for.
     * @return A builder instance.
     */
    public static ClassNameBuilder builder(String resource) {
        return new ClassNameBuilder(resource);
    }

    /**
     * Returns the JavaPoet class name object for the resource.
     * @return The JavaPoet class name.
     * @throws URISyntaxException If the URI violates RFC 2396 augmented by the rules defined in URI and the
     * requirement for a hostname component.
     * @throws NameBuildingException If the required information for building the name is not contained in the URI.
     */
    public ClassName className() throws URISyntaxException, NameBuildingException {
        return ClassName.get(packageName(), capitalizedIdentifier());
    }

    /**
     * Generates a JavaPoet type spec for an empty interface representing the resource.
     * @return JavaPoet object representing an interface for the resource. Can be modified by calling
     * {@link TypeSpec#toBuilder()} on it.
     * @throws URISyntaxException If the URI violates RFC 2396 augmented by the rules defined in URI and the
     * requirement for a hostname component.
     * @throws NameBuildingException If the required information for building the name is not contained in the URI.
     */
    public TypeSpec interfaceSpec() throws URISyntaxException, NameBuildingException {
        return TypeSpec.interfaceBuilder(className()).build();
    }

    @Override
    public ClassNameBuilder withRDFSLabel(String label) {
        super.withRDFSLabel(label);
        return this;
    }
}
