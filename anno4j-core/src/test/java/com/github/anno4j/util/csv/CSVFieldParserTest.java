package com.github.anno4j.util.csv;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import org.junit.Test;
import org.openrdf.model.impl.*;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Test for {@link CSVFieldParser}
 */
public class CSVFieldParserTest {

    private static final Collection<String> DEFAULT_DATE_FORMATS = Collections.singletonList("dd.MM.yyyy");

    private static final Map<String, String> DEFAULT_URI_PREFIXES;
    static {
        DEFAULT_URI_PREFIXES = new HashMap<>();
        DEFAULT_URI_PREFIXES.put("ex", "http://example.org/");
    }

    @Test
    public void testEmpty() throws Exception {
        CSVFieldParser parser = new CSVFieldParser(DEFAULT_DATE_FORMATS, DEFAULT_URI_PREFIXES);
        assertNull(parser.parseToRDFValue(""));
    }

    @Test
    public void testUri() throws Exception {
        CSVFieldParser parser = new CSVFieldParser(DEFAULT_DATE_FORMATS, DEFAULT_URI_PREFIXES);
        // Test full URL:
        assertEquals(new URIImpl("http://test.com/foo"), parser.parseToRDFValue("http://test.com/foo"));
        // Test full URN:
        assertEquals(new URIImpl("urn:test:foo"), parser.parseToRDFValue("urn:test:foo"));
        // Test with registered prefix:
        assertEquals(new URIImpl("http://example.org/foo"), parser.parseToRDFValue("ex:foo"));
        // Test with unregistered prefix:
        assertEquals(new URIImpl("test:foo"), parser.parseToRDFValue("test:foo"));
    }

    @Test
    public void testInteger() throws Exception {
        CSVFieldParser parser = new CSVFieldParser(DEFAULT_DATE_FORMATS, DEFAULT_URI_PREFIXES);
        // Test valid positive integer:
        assertEquals(new NumericLiteralImpl(42), parser.parseToRDFValue("42"));
        // Test valid negative integer:
        assertEquals(new NumericLiteralImpl(-42), parser.parseToRDFValue("-42"));
        // Test invalid integer:
        assertEquals(new LiteralImpl("0xCAFE"), parser.parseToRDFValue("0xCAFE"));
    }

    @Test
    public void testDecimal() throws Exception {
        CSVFieldParser parser = new CSVFieldParser(DEFAULT_DATE_FORMATS, DEFAULT_URI_PREFIXES);
        // Test dot separator:
        assertEquals(new NumericLiteralImpl(13.37), parser.parseToRDFValue("13.37"));
        // Test comma separator:
        assertEquals(new NumericLiteralImpl(13.37), parser.parseToRDFValue("13,37"));
        // Test negative:
        assertEquals(new NumericLiteralImpl(-13.37), parser.parseToRDFValue("-13.37"));
        // Test illegal decimal:
        assertEquals(new LiteralImpl("1.5e10"), parser.parseToRDFValue("1.5e10"));
    }

    @Test
    public void testBoolean() throws Exception {
        CSVFieldParser parser = new CSVFieldParser(DEFAULT_DATE_FORMATS, DEFAULT_URI_PREFIXES);
        // Test true:
        assertEquals(new BooleanLiteralImpl(true), parser.parseToRDFValue("true"));
        // Test false:
        assertEquals(new BooleanLiteralImpl(false), parser.parseToRDFValue("false"));
    }

    @Test
    public void testLangString() throws Exception {
        CSVFieldParser parser = new CSVFieldParser(DEFAULT_DATE_FORMATS, DEFAULT_URI_PREFIXES);
        // Test without region:
        assertEquals(new LiteralImpl("Hallo", "de"), parser.parseToRDFValue("\"Hallo\"@de"));
        // Test without region (uppercase):
        assertEquals(new LiteralImpl("Hallo", "de"), parser.parseToRDFValue("\"Hallo\"@DE"));
        // Test with region:
        assertEquals(new LiteralImpl("Hallo", "de-AT"), parser.parseToRDFValue("\"Hallo\"@de-AT"));
        // Test non-BCP47 compliant:
        assertEquals(new LiteralImpl("\"Hallo\"@foobar"), parser.parseToRDFValue("\"Hallo\"@foobar"));
    }

    @Test
    public void testDate() throws Exception {
        XMLGregorianCalendar calendar = new XMLGregorianCalendarImpl();
        calendar.setYear(2017);
        calendar.setMonth(12);
        calendar.setDay(11);

        CSVFieldParser parser = new CSVFieldParser(Arrays.asList("dd.MM.yyyy", "dd _ MM ! yyyy"), DEFAULT_URI_PREFIXES);
        // Test default:
        assertEquals(new CalendarLiteralImpl(calendar), parser.parseToRDFValue("11.12.2017"));
        // Test custom:
        assertEquals(new CalendarLiteralImpl(calendar), parser.parseToRDFValue("11 _ 12 ! 2017"));
        // Test unsupported:
        assertEquals(new LiteralImpl("11 :) 12 :D 2017"), parser.parseToRDFValue("11 :) 12 :D 2017"));
    }

    @Test
    public void testDateTime() throws Exception {
        XMLGregorianCalendar calendar = new XMLGregorianCalendarImpl();
        calendar.setYear(2017);
        calendar.setMonth(12);
        calendar.setDay(11);
        calendar.setHour(22);
        calendar.setMinute(30);
        calendar.setSecond(0);

        CSVFieldParser parser = new CSVFieldParser(Arrays.asList("dd.MM.yyyy HH:mm"), DEFAULT_URI_PREFIXES);
        // Test valid:
        assertEquals(new CalendarLiteralImpl(calendar), parser.parseToRDFValue("11.12.2017 22:30"));
        // Test invalid:
        assertEquals(new LiteralImpl("11 :) 12 :D 2017 22.30"), parser.parseToRDFValue("11 :) 12 :D 2017 22.30"));
    }
}