package com.github.anno4j.util.csv;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.namespaces.OWL;
import com.github.anno4j.model.namespaces.RDF;
import com.github.anno4j.model.namespaces.RDFS;
import com.github.anno4j.model.namespaces.XSD;
import de.siegmar.fastcsv.reader.CsvContainer;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Parses the custom RDF/CSV format and stores triples in a connected RDF repository.
 * A RDF/CSV file is a CSV file (which properties can be defined with {@link CSVParserConfig}) with a header row
 * where the first column defines the subject of a triple and each other column corresponds to a RDF property.
 * Every cell contains the object of a triple with the respective subject (defined in first column) and predicate
 * (defined by the column of the cell).
 * The parser supports URI prefixes and some primitive literal types.
 */
public class CSVParser {

    /**
     * Exception signalizing that a parsed RDF/CSV file is malformed.
     */
    public static class CSVParseException extends Exception {

        /**
         * Number of the row where the error was located.
         * Counting starts at one. A value of 0 should be used to signalize that the number is unspecified.
         */
        private int row;

        /**
         * Number of the column where the error was located.
         * Counting starts at one. A value of 0 should be used to signalize that the number is unspecified.
         */
        private int column;

        /**
         * Initializes the exception without a message.
         */
        public CSVParseException() {
        }

        /**
         * Initializes the exception with a message and a location.
         * @param message The message.
         * @param row The number of the row where the error was located.
         *            Counting starts at one. A value of 0 should be used to signalize that the number is unspecified.
         * @param column The number of the column where the error was located.
         *            Counting starts at one. A value of 0 should be used to signalize that the number is unspecified.
         */
        public CSVParseException(String message, int row, int column) {
            super(message);
            this.row = row;
            this.column = column;
        }

        /**
         * Initializes the exception with a message and the location unspecified.
         * @param message
         */
        public CSVParseException(String message) {
            super(message);
        }

        /**
         * Initialize wrapping another exception.
         * @param cause The exception wrapped.
         */
        public CSVParseException(Throwable cause) {
            super(cause);
        }

        /**
         * @return The number of the row where the error was located.
         * Counting starts at one. A value of 0 should be used to signalize that the number is unspecified.
         */
        public int getRow() {
            return row;
        }

        /**
         * @return The number of the column where the error was located.
         * Counting starts at one. A value of 0 should be used to signalize that the number is unspecified.
         */
        public int getColumn() {
            return column;
        }
    }

    public static class CSVParserConfig {

        private char fieldSeparator;

        private char textDelimiter;

        private Charset charset;

        public char getFieldSeparator() {
            return fieldSeparator;
        }

        public void setFieldSeparator(char fieldSeparator) {
            this.fieldSeparator = fieldSeparator;
        }

        public char getTextDelimiter() {
            return textDelimiter;
        }

        public void setTextDelimiter(char textDelimiter) {
            this.textDelimiter = textDelimiter;
        }

        public Charset getCharset() {
            return charset;
        }

        public void setCharset(Charset charset) {
            this.charset = charset;
        }
    }

    public static final List<String> DEFAULT_DATE_FORMATS = Arrays.asList(
            "dd.MM.yyyy", "dd.MM.yyyy HH:mm:ss", "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss"
    );

    public static final Map<String, String> DEFAULT_URI_PREFIXES;
    static { // Intialize with default mappings:
        DEFAULT_URI_PREFIXES = new HashMap<>();
        DEFAULT_URI_PREFIXES.put("rdf", RDF.NS);
        DEFAULT_URI_PREFIXES.put("rdfs", RDFS.NS);
        DEFAULT_URI_PREFIXES.put("owl", OWL.NS);
        DEFAULT_URI_PREFIXES.put("xsd", XSD.NS);
    }

    /**
     * Parser configuration as defined in <a href="https://tools.ietf.org/html/rfc4180">RFC 4180</a>.
     */
    public static final CSVParserConfig RFC4180_CONFIG;
    static {
        RFC4180_CONFIG = new CSVParserConfig();
        RFC4180_CONFIG.setFieldSeparator(',');
        RFC4180_CONFIG.setTextDelimiter('"');
        RFC4180_CONFIG.setCharset(StandardCharsets.UTF_8);
    }

    /**
     * Connection to the Sesame repository where the read triples are inserted.
     */
    private RepositoryConnection connection;

    /**
     * The parser used for converting CSV cell values into Sesame values (URIs, literals).
     */
    private CSVFieldParser fieldParser;

    /**
     * The current subject. In the CSV the first column specified the subject of the triples. It may be omitted
     * in subsequent rows if it stays the same.
     */
    private URI currentSubject;

    public CSVParser(Anno4j anno4j) throws RepositoryException {
        this(anno4j, DEFAULT_DATE_FORMATS, DEFAULT_URI_PREFIXES);
    }

    public CSVParser(Anno4j anno4j, Collection<String> dateTimeFormats, Map<String, String> uriPrefixes) throws RepositoryException {
        this(anno4j.getRepository().getConnection(), dateTimeFormats, uriPrefixes);
    }

    public CSVParser(RepositoryConnection connection, Collection<String> dateTimeFormats, Map<String, String> uriPrefixes) {
        this.connection = connection;
        fieldParser = new CSVFieldParser(dateTimeFormats, uriPrefixes);
    }

