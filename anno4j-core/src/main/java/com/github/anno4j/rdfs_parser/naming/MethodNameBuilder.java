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
    private MethodNameBuilder(String resource) {
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
     * Returns a JavaPoet {@link MethodSpec} object for a property described by the builders resource.
     * Only the name of the method is set in the method specification, i.e. this is a equivalent to
     * <code>MethodSpec.methodBuilder(prefix + ci).build();</code>, where
     * <code>ci</code> is a capitalized identifier for the resource.
     * The generation of the methods name if preferably done on basis of the RDFS label set.
     * If it is not possible to derive a meaningful name, then an unambiguous name is picked.
     * @param prefix The part preceding the properties name in the method name, e.g. "set", "addAll", ...
     * @param pluralName Whether a plural form should be picked for the methods name.
     * @return A {@link MethodSpec} object for a method for the resource.
     */
    public MethodSpec getJavaPoetMethodSpec(String prefix, boolean pluralName) {
        StringBuilder methodName = new StringBuilder(prefix);

        try {
            if(pluralName) {
                methodName.append(capitalizedPluralIdentifier());
            } else {
                methodName.append(capitalizedIdentifier());
            }
        } catch (NameBuildingException | URISyntaxException e) {
            // Find some unambiguous name for the property by using hashCode():
            int hashCode = getResource().hashCode();
            methodName.append("UnnamedProperty");
            if(hashCode < 0) {
                methodName.append(-hashCode)
                          .append("_");
            } else {
                methodName.append(hashCode);
            }
        }

        return MethodSpec.methodBuilder(methodName.toString()).build();
    }

    @Override
    public MethodNameBuilder withRDFSLabel(String label) {
        super.withRDFSLabel(label);
        return this;
    }
}
