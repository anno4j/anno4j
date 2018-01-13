package com.github.anno4j.util.csv;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.BooleanLiteralImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.NumericLiteralImpl;
import org.openrdf.model.impl.URIImpl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * The CSV field parser converts the content of the RDF/CSV format (as recognized by {@link CSVParser}) into
 * Sesame values. The parser tries to detect some primitive types and converts them accordingly.
 * URI-prefixes can be registered to recognize short-hand URI syntax, e.g. {@code ex:foo}.
 * Also Yoda date/time <a href="http://www.joda.org/joda-time/apidocs/org/joda/time/format/DateTimeFormat.html">formats</a>
 * can be registered.
 * The parser supports the following types:
 * <ul>
 *     <li>URIs (full or with registered prefix)</li>
 *     <li>Integers in base 10, e.g. {@code 42}</li>
 *     <li>Decimal numbers in base 10 with '.' or ',' separator, e.g. {@code 42,00}</li>
 *     <li>Boolean. Must be either {@code false} or {@code true}</li>
 *     <li>Strings with a BCP47 language tag, e.g. {@code "Hallo"@de}</li>
 *     <li>Date/Time values in a registered format, e.g. {@code 08.12.2017} for format {@code dd.MM.yyyy}</li>
 * </ul>
 */
class CSVFieldParser {

    /**
     * Regex capturing Uniform Resource Identifiers (URI).
     * This regex was proposed <a href="http://blog.dieweltistgarnichtso.net/constructing-a-regular-expression-that-matches-uris">here</a>.
     */
    private static final Pattern URI_PATTERN = Pattern.compile("((?<=\\()[A-Za-z][A-Za-z0-9\\+\\.\\-]*:([A-Za-z0-9\\.\\-_~:/\\?#\\[\\]@!\\$&'\\(\\)\\*\\+,;=]|%[A-Fa-f0-9]{2})+(?=\\)))|([A-Za-z][A-Za-z0-9\\+\\.\\-]*:([A-Za-z0-9\\.\\-_~:/\\?#\\[\\]@!\\$&'\\(\\)\\*\\+,;=]|%[A-Fa-f0-9]{2})+)");

    /**
     * Regex capturing integers in decimal representation.
     */
    private static final Pattern INTEGER_PATTERN = Pattern.compile("^-?([0-9])+$");

    /**
     * Regex capturing decimal numbers in decimal notation with '.' or ',' as separator.
     */
    private static final Pattern DECIMAL_PATTERN = Pattern.compile("^-?[0-9]+(([.,])[0-9]+)?");

    /**
     * Regex capturing booleans in string representation.
     */
    private static final Pattern BOOLEAN_PATTERN = Pattern.compile("^(true|false)$");

    /**
     * Regex capturing an literal with a <a href="https://tools.ietf.org/html/bcp47">BCP 47</a> compliant language tag.
     */
    private static final Pattern LANG_STRING_PATTERN = Pattern.compile("\"(.*)\"@([a-zA-Z][a-zA-Z](-[a-zA-Z][a-zA-Z])?)");

    /**
     * Contains regex's capturing the supported date formats.
     */
    private List<Pattern> datePatterns = new LinkedList<>();

    private Map<String, String> uriPrefixes;

    /**
     * The formatter used for parsing dates.
     */
    private DateTimeFormatter dateTimeFormatter;

    /**
     * Initializes the parser with its supported formats and URI prefixes.
     * @param dateTimeFormats The formats that should be parsed. Each entry must be a YodaTime
     *                        <a href="http://www.joda.org/joda-time/apidocs/org/joda/time/format/DateTimeFormat.html">format</a>
     * @param uriPrefixes The URI prefixes that may occur in abbreviated URIs.
     */
    public CSVFieldParser(Collection<String> dateTimeFormats, Map<String, String> uriPrefixes) {
        // Create Regex's and parsers for all data/time formats:
        List<DateTimeParser> parsers = new ArrayList<>(dateTimeFormats.size());
        for (String format : dateTimeFormats) {
            parsers.add(DateTimeFormat.forPattern(format).getParser());
            datePatterns.add(getDateFormatAsRegex(format));
        }

        // Convert parsers to array and initialize the formatter:
        DateTimeParser[] parsersArray = parsers.toArray(new DateTimeParser[parsers.size()]);
        dateTimeFormatter = new DateTimeFormatterBuilder().append(null, parsersArray).toFormatter();

        this.uriPrefixes = new HashMap<>(uriPrefixes);
    }