    /**
     * Updates the current subject. In the CSV the first column specified the subject of the triples. It may be omitted
     * in subsequent rows if it stays the same. This method updates the value of {@link #currentSubject} if the subject
     * was redefined.
     * @param row The current row.
     * @param rowNumber The number of the current row.
     * @throws CSVParseException Thrown if an error occurs while parsing cell values.
     */
    private void updateSubject(CsvRow row, int rowNumber) throws CSVParseException {
        // Is there a new subject specified in the first cell?
        if(!row.getField(0).isEmpty()) {
            // Parse the first cell of the current row:
            Value value =  fieldParser.parseToRDFValue(row.getField(0));

            if(value instanceof URI) { // Subjects must be URIs
                currentSubject = (URI) value;

            } else {
                throw new CSVParseException("The subject " + row.getField(0)
                        + " must be a URI, but was recognized as " + value.getClass().getSimpleName(), rowNumber, 1);
            }

        } else if(currentSubject == null) { // There must be a subject:
            throw new CSVParseException("No subject was defined.", rowNumber, 0);
        }
    }

    /**
     * Parses the used predicates from the CSV header. In the CSV each column except the first (which denotes the subject)
     * corresponds to a RDF property. This method returns these properties in the same order as they were defined in the
     * header.
     * @param csv The CSV container (with header enabled) from which to get the predicates.
     * @return Returns the predicates in the order they are defined in.
     * @throws CSVParseException Thrown if an error occurs while parsing a header value.
     */
    private List<URI> getCSVPredicates(CsvContainer csv) throws CSVParseException {
        List<String> headerValues = csv.getHeader();
        List<URI> predicates = new ArrayList<>(headerValues.size() - 1);
        // Skip the first row (the subject):
        for(String headerValue : headerValues.subList(1, headerValues.size())) {
            Value parsed = fieldParser.parseToRDFValue(headerValue);

            if(parsed instanceof URI) {
                predicates.add((URI) parsed);
            } else {
                throw new CSVParseException("Header entry " + headerValue + " must be a property URI, but was recognized as " + parsed.getClass().getSimpleName());
            }
        }

        return predicates;
    }

    /**
     * Persists the triples specified in the given CSV row. The current subject (see {@link #currentSubject}) is updated
     * if it's redefined in the given row. All parsed triples are added to the given connected repository.
     * @param row The row to parse.
     * @param properties The URIs of the properties that correspond to the columns of the CSV. These must be in the same order
     *                   as specified in the CSV header.
     * @param rowNumber The number of the current row. Counting starts at 1.
     * @param connection A connection to the repository that should receive the parsed triples.
     * @throws CSVParseException Thrown if the value of a cell is malformed.
     * @throws RepositoryException Thrown if there is an error while inserting a parsed triple into the repository.
     */
    private void persistRow(CsvRow row, List<URI> properties, int rowNumber, RepositoryConnection connection) throws CSVParseException, RepositoryException {
        // Check that the column count is equal to number of properties + 1 (the subject):
        if(row.getFieldCount() != properties.size() + 1) {
            throw new CSVParseException("Line " + rowNumber + " has " + row.getFieldCount() + " fields, but "
                    + properties.size() + " properties are declared in header.", rowNumber, 0);
        }

        // Update the current subject:
        updateSubject(row, rowNumber);

        for(int columnNumber = 1; columnNumber < row.getFieldCount(); columnNumber++) {
            URI predicate = properties.get(columnNumber - 1); // Property list is one behind, because there is the subject at the beginning of the row.

            // Parse the current cell:
            Value object;
            try {
                object = fieldParser.parseToRDFValue(row.getField(columnNumber));
            } catch (CSVParseException e) { // Rethrow exception with row- and column-number
                throw new CSVParseException(e.getMessage(), rowNumber, columnNumber);
            }

            // Insert triple with current subject/predicate and the parsed value if cell wasn't empty:
            if(object != null) {
                connection.add(new StatementImpl(currentSubject, predicate, object));
            }
        }
    }

    /**
     * Registers the {@code prefix} to stand for the {@code uri} prefix.
     * @param prefix The shorthand to register.
     * @param namespace The URI prefix the shorthand should stand for.
     */
    public void registerPrefix(String prefix, String namespace) {
        fieldParser.registerPrefix(prefix, namespace);
    }

    /**
     * Parses a <a href="https://tools.ietf.org/html/rfc4180">RFC 4180</a> compliant RDF/CSV file and
     * stores the triple in the connected repository.
     * @param file The CSV file to read from.
     * @throws IOException Thrown if an error occurs while reading the file or if not all rows have same length.
     * @throws RepositoryException Thrown if there is an error while inserting triples into the repository.
     * @throws CSVParseException Thrown if a cell is found malformed.
     */
    public void parseCSVFile(File file) throws RepositoryException, CSVParseException, IOException {
        parseCSVFile(file, RFC4180_CONFIG);
    }

    /**
     * Parses a RDF/CSV file and stores the triples in the connected repository.
     * @param file The CSV file to read from.
     * @param config The configuration containing information about the file.
     * @throws IOException Thrown if an error occurs while reading the file or if not all rows have same length.
     * @throws RepositoryException Thrown if there is an error while inserting triples into the repository.
     * @throws CSVParseException Thrown if a cell is found malformed.
     */
    public void parseCSVFile(File file, CSVParserConfig config) throws IOException, RepositoryException, CSVParseException {
        CsvReader reader = new CsvReader();
        reader.setContainsHeader(true);
        reader.setSkipEmptyRows(true);
        reader.setSkipEmptyRows(true);
        reader.setFieldSeparator(config.getFieldSeparator());
        reader.setTextDelimiter(config.getTextDelimiter());
        reader.setErrorOnDifferentFieldCount(true);

        CsvContainer csv = reader.read(file, config.getCharset());

        List<URI> predicates = getCSVPredicates(csv); // Get the predicates of the CSV
        List<CsvRow> rows = csv.getRows(); // Get the data rows of the CSV

        for(int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            CsvRow row = rows.get(rowIndex);
            int rowNumber = rowIndex + 1;

            persistRow(row, predicates, rowNumber, connection);
        }
    }
}
