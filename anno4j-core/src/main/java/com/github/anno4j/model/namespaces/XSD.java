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

    /**
     * The string datatype represents character strings in XML.
     */
    public final static String STRING = NS + "string";

    /**
     * boolean represents the values of two-valued logic.
     */
    public final static String BOOLEAN = NS + "boolean";

    /**
     * decimal represents a subset of the real numbers, which can be represented by decimal numerals
     */
    public final static String DECIMAL = NS + "decimal";

    /**
     * The float datatype is patterned after the IEEE single-precision 32-bit floating point datatype [IEEE 754-2008].
     */
    public final static String FLOAT = NS + "float";

    /**
     * The double datatype is patterned after the IEEE double-precision 64-bit floating point datatype [IEEE 754-2008].
     */
    public final static String DOUBLE = NS + "double";

    /**
     * duration is a datatype that represents durations of time.
     */
    public final static String DURATION = NS + "duration";

    /**
     * dateTime represents instants of time, optionally marked with a particular time zone offset.
     */
    public final static String DATE_TIME = NS + "dateTime";

    /**
     * time represents instants of time that recur at the same point in each calendar day,
     * or that occur in some arbitrary calendar day.
     */
    public final static String TIME = NS + "time";

    /**
     * date represents top-open intervals of exactly one day in length on the timelines of dateTime,
     * beginning on the beginning moment of each day, up to but not including the beginning moment of the next day).
     */
    public final static String DATE = NS + "date";

    /**
     * gYearMonth represents specific whole Gregorian months in specific Gregorian years.
     */
    public final static String G_YEAR_MONTH = NS + "gYearMonth";

    /**
     * gYear represents Gregorian calendar years.
     */
    public final static String G_YEAR = NS + "gYear";

    /**
     * gMonthDay represents whole calendar days that recur at the same point in each calendar year,
     * or that occur in some arbitrary calendar year.
     */
    public final static String G_MONTH_DAY = NS + "gMonthDay";

    /**
     * gDay represents whole days within an arbitrary month—days that recur at the same point in each (Gregorian) month.
     */
    public final static String G_DAY = NS + "gDay";

    /**
     * gMonth represents whole (Gregorian) months within an arbitrary year—months that recur
     * at the same point in each year.
     */
    public final static String G_MONTH = NS + "gMonth";

    /**
     * hexBinary represents arbitrary hex-encoded binary data.
     */
    public final static String HEX_BINARY = NS + "hexBinary";

    /**
     * base64Binary represents arbitrary Base64-encoded binary data.
     */
    public final static String BASE64_BINARY = NS + "base64Binary";

    /**
     * anyURI represents an Internationalized Resource Identifier Reference (IRI).
     * An anyURI value can be absolute or relative, and may have an optional
     * fragment identifier (i.e., it may be an IRI Reference).
     */
    public final static String ANY_URI = NS + "anyURI";

    /**
     * QName represents XML qualified names. The value space of QName is the set of tuples {namespace name, local part},
     * where namespace name is an anyURI and local part is an NCName.
     */
    public final static String Q_NAME = NS + "QName";

    /**
     * NOTATION represents the NOTATION attribute type from XML.
     */
    public final static String NOTATION = NS + "NOTATION";
}
