package com.github.anno4j.model;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.impl.body.TextualBody;
import com.github.anno4j.model.impl.targets.SpecificResource;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.idGenerator.IDGenerator;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.RDFObject;
import org.openrdf.repository.object.behaviours.RDFObjectImpl;
import org.openrdf.repository.object.exceptions.ObjectPersistException;

import java.util.HashSet;

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
        body.addRight(this.anno4j.createObject(ResourceObject.class, (Resource) RIGHT));

        SpecificResource target = this.anno4j.createObject(SpecificResource.class);
        HashSet<ResourceObject> rights = new HashSet<>();
        rights.add(this.anno4j.createObject(ResourceObject.class, (Resource) RIGHT2));
        rights.add(this.anno4j.createObject(ResourceObject.class, (Resource) RIGHT3));
        target.setRights(rights);

        annotation.addBody(body);
        annotation.addTarget(target);

        Annotation result = this.anno4j.findByID(Annotation.class, annotation.getResourceAsString());

        assertEquals(0, result.getRights().size());
        assertEquals(1, result.getBodies().iterator().next().getRights().size());
        assertEquals(2, ((SpecificResource) result.getTargets().toArray()[0]).getRights().size());
    }

    @Test
    public void testCanonical() throws RepositoryException, IllegalAccessException, InstantiationException {
        String canonicalURI = "http://somepage/canonical/";

        Annotation annotation = this.anno4j.createObject(Annotation.class);

        Annotation result = this.anno4j.findByID(Annotation.class, annotation.getResourceAsString());

        assertEquals(null, result.getCanonical());

        ResourceObject canonical = this.anno4j.createObject(ResourceObject.class);
        canonical.setResourceAsString(canonicalURI);
        annotation.setCanonical(canonical);

        result = this.anno4j.findByID(Annotation.class, annotation.getResourceAsString());

        assertEquals(canonicalURI, result.getCanonical().getResourceAsString());
    }

    @Test
    public void testVia() throws RepositoryException, IllegalAccessException, InstantiationException {
        String viaURI = "http://somepage/via1/";
        ResourceObject via = this.anno4j.createObject(ResourceObject.class);
        via.setResourceAsString(viaURI);

        String viaURI2 = "http://somepage/via2/";
        ResourceObject via2 = this.anno4j.createObject(ResourceObject.class);
        via2.setResourceAsString(viaURI2);

        String viaURI3 = "http://somepage/via3/";
        ResourceObject via3 = this.anno4j.createObject(ResourceObject.class);
        via3.setResourceAsString(viaURI3);

        TextualBody body = this.anno4j.createObject(TextualBody.class);

        TextualBody result = this.anno4j.findByID(TextualBody.class, body.getResourceAsString());

        assertEquals(0, result.getVia().size());

        body.addVia(via);

        result = this.anno4j.findByID(TextualBody.class, body.getResourceAsString());

        assertEquals(1, result.getVia().size());
        assertEquals(viaURI, result.getVia().iterator().next().getResourceAsString());

        HashSet<ResourceObject> vias = new HashSet<>();
        vias.add(via2);
        vias.add(via3);

        body.setVia(vias);

        result = this.anno4j.findByID(TextualBody.class, body.getResourceAsString());

        assertEquals(2, result.getVia().size());
    }
}