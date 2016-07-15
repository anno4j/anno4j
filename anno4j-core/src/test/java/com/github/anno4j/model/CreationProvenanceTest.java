package com.github.anno4j.model;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.impl.body.TextualBody;
import com.github.anno4j.model.impl.targets.SpecificResource;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.exceptions.ObjectPersistException;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

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
    private final static String BAD_DATE2 = "2015-01-28T12:00:00";

    private final static URIImpl RIGHT = new URIImpl("http://example.org/right");
    private final static URIImpl RIGHT2 = new URIImpl("http://example.org/right2");
    private final static URIImpl RIGHT3 = new URIImpl("http://example.org/right3");

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
    public void testGoodDates2() throws RepositoryException, IllegalAccessException, InstantiationException {
        Annotation annotation = this.anno4j.createObject(Annotation.class);

        annotation.setCreated(GOOD_DATE);

        Annotation result = this.anno4j.findByID(Annotation.class, annotation.getResourceAsString());

        assertEquals(GOOD_DATE, result.getCreated());
    }

    @Test(expected = ObjectPersistException.class)
    public void testBadDate2() throws RepositoryException, IllegalAccessException, InstantiationException {
        Annotation annotation = this.anno4j.createObject(Annotation.class);

        annotation.setCreated(BAD_DATE);
    }

    @Test(expected = ObjectPersistException.class)
    public void testBadDate3() throws RepositoryException, IllegalAccessException, InstantiationException {
        Annotation annotation = this.anno4j.createObject(Annotation.class);

        annotation.setModified(BAD_DATE2);
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
        String timezone = "UTC";
        String timezone2 = "America/Argentina/Ushuaia";

        SpecificResource spec = this.anno4j.createObject(SpecificResource.class);
        spec.setModified(year, month, day, hours, minutes, seconds, timezone);

        SpecificResource result = this.anno4j.findByID(SpecificResource.class, spec.getResourceAsString());

        assertEquals("2015-12-16T12:00:00Z", result.getModified());

        spec.setModified(year, month, day, hours, minutes, seconds, timezone2);

        result = this.anno4j.findByID(SpecificResource.class, spec.getResourceAsString());

        assertEquals("2015-12-16T12:00:00-03:00", result.getModified());
    }

    @Test
    public void testRights() throws RepositoryException, IllegalAccessException, InstantiationException {
        Annotation annotation = this.anno4j.createObject(Annotation.class);

        TextualBody body = this.anno4j.createObject(TextualBody.class);
        body.addRight(this.anno4j.createObject(ResourceObject.class, RIGHT));

        SpecificResource target = this.anno4j.createObject(SpecificResource.class);
        HashSet<ResourceObject> rights = new HashSet<>();
        rights.add(this.anno4j.createObject(ResourceObject.class, RIGHT2));
        rights.add(this.anno4j.createObject(ResourceObject.class, RIGHT3));
        target.setRights(rights);

        annotation.setBody(body);
        annotation.addTarget(target);

        Annotation result = this.anno4j.findByID(Annotation.class, annotation.getResourceAsString());

        assertEquals(0, result.getRights().size());
        assertEquals(1, result.getBody().getRights().size());
        assertEquals(2, ((SpecificResource) result.getTarget().toArray()[0]).getRights().size());
    }
}