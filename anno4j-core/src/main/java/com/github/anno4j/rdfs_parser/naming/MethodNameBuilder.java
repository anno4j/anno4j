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

    /**
     * Extracts a plural form for this resource.
     * A trailing "s" is added and some simple grammatical rules (for english) are applied.
     * @return Same as {@link #lowercaseIdentifier()}, but in a plural form.
     * @throws URISyntaxException If the URI violates RFC 2396 augmented by the rules defined in {@link java.net.URI}.
     * @throws NameBuildingException If the required information for building the name is not contained in the URI.
     */
    public String lowercasePluralIdentifier() throws URISyntaxException, NameBuildingException {
        StringBuilder identifier = new StringBuilder();
        identifier.append(capitalizedIdentifier());

        // Replace trailing "y" with "ie". E.g. "capacity" will be transformed to "capacities":
        if(identifier.charAt(identifier.length() - 1) == 'y') {
            identifier.deleteCharAt(identifier.length() - 1);
            identifier.append("ie");
        }

        // Only append trailing "s" if the name does not yet end with one:
        if(identifier.charAt(identifier.length() - 1) != 's') {
            identifier.append("s");
        }

        return identifier.toString();
    }

    /**
     * Extracts a capitalized plural form for this resource.
     * A trailing "s" is added and some simple grammatical rules (for english) are applied.
     * @return Same as {@link #capitalizedIdentifier()}, but in a plural form.
     * @throws URISyntaxException If the URI violates RFC 2396 augmented by the rules defined in {@link java.net.URI}.
     * @throws NameBuildingException If the required information for building the name is not contained in the URI.
     */
    public String capitalizedPluralIdentifier() throws URISyntaxException, NameBuildingException {
        StringBuilder identifier = new StringBuilder(lowercasePluralIdentifier());
        if(identifier.length() > 0) {
            identifier.setCharAt(0, Character.toUpperCase(identifier.charAt(0)));
        }
        return identifier.toString();
    }

    /**
     * Generates a JavaPoet method spec for an empty setter representing the property resource.
     * The method is named according to Java naming conventions with preceeding "set".
     * @return JavaPoet method object. Can be modified by calling
     * {@link MethodSpec#toBuilder()} on it.
     * @throws URISyntaxException If the URI violates RFC 2396 augmented by the rules defined in URI and the
     * requirement for a hostname component.
     * @throws NameBuildingException If the required information for building the name is not contained in the URI.
     */
    public MethodSpec setterSpec() throws URISyntaxException, NameBuildingException {
        return MethodSpec.methodBuilder("set" + capitalizedIdentifier()).build();
    }

    /**
     * Generates a JavaPoet method spec for an empty add method representing the property resource.
     * The method is named with preceeding "set".
     * @return JavaPoet method object. Can be modified by calling
     * {@link MethodSpec#toBuilder()} on it.
     * @throws URISyntaxException If the URI violates RFC 2396 augmented by the rules defined in URI and the
     * requirement for a hostname component.
     * @throws NameBuildingException If the required information for building the name is not contained in the URI.
     */
    public MethodSpec adderSpec() throws URISyntaxException, NameBuildingException {
        return MethodSpec.methodBuilder("add" + capitalizedIdentifier()).build();
    }

    @Override
    public MethodNameBuilder withRDFSLabel(String label) {
        super.withRDFSLabel(label);
        return this;
    }
}
