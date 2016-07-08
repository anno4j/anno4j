package com.github.anno4j.model;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.targets.SpecificResource;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.repository.RepositoryException;

import static org.junit.Assert.*;

/**
 * Test suite for the interfaces extending the CreationProvenance interface
 */
public class CreationProvenanceTest {

    private Anno4j anno4j;

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
    }

    private final static String GOOD_DATE = "2015-01-28T12:00:00+01:00";
    private final static String GOOD_DATE_2 = "2015-01-28T12:00:00+00:00";
    private final static String BAD_DATE = "2015--01-28T12:00:00Z";

    @Test
    public void testGoodDates() {
        DateTimeFormatter format = ISODateTimeFormat.dateTimeNoMillis().withZoneUTC();
        format.parseDateTime(GOOD_DATE);
        format.parseDateTime(GOOD_DATE_2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadDates() {
        DateTimeFormatter format = ISODateTimeFormat.dateTimeNoMillis().withZoneUTC();
        format.parseDateTime(BAD_DATE);
    }

    @Test
    public void testSetModified() throws RepositoryException, IllegalAccessException, InstantiationException {
        Annotation annotation = this.anno4j.createObject(Annotation.class);
        String date = "2016-07-04T12:00:00Z";

        annotation.setModified(date);

        Annotation result = this.anno4j.findByID(Annotation.class, annotation.getResourceAsString());

        assertEquals(date, result.getModified());
    }

    @Test
    public void testSetModified2() throws RepositoryException, IllegalAccessException, InstantiationException {
        int year = 2015;
        int month = 12;
        int day = 16;
        int hours = 12;
        int minutes = 0;
        int seconds = 0;

        SpecificResource spec = this.anno4j.createObject(SpecificResource.class);
        spec.setModified(year, month, day, hours, minutes, seconds);

        SpecificResource result = this.anno4j.findByID(SpecificResource.class, spec.getResourceAsString());

        assertEquals("2015-12-16T12:00:00Z", result.getModified());
    }
}