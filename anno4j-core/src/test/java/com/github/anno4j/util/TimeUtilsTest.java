package com.github.anno4j.util;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

import static org.junit.Assert.*;

public class TimeUtilsTest {

    private final static String WADM_EXAMPLE_11_TIME_STRING = "2015-01-28T12:00:00Z";
    private final static String WADM_EXAMPLE_11_TIME_STRING_WITH_MILLIS = "2015-01-28T12:00:00.0Z";

    @Test
    public void testTimeStringImplementation(){
        assertTrue(TimeUtils.testTimeString(WADM_EXAMPLE_11_TIME_STRING));

        assertTrue(TimeUtils.testTimeString(WADM_EXAMPLE_11_TIME_STRING_WITH_MILLIS));
    }

    @Test
    public void testPrivateTimes() {
        assertTrue(this.testNoMillis(WADM_EXAMPLE_11_TIME_STRING));
        assertFalse(this.testNoMillis(WADM_EXAMPLE_11_TIME_STRING_WITH_MILLIS));

        assertTrue(this.testWithMillis(WADM_EXAMPLE_11_TIME_STRING_WITH_MILLIS));
        assertFalse(this.testWithMillis(WADM_EXAMPLE_11_TIME_STRING));
    }

    private static boolean testNoMillis(String time) throws IllegalArgumentException {
        DateTimeFormatter format = ISODateTimeFormat.dateTimeNoMillis().withZoneUTC();

        try {
            format.parseDateTime(time);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean testWithMillis(String time) throws IllegalArgumentException {
        DateTimeFormatter format = ISODateTimeFormat.dateTime().withZoneUTC();

        try {
            format.parseDateTime(time);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}