    /**
     * Transforms a Yoda-time date/time
     * <a href="http://www.joda.org/joda-time/apidocs/org/joda/time/format/DateTimeFormat.html">format</a>
     * into a regex that captures all dates in the given format.
     * Note that there might be strings detected by the returned regex that are not compliant to the given format.
     * @param format The Yoda-time format.
     * @return The corresponding regex.
     */
    private static Pattern getDateFormatAsRegex(String format) {
        // Each format symbol can be captured by a regex. Concatenate a regex accordingly:
        StringBuilder regex = new StringBuilder();
        for(int i = 0; i < format.length(); i++) {
            switch (format.charAt(i)) {
                case 'G': regex.append("(BC|AD)"); break;
                case 'C': regex.append("([0-9]+)"); break;
                case 'Y': regex.append("([0-9]+)"); break;
                case 'x': regex.append("([0-9]+)"); break;
                case 'w': regex.append("([0-9]+)"); break;
                case 'e': regex.append("([0-9]+)"); break;
                case 'E': regex.append("(Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday|" +
                        "Mon|Tue|Wed|Thu|Fri|Sat|Sun)"); break;
                case 'y': regex.append("([0-9]+)"); break;
                case 'D': regex.append("([0-9]+)"); break;
                case 'M': regex.append("(([0-9]?[0-9])|January|February|March|April|June|July|" +
                        "August|September|October|November|December|" +
                        "Jan|Feb|Mar|Apr|Jun|Jul|Aug|Sep|Oct|Nov|Dec)"); break;
                case 'd': regex.append("([0-9]+)"); break;
                case 'a': regex.append("(am|pm|AM|PM)"); break;
                case 'K': regex.append("([0-9]+)"); break;
                case 'h': regex.append("([0-9]+)"); break;
                case 'H': regex.append("([0-9]+)"); break;
                case 'k': regex.append("([0-9]+)"); break;
                case 'm': regex.append("([0-9]+)"); break;
                case 's': regex.append("([0-9]+)"); break;
                case 'S': regex.append("([0-9]+)"); break;
                // If the current symbol isn't a regex symbol, simply escape and append it:
                default: regex.append(Pattern.quote(format.substring(i, i + 1)));
            }
        }
        // Compile the pattern:
        return Pattern.compile(regex.toString());
    }

    /**
     * Parses the given string to a date if the value corresponds to any format specified in the
     * {@link com.github.anno4j.util.csv.CSVParser.CSVParserConfig} this parser was initialized with.
     * @param value The value to parse.
     * @return Returns the parsed date or null if the given string isn't in a supported format.
     */
    private Date asDate(String value) {
        // First check whether any regex matches giving a first hit that this format could be parsed:
        boolean datePatternMatch = false;
        for (Pattern datePattern : datePatterns) {
            if(datePattern.matcher(value).matches()) {
                datePatternMatch = true;
                break; // Break for performance reasons
            }
        }

        // If any pattern matched try to parse the value as date:
        if(datePatternMatch) {
            try {
                return dateTimeFormatter.parseDateTime(value).toDateTime(DateTimeZone.UTC).toDate();
            } catch (IllegalArgumentException ignored) {
                return null; // Return null if this isn't really in a supported format.
            }

        } else {
            return null;
        }
    }

