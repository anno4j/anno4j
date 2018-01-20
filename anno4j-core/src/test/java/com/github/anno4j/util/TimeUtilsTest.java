package com.github.anno4j.util;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.repository.RepositoryException;

import static org.junit.Assert.*;

public class TimeUtilsTest {

    private Anno4j anno4j;

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
    }

    private final static String WADM_EXAMPLE_11_TIME_STRING = "2015-01-28T12:00:00Z";
    private final static String WADM_EXAMPLE_11_TIME_STRING_WITH_MILLIS = "2015-01-28T12:00:00.0Z";
    private final static String WADM_EXAMPLE_11_TIME_STRING_WITH_MILLIS_2 = "2015-01-28T12:00:00.1234Z";
    private final static String WADM_EXAMPLE_11_TIME_STRING_WITH_MILLIS_3 = "2015-01-05T12:00:00.1234Z";

    @Test
    public void testTimeStringImplementation(){
        assertTrue(TimeUtils.testTimeString(WADM_EXAMPLE_11_TIME_STRING));

        assertTrue(TimeUtils.testTimeString(WADM_EXAMPLE_11_TIME_STRING_WITH_MILLIS));
    }

    @Test
    public void testPrivateTimes() {
        assertTrue(createNoMillis(WADM_EXAMPLE_11_TIME_STRING));
        assertFalse(createNoMillis(WADM_EXAMPLE_11_TIME_STRING_WITH_MILLIS));

        assertTrue(createWithMillis(WADM_EXAMPLE_11_TIME_STRING_WITH_MILLIS));
        assertFalse(createWithMillis(WADM_EXAMPLE_11_TIME_STRING));
    }

    @Test
    public void testCreateTimeStringWithMillis() {
        String testMillis = TimeUtils.createTimeStringWithMillis(2015, 1, 28, 12, 0, 0, 0, "UTC");

        assertEquals(WADM_EXAMPLE_11_TIME_STRING_WITH_MILLIS, testMillis);
        assertTrue(TimeUtils.testTimeString(testMillis));

        String testMillis2 = TimeUtils.createTimeStringWithMillis(2015, 1, 28, 12, 0, 0, 1234, "UTC");

        assertEquals(WADM_EXAMPLE_11_TIME_STRING_WITH_MILLIS_2, testMillis2);
        assertTrue(TimeUtils.testTimeString(testMillis2));

        String testMillis3 = TimeUtils.createTimeStringWithMillis(2015, 1, 5, 12, 0, 0, 1234, "UTC");

        assertEquals(WADM_EXAMPLE_11_TIME_STRING_WITH_MILLIS_3, testMillis3);
        assertTrue(TimeUtils.testTimeString(testMillis3));
    }

    @Test
    public void testCreateTimeStringWithAnnotation() throws RepositoryException, IllegalAccessException, InstantiationException {
        Annotation annotation = this.anno4j.createObject(Annotation.class);

        annotation.setGenerated(2015, 1, 28, 12, 0, 0, 0, "UTC");

        annotation.setModified(2015, 1, 28, 12, 0, 0, 1234, "UTC");

        annotation.setCreated(2015, 1, 5, 12, 0, 0, 1234, "UTC");
    }

    private static boolean createNoMillis(String time) throws IllegalArgumentException {
        DateTimeFormatter format = ISODateTimeFormat.dateTimeNoMillis().withZoneUTC();

        try {
            format.parseDateTime(time);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean createWithMillis(String time) throws IllegalArgumentException {
        DateTimeFormatter format = ISODateTimeFormat.dateTime().withZoneUTC();

        try {
            format.parseDateTime(time);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}