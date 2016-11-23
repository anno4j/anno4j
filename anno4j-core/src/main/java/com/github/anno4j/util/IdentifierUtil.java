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
}
