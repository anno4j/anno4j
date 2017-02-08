package com.github.anno4j.rdfs_parser.naming;

import com.squareup.javapoet.MethodSpec;

import java.net.URISyntaxException;

/**
 * Generates Java compliant method names for resources.
 */
public class MethodNameBuilder extends IdentifierBuilder {

    /**
     * @param resource The resource to build a name for.
     */
    protected MethodNameBuilder(String resource) {
        super(resource);
    }

    /**
     * Creates a new MethodNameBuilder object for the given resource.
     * @param resource The resource to build a name for.
     * @return A builder instance.
     */
    public static MethodNameBuilder builder(String resource) {
        return new MethodNameBuilder(resource);
    }

    /**
     * Generates a JavaPoet method spec for an empty getter representing the property resource.
     * The method is named according to Java naming conventions with preceeding "get".
     * @return JavaPoet method object. Can be modified by calling
     * {@link MethodSpec#toBuilder()} on it.
     * @throws URISyntaxException If the URI violates RFC 2396 augmented by the rules defined in URI and the
     * requirement for a hostname component.
     * @throws NameBuildingException If the required information for building the name is not contained in the URI.
     */
    public MethodSpec getterSpec() throws URISyntaxException, NameBuildingException {
        return MethodSpec.methodBuilder("get" + capitalizedIdentifier()).build();
    }

    @Override
    public MethodNameBuilder withRDFSLabel(String label) {
        super.withRDFSLabel(label);
        return this;
    }
}
