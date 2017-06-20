package com.github.anno4j.schema_parsing.naming;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.schema.model.rdfs.RDFSSchemaResource;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Pattern;

/**
 * Utility class for generating Java compliant names for resources.
 * Provides functionality for deriving a package name from the hostname portion of URIs.
 * The readability can be enhanced by specifying the rdfs:label of the resource.
 */
public class IdentifierBuilder {

    /**
     * Signalizes that an error occurred while extracting a Java type name from a URI.
     */
    public class NameBuildingException extends Exception {
        /**
         * {@inheritDoc}
         */
        public NameBuildingException(String message) {
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
     * Regex for validating well-formed URIs as defined in
     * <a href="https://www.ietf.org/rfc/rfc3986.txt">RFC 3986</a> Appendix B.
     */
    private static final Pattern URI_VALIDATION_REGEX = Pattern.compile("^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?");

    /**
     * Connection to use for building names, e.g. to disambiguate the identifiers.
     */
    private final ObjectConnection connection;

    /**
     * Initializes the builder with a connection to a repository.
     * @param connection The connection to use later.
     */
    IdentifierBuilder(ObjectConnection connection) {
        this.connection = connection;
    }

    /**
     * Returns a builder object that operates on the given repository.
     * @param repository The repository to use, e.g. for name disambiguation.
     * @return An instance of the builder.
     * @throws RepositoryException Thrown if an error occurs while opening a connection to the repository.
     */
    public static IdentifierBuilder forObjectRepository(ObjectRepository repository) throws RepositoryException {
        return forObjectRepository(repository.getConnection());
    }

    /**
     * Returns a builder object that operates on the connected repository.
     * @param connection A connection to the repository to use, e.g. for name disambiguation.
     * @return An instance of the builder.
     */
    public static IdentifierBuilder forObjectRepository(ObjectConnection connection) {
        return new IdentifierBuilder(connection);
    }

    /**
     * Generates a Java compliant name using the rules and conventions defined at
     * <a href="https://docs.oracle.com/javase/tutorial/java/package/namingpkgs.html">Naming a Package</a>.
     * @param name The name that should be escaped.
     * @return The escaped name. Returns the name '_' if the given name is empty.
     * @throws IllegalArgumentException Thrown if <code>name</code> is empty or null.
     */
    private String toJavaName(String name) throws IllegalArgumentException {
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
            throw new IllegalArgumentException("The provided name must not be null or empty.");
        }
    }

