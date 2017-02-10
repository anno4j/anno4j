package com.github.anno4j.model.namespaces;

/**
 * Ontology Class for the XML Schema Definition (xsd:).
 * See <a href="https://www.w3.org/TR/xmlschema11-2/">https://www.w3.org/TR/xmlschema11-2/</a>
 */
public class XSD {

    /**
     * Textual representation of the namespace.
     */
    public final static String NS = "http://www.w3.org/2001/XMLSchema#";

    public final static String STRING = NS + "string";

    public final static String BOOLEAN = NS + "boolean";

    public final static String DECIMAL = NS + "decimal";

    public final static String FLOAT = NS + "float";

    public final static String DOUBLE = NS + "double";

    public final static String DURATION = NS + "duration";

    public final static String DATE_TIME = NS + "dateTime";

    public final static String TIME = NS + "time";

    public final static String DATE = NS + "date";

    public final static String G_YEAR_MONTH = NS + "gYearMonth";

    public final static String G_YEAR = NS + "gYear";

    public final static String G_MONTH_DAY = NS + "gMonthDay";

    public final static String G_DAY = NS + "gDay";

    public final static String G_MONTH = NS + "gMonth";

    public final static String HEX_BINARY = NS + "hexBinary";

    public final static String BASE64_BINARY = NS + "base64Binary";

    public final static String ANY_URI = NS + "anyURI";

    public final static String Q_NAME = NS + "QName";

    public final static String NOTATION = NS + "NOTATION";
}
