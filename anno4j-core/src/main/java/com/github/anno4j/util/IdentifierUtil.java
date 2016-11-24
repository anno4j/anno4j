package com.github.anno4j.util;

/**
 * Different static methods that help for identifiers, Java class naming conventions etc.
 */
public class IdentifierUtil {

    /**
     * Under the assumption, that no RDF identifier may contain "/" or "#", these characters indicate the last
     * position of the namespace. This method cuts the namespace and returns the RDF identifier only.
     *
     * @param input The namespace + identifier that is to be trimmed.
     * @return The RDF identifier without associated namespace.
     */
    public static String trimNamespace(String input) {
        int lastSlash = input.lastIndexOf('/');
        int lastHashtag = input.lastIndexOf('#');

        int lastNamespaceChar = Math.max(lastSlash, lastHashtag);

        if (lastNamespaceChar == -1) {
            return input;
        } else {
            return input.substring(lastNamespaceChar + 1);
        }
    }

    /**
     * Identifiers between RDF Resources and Java classes have different restrictions.
     * This method takes a RDF Resource identifier and changes it so that it is also applicable as Java class name.
     *
     * @param input The RDF Resource identifier to change.
     * @return      A String representing the RDF Resource identifier, adapted so it can be also a Java class name.
     */
    public static String adaptToJavaIdentifier(String input) {
        String result = input.replace('-', '_');

        return result;
    }
}