    /**
     * Returns the filename or fragment of a given URI.
     * @param resource The resource for which to extract the fragment or filename.
     * @return The fragment or filename or an deterministically chosen, unique name for the resource.
     */
    private String getFileOrFragmentName(ResourceObject resource) {

        URI u;
        try {
            u = new URI(resource.getResourceAsString());
        } catch (URISyntaxException e) {
            return "Unnamed" + resource.getResourceAsString().hashCode();
        }
        // Handle fragments, i.e. trailing components preceeded by "#":
        if(u.getFragment() != null) {
            return u.getFragment();
        }
        // Handle URLs with paths. Pick the name of the last folder/file:
        if(u.getPath() != null) {
            String[] splits = u.getPath().split("/");
            if(!splits[splits.length - 1].isEmpty()) {
                return splits[splits.length - 1];
            }
        }
        // Handle URNs:
        if(u.toString().startsWith("urn:")) {
            String[] splits = u.toString().split(":");
            if(!splits[splits.length - 1].isEmpty()) {
                return splits[splits.length - 1];
            }
        }
        return "Unnamed" + resource.getResourceAsString().hashCode();
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
     * @param object The resource for which to get the package name for.
     * @return Returns the package name for the given resource. If the hostname is a IP address or the resource is a blank node
     * then the default package "" is returned.
     */
    public String packageName(ResourceObject object) {
        URI u;
        try {
            u = new URI(object.getResourceAsString());
        } catch (URISyntaxException e) {
            return "";
        }

        if(u.getHost() != null) {

            String[] splits = u.getHost().toLowerCase().split("\\.");

            // Iterate the components in reverse order to generate a package style name:
            List<String> packageNamePortions = new LinkedList<>();
            for (int i = splits.length - 1; i >= 0; i--) {
                // Package names can't be empty, only numbers (IPv4 addresses) or contain colons (IPv6):
                if(!splits[i].isEmpty() && !splits[i].matches("[0-9]+") && !splits[i].contains(":")) {

                    String portion;
                    try {
                        portion = toJavaName(splits[i]); // Make Java compliant
                        packageNamePortions.add(portion);

                    } catch (IllegalArgumentException ignored) { }
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
            return "";
        }
    }

    /**
     * Returns the RDFS label preferred by the given configuration.
     * @param resource The resource for which to get a label.
     * @param config The configuration object specifying the language preference.
     * @return Returns the RDFS label preferred or null if no label could be found.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    private String getPreferredRDFSLabel(ResourceObject resource, OntGenerationConfig config) throws RepositoryException {
        try {
            RDFSSchemaResource schemaResource = connection.findObject(RDFSSchemaResource.class, resource.getResource());
            if(schemaResource != null) {
                CharSequence bestLabel = null;
                for (CharSequence label : schemaResource.getLabels()) {
                    if (config.isPreferredForIdentifiers(label, bestLabel)) {
                        bestLabel = label;
                    }
                }
                if (bestLabel != null) {
                    return bestLabel.toString();
                } else {
                    return null;
                }

            } else { // Something that is not a RDFS resource has no RDFS label:
                return null;
            }

        } catch (QueryEvaluationException | RepositoryException e) {
            throw new RepositoryException();
        }
    }

    /**
     * Checks whether there is any other resource than the given one which has an {@code rdfs:label}
     * which would possibly result in the same identifier name by {@link #toJavaName(String)}.
     * @param resource The resource that has the label.
     * @param label The label to check for.
     * @return Returns true iff there is no possibly conflicting label within the repository.
     * @throws RepositoryException Thrown if an error occurrs while querying the repository.
     */
    private boolean isRDFSLabelUnique(ResourceObject resource, String label) throws RepositoryException {
        // A conflicting label may be lead by some non-alphanumeric characters:
        StringBuilder regex = new StringBuilder("([^a-zA-Z0-9])*");
        for(int i = 0; i < label.length(); i++) {
            char c = label.charAt(i);
            if(Character.isJavaIdentifierPart(c)) {
                regex.append(c);
            }
            // Between each char (and at the end) there may be non-alphanumeric chars (would be pruned out by name generation):
            regex.append("([^0-9a-zA-Z])*");
        }

        try {
            return !connection.prepareBooleanQuery(QueryLanguage.SPARQL,
                    "ASK {" +
                            "   ?s rdfs:label ?l . " +
                            "   FILTER( ?s != <" + resource.getResourceAsString() + "> && REGEX(LCASE(str(?l)), \"" + regex + "\") )" +
                            "}"
            ).evaluate();
        } catch (MalformedQueryException | QueryEvaluationException | RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * Returns true if there is no suffix found in any other resource than the given one
     * that would possibly result into the same identifier name when processed by {@link #toJavaName(String)}.
     * @param resource The resource which suffix should be checked.
     * @return Returns true iff the no other possibly conflicting IRI is found in the repository.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    private boolean isFileOrFragmentNameUnique(ResourceObject resource) throws RepositoryException {
        String fragment = getFileOrFragmentName(resource);
        // A conflicting label may be lead by a separator and some non-alphanumeric characters:
        StringBuilder regex = new StringBuilder("(.*)(:|/|#)([^a-zA-Z0-9])*");
        for(int i = 0; i < fragment.length(); i++) {
            char c = fragment.charAt(i);
            if(Character.isJavaIdentifierPart(c)) {
                regex.append(c);
            }
            // Between each char (and at the end) there may be non-alphanumeric chars (would be pruned out by name generation):
            regex.append("([^a-zA-Z0-9])*");
        }
        regex.append("$");

        try {
            return !connection.prepareBooleanQuery(QueryLanguage.SPARQL,
                    "ASK {" +
                            "   ?s ?p ?o . " +
                            "   FILTER( ?s != <" + resource.getResourceAsString() + "> && REGEX(LCASE(str(?s)), \"" + regex + "\") )" +
                            "}"
            ).evaluate();
        } catch (MalformedQueryException | QueryEvaluationException | RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * Determines a Java compliant identifier for the resource with first character in lowercase (e.g. variable names).
     * Removes all characters that are not allowed in identifier names and transforms separations by underscore,
     * hyphen or whitespace to CamelCase.
     * The name is preferably generated from the rdfs:label if it was set.
     * @param resource The resource for which to find a name.
     * @param config The configuration object specifying how names are built.
     * @return The identifier name that was determined.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    public String lowercaseIdentifier(ResourceObject resource, OntGenerationConfig config) throws RepositoryException {
        // Prefer the rdfs:label as a basis for the name. If not supplied, try to extract from URI:
        StringBuilder rawName;
        String rdfsLabel = getPreferredRDFSLabel(resource, config);
        if(rdfsLabel != null && (!config.isRDFSLabelAmbiguityChecked() || isRDFSLabelUnique(resource, rdfsLabel))) {
            rawName = new StringBuilder(rdfsLabel);
        } else if(!config.isFileOrFragmentAmbiguityChecked() || isFileOrFragmentNameUnique(resource)){
            rawName = new StringBuilder(getFileOrFragmentName(resource));
        } else {
            rawName = new StringBuilder(getFileOrFragmentName(resource)).append(resource.getResourceAsString().hashCode());
        }

        // Replace separation with underscore and/or whitespace to camel case:
        int pivotUnderscore = -1, pivotWhitespace = -1;
        do {
            pivotUnderscore = rawName.indexOf("_", pivotUnderscore);
            pivotWhitespace = rawName.indexOf(" ", pivotWhitespace);

            if(pivotUnderscore != -1) {
                if(pivotUnderscore + 1 < rawName.length() && Character.isLowerCase(rawName.charAt(pivotUnderscore + 1))) {
                    rawName.setCharAt(pivotUnderscore + 1, Character.toUpperCase(rawName.charAt(pivotUnderscore + 1)));
                }
                rawName.deleteCharAt(pivotUnderscore);
                // If the pivot for whitespace was ahead set it one back, because one character was deleted:
                if(pivotUnderscore < pivotWhitespace) {
                    pivotWhitespace--;
                }
            }

            if(pivotWhitespace != -1) {
                if(pivotWhitespace + 1 < rawName.length() && Character.isLowerCase(rawName.charAt(pivotWhitespace + 1))) {
                    rawName.setCharAt(pivotWhitespace + 1, Character.toUpperCase(rawName.charAt(pivotWhitespace + 1)));
                }
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
     * @param resource The resource for which to find a name.
     * @param config The configuration object specifying how names are built.
     * @return The identifier name that was determined.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    public String capitalizedIdentifier(ResourceObject resource, OntGenerationConfig config) throws RepositoryException {

        // Prune non allowed characters from the raw name:
        StringBuilder identifier = new StringBuilder(lowercaseIdentifier(resource, config));

        // Capitalize first character as defined by the coding conventions:
        identifier.setCharAt(0, Character.toUpperCase(identifier.charAt(0)));

        return identifier.toString();
    }

    /**
     * Extracts a plural form for this resource.
     * A trailing "s" is added and some simple grammatical rules (for english) are applied.
     * @param resource The resource for which to find a name.
     * @param config The configuration object specifying how names are built.
     * @return Same as {@link #lowercaseIdentifier(ResourceObject, OntGenerationConfig)}, but in a plural form.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    public String lowercasePluralIdentifier(ResourceObject resource, OntGenerationConfig config) throws RepositoryException {
        StringBuilder identifier = new StringBuilder();
        identifier.append(lowercaseIdentifier(resource, config));

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
     * @param resource The resource for which to find a name.
     * @param config The configuration object specifying how names are built.
     * @return Same as {@link #capitalizedIdentifier(ResourceObject, OntGenerationConfig)}, but in a plural form.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    public String capitalizedPluralIdentifier(ResourceObject resource, OntGenerationConfig config) throws RepositoryException {
        StringBuilder identifier = new StringBuilder(lowercasePluralIdentifier(resource, config));
        if(identifier.length() > 0) {
            identifier.setCharAt(0, Character.toUpperCase(identifier.charAt(0)));
        }
        return identifier.toString();
    }
}