    /**
     * Returns the date as a literal that can be used by Anno4j. This literal has type {@code java:java.util.Date}
     * and a ISO8601 compliant label.
     * @param date The date to convert.
     * @return Returns the Sesame literal object.
     */
    private Value asDateTimeValue(Date date) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        return new LiteralImpl(format.format(date), new URIImpl("java:java.util.Date"));
    }

    /**
     * Checks whether a field value is an URI.
     * This predicate is also fulfilled if the value is an abbreviated URI with an prefix in {@link #uriPrefixes}.
     * @param value The value to check.
     * @return Returns true iff the value is an URI.
     */
    private boolean isURI(String value) {
        // Is this a URI?
        if(!URI_PATTERN.matcher(value).matches()) {
            // Is it an abbreviated URI?
            int separator = value.indexOf(":");
            if(separator != -1) {
                // Is this prefix known?
                String prefix = value.substring(0, separator);
                return uriPrefixes.containsKey(prefix);
            } else {
                return false;
            }

        } else {
            return true;
        }
    }

    /**
     * Converts an URI-string to a Sesame URI. The given value may be abbreviated with a prefix registered in
     * {@link #uriPrefixes}.
     * @param value The value to convert.
     * @return Returns the Sesame URI.
     * @throws CSVParser.CSVParseException Thrown if the prefix used isn't known or the URI is malformed.
     */
    private URI getAsURI(String value) throws CSVParser.CSVParseException {
        int separator = value.indexOf(":");
        if(separator != -1 && separator < value.length() - 1) {
            // Is this prefix known?
            String prefix = value.substring(0, separator);
            String suffix = value.substring(separator + 1, value.length());

            if(uriPrefixes.containsKey(prefix)) {
                return new URIImpl(uriPrefixes.get(prefix) + suffix);
            }

        }

        if(URI_PATTERN.matcher(value).matches()) {
            return new URIImpl(value);
        } else {
            throw new CSVParser.CSVParseException(value + " isn't a well-formed URI.");
        }
    }

    /**
     * Converts a string with format {@code "some text"@lang} into a Sesame language-tagged literal.
     * @param value The value to convert.
     * @return Returns the Sesame language-tagged literal object.
     */
    private Literal asLanguageTaggedLiteral(String value) {
        // Get the literal content:
        int literalEnd = value.lastIndexOf("\"");
        String label = value.substring(1, literalEnd); // Start after the first '"' until the last '"'

        int langStart = value.lastIndexOf("@") + 1; // Get the position of the tag separator
        String language = value.substring(langStart); // Everything after it is the language tag

        return new LiteralImpl(label, language.toLowerCase());

    }

    /**
     * Parses a CSV field value and converts it into a Sesame triple.
     * Dates are parsed according to the formats specified in the configuration this parser was initialized with.
     * @param value The value to parse.
     * @return Returns a sesame value derived from the given value or null if the given value is empty.
     * @throws com.github.anno4j.util.csv.CSVParser.CSVParseException Thrown if the given value is malformed.
     */
    public Value parseToRDFValue(String value) throws CSVParser.CSVParseException {
        Date date;

        if(value.isEmpty()) {
            return null;

        } else if(isURI(value)) {
            return getAsURI(value);

        } else if((date = asDate(value)) != null) {
            return asDateTimeValue(date);

        } else if(INTEGER_PATTERN.matcher(value).matches()) {
            return new NumericLiteralImpl(Integer.parseInt(value));

        } else if(DECIMAL_PATTERN.matcher(value).matches()) {
            return new NumericLiteralImpl(Double.parseDouble(value.replace(",", ".")));

        } else if(BOOLEAN_PATTERN.matcher(value).matches()) {
            return new BooleanLiteralImpl(Boolean.parseBoolean(value));

        } else if (LANG_STRING_PATTERN.matcher(value).matches()){
            return asLanguageTaggedLiteral(value);

        } else { // If no type matches return as untyped literal:
            return new LiteralImpl(value);
        }
    }

    /**
     * Registers the {@code prefix} to stand for the {@code uri} prefix.
     * @param prefix The shorthand to register.
     * @param uri The URI prefix the shorthand should stand for.
     */
    public void registerPrefix(String prefix, String uri) {
        uriPrefixes.put(prefix,uri);
    }
}
