package com.github.anno4j.rdfs_parser.naming;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Utility class for generating Java compliant names for resources.
 * Provides functionality for deriving a package name from the hostname portion of URIs.
 * The readability can be enhanced by specifying the rdfs:label of the resource.
 */
public class IdentifierBuilder {

    /**
     * Signalizes that an error occured while extracting a Java type name from a URI.
     */
    public class NameBuildingException extends Exception {
        public NameBuildingException(String message) {
            super(message);
        }
    }

    /**
     * Thrown if a name should be extracted from URI while this is a blank node.
     */
    public class BlankNodeException extends NameBuildingException {
        public BlankNodeException(String message) {
            super(message);
        }
    }

    /**
     * List of reserved keywords in the Java programming language.
     * Those can not be used as identifiers.
     */
    private static final String JAVA_RESERVED_KEYWORDS[] = { "abstract", "assert", "boolean",
            "break", "byte", "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else", "extends", "false",
            "final", "finally", "float", "for", "goto", "if", "implements",
            "import", "instanceof", "int", "interface", "long", "native",
            "new", "null", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super", "switch",
            "synchronized", "this", "throw", "throws", "transient", "true",
            "try", "void", "volatile", "while" };

    /**
     * The resource to build a name for.
     */
    private String resource;

    /**
     * The rdfs:label assigned to the resource.
     * Used for constructing more meaningful names.
     */
    private String rdfsLabel;

    /**
     * @param resource The resource to build a name for.
     */
    protected IdentifierBuilder(String resource) {
        this.resource = resource;
    }

    /**
     * Creates a new builder object.
     * @param resource The resource to build a name for.
     * @return A builder instance.
     */
    public static IdentifierBuilder builder(String resource) {
        return new IdentifierBuilder(resource);
    }

    /**
     * Supplies a rdfs:label for the resource to the builder,
     * which is used for constructing more meaningful names.
     * @param label A rdfs:label literal assigned to the resource.
     * @return Reference to the builder in order to enable method chaining.
     */
    public IdentifierBuilder withRDFSLabel(String label) {
        rdfsLabel = label;
        return this;
    }

    /**
     * @return The resource ID or URI that this builder constructs a name for.
     */
    public String getResource() {
        return resource;
    }

    /**
     * @return The rdfs:label assigned to the resource.
     * Used for constructing more meaningful names.
     */
    public String getRdfsLabel() {
        return rdfsLabel;
    }

    /**
     * @return Returns true iff the resource is a blank node.
     */
    private boolean isBlankNode() {
        // IDs of blank nodes do not conform to URI specification:
        try {
            new URI(resource);
        } catch (URISyntaxException e) {
            return true;
        }
        return false;
    }

    /**
     * Generates a Java compliant name using the rules and conventions defined at
     * <a href="https://docs.oracle.com/javase/tutorial/java/package/namingpkgs.html">Naming a Package</a>.
     * @param name The name that should be escaped.
     * @return The escaped name. Returns the name '_' if the given name is empty.
     */
    private String toJavaName(String name) {
        StringBuilder builder = new StringBuilder(name);
        if(!name.isEmpty()) {
            // Remove illegal characters:
            for(int i = 0; i < builder.length(); i++) {
                char c = builder.charAt(i);
                if(!Character.isJavaIdentifierPart(c)) {
                    // Hyphens and whitespaces are not allowed in package names. Replace them by underscore:
                    if(c == '-' || c == ' ') {
                        builder.setCharAt(i, '_');

                    } else { // All other characters are removed:
                        builder.deleteCharAt(i);
                        i--;
                    }
                }
            }

            // Append underscore to java reserved names to avoid collision:
            if(Arrays.binarySearch(JAVA_RESERVED_KEYWORDS, builder.toString()) >= 0) {
                builder.append("_");
            }

            // Names may not start with e.g. a number and must not be empty:
            if(builder.length() == 0 || !Character.isJavaIdentifierStart(builder.charAt(0))) {
                builder.insert(0, '_');
            }
            return builder.toString();

        } else {
            // If a empty name is provided:
            return "_";
        }
    }

    /**
     * Returns the filename or fragment of a given URI.
     * @return The fragment or filename.
     * @throws URISyntaxException If the URI violates RFC 2396 augmented by the rules defined in {@link java.net.URI}.
     * @throws NameBuildingException If no fragment or filename can be found in the URI.
     * @throws BlankNodeException If the URI represents a blank node.
     */
    private String getFileOrFragmentName() throws URISyntaxException, NameBuildingException {
        // We cannot get a filename or fragment for a blank node:
        if(isBlankNode()) {
            throw new BlankNodeException("Cannot get a filename or fragment for a blank node");
        }

        URI u = new URI(resource);
        if(u.getFragment() != null) {
            return u.getFragment();
        }
        if(u.getPath() != null) {
            String[] splits = u.getPath().split("/");
            if(!splits[splits.length - 1].isEmpty()) {
                return splits[splits.length - 1];
            }
        }
        throw new NameBuildingException("No fragment or filename can be found in " + resource + ".");
    }

