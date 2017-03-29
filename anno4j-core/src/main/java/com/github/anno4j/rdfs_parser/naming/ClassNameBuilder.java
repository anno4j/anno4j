package com.github.anno4j.rdfs_parser.naming;

import com.github.anno4j.rdfs_parser.model.RDFSProperty;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;

/**
 * Generates Java compliant class or interface names for resources.
 * Furthermore JavaPoet interface and class objects can be created.
 */
public class ClassNameBuilder extends IdentifierBuilder {

    /**
     * Properties that have the class to build a name for as part of
     * their range.
     * Used for generating names for blank nodes.
     */
    private Collection<String> incomingProperties = new HashSet<>();

    /**
     * Properties that have the class to build a name for as part of
     * their domain.
     * Used for generating names for blank nodes.
     */
    private Collection<String> outgoingProperties = new HashSet<>();

    /**
     * @param resource The resource to build a name for.
     */
    private ClassNameBuilder(String resource) {
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
        if(isBlankNode() && getRdfsLabel() == null) {
            if(!outgoingProperties.isEmpty()) {
                StringBuilder name = new StringBuilder();
                for (String property : outgoingProperties) {
                    String part = IdentifierBuilder.builder(property)
                            .capitalizedIdentifier();
                    name.append(part);
                }

                name.append("Node");

                return ClassName.get(packageName(), name.toString());

            } else if (!incomingProperties.isEmpty()) {
                StringBuilder name = new StringBuilder();
                for (String property : incomingProperties) {
                    String part = IdentifierBuilder.builder(property)
                            .capitalizedIdentifier();
                    name.append(part);
                }

                name.append("Target");

                return ClassName.get(packageName(), name.toString());

            } else {
                return ClassName.get(packageName(), capitalizedIdentifier());
            }

        } else {
            return ClassName.get(packageName(), capitalizedIdentifier());
        }
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

    /**
     * Adds that the class is range of a property <code>prop</code>
     * as additional information for name building.
     * @param prop The property this class is part of the range.
     * @return Reference to the builder in order to enable method chaining.
     */
    public ClassNameBuilder withIncomingProperty(String prop) {
        incomingProperties.add(prop);
        return this;
    }

    /**
     * Adds that the class is range of a property <code>prop</code>
     * as additional information for name building.
     * @param prop The property this class is part of the range.
     * @return Reference to the builder in order to enable method chaining.
     */
    public ClassNameBuilder withIncomingProperty(RDFSProperty prop) {
        return withIncomingProperty(prop.getResourceAsString());
    }

    /**
     * Adds that the class is domain of a property <code>prop</code>
     * as additional information for name building.
     * @param prop The property this class is part of the domain.
     * @return Reference to the builder in order to enable method chaining.
     */
    public ClassNameBuilder withOutgoingProperty(String prop) {
        outgoingProperties.add(prop);
        return this;
    }

    /**
     * Adds that the class is domain of a property <code>prop</code>
     * as additional information for name building.
     * @param prop The property this class is part of the domain.
     * @return Reference to the builder in order to enable method chaining.
     */
    public ClassNameBuilder withOutgoingProperty(RDFSProperty prop) {
        return withOutgoingProperty(prop.getResourceAsString());
    }
}