    /**
     * Determines a package name for the type generated for a URI by its hostname component.
     * If a portion of the hostname contains a hyphen or if it is a Java reserved name or
     * starts with a digit then this portion of the package name is added an underscore
     * (like suggested in
     * <a href="https://docs.oracle.com/javase/tutorial/java/package/namingpkgs.html">Naming a Package</a>).
     * If a portion of the hostname contains a special character that is not a hyphen then this character is left out
     * in the package name.
     *
     * @return Returns the package name for the given URI. If the hostname is a IP address or the resource is a blank node
     * then the default package "" is returned.
     * @throws URISyntaxException If the URI violates RFC 2396 augmented by the rules defined in {@link java.net.URI}
     * and the requirement for a hostname component.
     */
    public String packageName() throws URISyntaxException {
        if(isBlankNode()) {
            return "";
        } else {
            URI u = new URI(resource);
            if(u.getHost() != null) {

                String[] splits = u.getHost().toLowerCase().split("\\.");

                // Iterate the components in reverse order to generate a package style name:
                List<String> packageNamePortions = new LinkedList<>();
                for (int i = splits.length - 1; i >= 0; i--) {
                    // Package names can't be empty, only numbers (IPv4 addresses) or contain colons (IPv6):
                    if(!splits[i].isEmpty() && !splits[i].matches("[0-9]+") && !splits[i].contains(":")) {

                        String portion = toJavaName(splits[i]); // Make Java compliant

                        packageNamePortions.add(portion);
                    }
                }

                if(packageNamePortions.size() > 0) {
                    StringBuilder packageName = new StringBuilder();

                    // Join the portions of the package name found to a package name separated by '.':
                    ListIterator<String> portionIter = packageNamePortions.listIterator();
                    while(portionIter.hasNext()) {
                        packageName.append(portionIter.next());
                        if(portionIter.hasNext()) {
                            packageName.append(".");
                        }
                    }
                    return packageName.toString();
                } else {
                    return "";
                }
            } else {
                throw new URISyntaxException(resource, "The URI does not contain a host component.");
            }
        }
    }

    /**
     * Determines a Java compliant identifier for the resource with first character in lowercase (e.g. variable names).
     * Removes all characters that are not allowed in identifier names and transforms separations by underscore,
     * hyphen or whitespace to CamelCase.
     * The name is preferably generated from the rdfs:label if it was set.
     * @return The identifier name that was determined.
     * @throws URISyntaxException If the URI violates RFC 2396 augmented by the rules defined in {@link java.net.URI}.
     * @throws NameBuildingException If the required information for building the name is not contained in the URI.
     */
    public String lowercaseIdentifier() throws URISyntaxException, NameBuildingException {
        // Prefer the rdfs:label as a basis for the name. If not supplied, try to extract from URI:
        StringBuilder rawName;
        if(rdfsLabel != null) {
            rawName = new StringBuilder(rdfsLabel);
        } else {
            try {
                rawName = new StringBuilder(getFileOrFragmentName());

            } catch (BlankNodeException e) {
                rawName = new StringBuilder(resource);
            }
        }

        // Replace separation with underscore and/or whitespace to camel case:
        int pivotUnderscore = -1, pivotWhitespace = -1;
        do {
            pivotUnderscore = rawName.indexOf("_", pivotUnderscore);
            pivotWhitespace = rawName.indexOf(" ", pivotWhitespace);

            if(pivotUnderscore != -1 && pivotUnderscore + 1 < rawName.length() && Character.isLowerCase(rawName.charAt(pivotUnderscore + 1))) {
                rawName.setCharAt(pivotUnderscore + 1, Character.toUpperCase(rawName.charAt(pivotUnderscore + 1)));
                rawName.deleteCharAt(pivotUnderscore);
                // If the pivot for whitespace was ahead set it one back, because one character was deleted:
                if(pivotUnderscore < pivotWhitespace) {
                    pivotWhitespace--;
                }
            }

            if(pivotWhitespace != -1 && pivotWhitespace + 1 < rawName.length() && Character.isLowerCase(rawName.charAt(pivotWhitespace + 1))) {
                rawName.setCharAt(pivotWhitespace + 1, Character.toUpperCase(rawName.charAt(pivotWhitespace + 1)));
                rawName.deleteCharAt(pivotWhitespace);
                // If the pivot for underscore was ahead set it one back, because one character was deleted:
                if(pivotUnderscore > pivotWhitespace) {
                    pivotUnderscore--;
                }
            }
        } while (pivotUnderscore != -1 || pivotWhitespace != -1);

        // Make sure the first character is lowercase:
        if(rawName.length() > 0) {
            rawName.setCharAt(0, Character.toLowerCase(rawName.charAt(0)));
        }

        return toJavaName(rawName.toString());
    }

    /**
     * Determines a Java compliant identifier for the resource with first character in uppercase (e.g. class names).
     * Removes all characters that are not allowed in identifier names and transforms separations by underscore,
     * hyphen or whitespace to CamelCase.
     * The name is preferably generated from the rdfs:label if it was set.
     * @return The identifier name that was determined.
     * @throws URISyntaxException If the URI violates RFC 2396 augmented by the rules defined in {@link java.net.URI}.
     * @throws NameBuildingException If the required information for building the name is not contained in the URI.
     */
    public String capitalizedIdentifier() throws URISyntaxException, NameBuildingException {

        // Prune non allowed characters from the raw name:
        StringBuilder identifier = new StringBuilder(lowercaseIdentifier());

        // Capitalize first character as defined by the coding conventions:
        identifier.setCharAt(0, Character.toUpperCase(identifier.charAt(0)));

        return identifier.toString();
    }


}
