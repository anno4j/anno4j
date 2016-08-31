package com.github.anno4j.model.impl;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.*;
import com.github.anno4j.model.impl.style.CssStylesheet;
import com.github.anno4j.model.impl.targets.SpecificResource;
import com.github.anno4j.querying.QueryService;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.annotations.Iri;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AnnotationTest {

    private Anno4j anno4j;

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
    }

    @Test
    public void testPersistAnnotation() throws Exception {
        String timestamp = "2015-01-28T12:00:00Z";

        // Create test annotation
        Annotation annotation = anno4j.createObject(Annotation.class);
        annotation.setGenerated(timestamp);
        annotation.setCreated(timestamp);

        // query persisted object
        Annotation result = anno4j.findByID(Annotation.class, annotation.getResourceAsString());

        assertEquals(annotation.getResource().toString(), result.getResource().toString());
        assertEquals(annotation.getCreated(), result.getCreated());
        assertEquals(annotation.getGenerated(), result.getGenerated());
    }

    @Test
    public void testResourceDefinition() throws Exception {
        // Create annotation
        Annotation annotation = anno4j.createObject(Annotation.class, (Resource) new URIImpl("http://www.somepage.org/resource1/"));

        // Query persisted object
        Annotation result = anno4j.findByID(Annotation.class, annotation.getResourceAsString());

        // Tests
        assertEquals(annotation.getResource(), result.getResource());
    }

    @Test
    public void testSingleTarget() throws RepositoryException, InstantiationException, IllegalAccessException {
        // Create annotation
        Annotation annotation = anno4j.createObject(Annotation.class);

        // Create specific resource
        SpecificResource specificResource = anno4j.createObject(SpecificResource.class);
        ResourceObject resourceObject = anno4j.createObject(ResourceObject.class);
        resourceObject.setResourceAsString("http://www.somepage.org/resource1/");
        specificResource.setSource(resourceObject);
        annotation.addTarget(specificResource);

        // Query annotation
        Annotation result = anno4j.findByID(Annotation.class, annotation.getResourceAsString());

        // Tests
        assertEquals(1, result.getTargets().size());
        assertEquals(("http://www.somepage.org/resource1/"), ((SpecificResource) result.getTargets().toArray()[0]).getSource().getResource().toString());
    }

    @Test
    public void testMultipleTargets() throws RepositoryException, InstantiationException, IllegalAccessException {
        // Create annotation
        Annotation annotation = anno4j.createObject(Annotation.class);

        // Create specific resource1
        SpecificResource specificResource = anno4j.createObject(SpecificResource.class);
        ResourceObject resourceObject = anno4j.createObject(ResourceObject.class);
        resourceObject.setResourceAsString("http://www.somepage.org/resource1/");
        specificResource.setSource(resourceObject);
        annotation.addTarget(specificResource);

        // Create specific resource2
        SpecificResource specificResource2 = anno4j.createObject(SpecificResource.class);
        ResourceObject resourceObject2 = anno4j.createObject(ResourceObject.class);
        resourceObject2.setResourceAsString("http://www.somepage.org/resource2/");
        specificResource2.setSource(resourceObject2);
        annotation.addTarget(specificResource2);

        // Query annotation
        Annotation result = anno4j.findByID(Annotation.class, annotation.getResourceAsString());

        // Tests
        List<String> urls = new ArrayList<>();
        for(Target target : result.getTargets()) {
            urls.add(((SpecificResource) target).getSource().getResource().toString());
        }

        assertTrue(urls.contains("http://www.somepage.org/resource1/"));
        assertTrue(urls.contains("http://www.somepage.org/resource2/"));
        assertEquals(2, result.getTargets().size());
    }

    @Test
    public void testSerializedAtAndAnnotatedAt() throws RepositoryException, IllegalAccessException, InstantiationException {
        int year = 2015;
        int month = 12;
        int day = 16;
        int hours = 12;
        int minutes = 0;
        int seconds = 0;
        String timezone = "UTC";

        int hours2 = 0;
        int minutes2 = 5;
        int seconds2 = 16;

        Annotation annotation = anno4j.createObject(Annotation.class);
        annotation.setGenerated(year, month, day, hours, minutes, seconds, timezone);
        annotation.setCreated(year, month, day, hours2, minutes2, seconds2, timezone);

        // Query annotation
        Annotation result = anno4j.findByID(Annotation.class, annotation.getResourceAsString());

        assertEquals("2015-12-16T12:00:00Z", result.getGenerated());
        assertEquals("2015-12-16T00:05:16Z", result.getCreated());
    }

    @Test
    public void testMotivation() throws RepositoryException, IllegalAccessException, InstantiationException {
        Annotation annotation = anno4j.createObject(Annotation.class);

        Motivation comment = MotivationFactory.getCommenting(this.anno4j);
        Motivation bookmark = MotivationFactory.getBookmarking(this.anno4j);

        Annotation result = anno4j.findByID(Annotation.class, annotation.getResourceAsString());

        assertEquals(0, result.getMotivatedBy().size());

        annotation.addMotivation(comment);

        result = anno4j.findByID(Annotation.class, annotation.getResourceAsString());

        assertEquals(1, result.getMotivatedBy().size());

        HashSet<Motivation> motivations = new HashSet<Motivation>();
        motivations.add(comment);
        motivations.add(bookmark);

        annotation.setMotivatedBy(motivations);

        result = anno4j.findByID(Annotation.class, annotation.getResourceAsString());

        assertEquals(2, result.getMotivatedBy().size());
    }

    @Test
    public void testBodyText() throws RepositoryException, IllegalAccessException, InstantiationException {
        Annotation annotation = this.anno4j.createObject(Annotation.class);

        annotation.addBodyText("test1");

        Annotation result = anno4j.findByID(Annotation.class, annotation.getResourceAsString());

        assertTrue(result.getBodyTexts().contains("test1"));

        HashSet<String> set = new HashSet<String>();
        set.add("test2");
        set.add("test3");

        annotation.setBodyTexts(set);

        result = anno4j.findByID(Annotation.class, annotation.getResourceAsString());

        assertEquals(2, result.getBodyTexts().size());
        assertTrue(result.getBodyTexts().contains("test2"));
        assertTrue(result.getBodyTexts().contains("test3"));
    }

    @Test
    public void testAnnotationWithCreation() throws RepositoryException, IllegalAccessException, InstantiationException {
        Annotation anno = this.anno4j.createObject(Annotation.class);
        anno.setCreated("2015-01-28T12:00:00Z");

        SpecificResource target = this.anno4j.createObject(SpecificResource.class);
        target.setCreated("2015-01-28T12:00:00+01:00");
        anno.addTarget(target);

        Annotation result = anno4j.findByID(Annotation.class, anno.getResourceAsString());

        assertEquals(anno.getCreated(), result.getCreated());
        assertEquals(((SpecificResource) anno.getTargets().toArray()[0]).getCreated(), ((SpecificResource) result.getTargets().toArray()[0]).getCreated());
    }

    @Test
    public void testAudiences() throws RepositoryException, IllegalAccessException, InstantiationException, ParseException, MalformedQueryException, QueryEvaluationException {
        Annotation annotation = this.anno4j.createObject(Annotation.class);

        Annotation result = anno4j.findByID(Annotation.class, annotation.getResourceAsString());

        assertEquals(0, result.getAudiences().size());

        TestAudience audience = this.anno4j.createObject(TestAudience.class);
        annotation.addAudience(audience);

        QueryService qs = this.anno4j.createQueryService();
        qs.addPrefix("schema", "https://schema.org/");
        qs.addCriteria("schema:audience[is-a schema:TestAudience]");

        List<Annotation> results = qs.execute(Annotation.class);
        result = results.get(0);

        assertEquals(1, result.getAudiences().size());

        HashSet<Audience> audiences = new HashSet<>();
        audiences.add(this.anno4j.createObject(TestAudience.class));
        audiences.add(this.anno4j.createObject(TestAudience.class));
        annotation.setAudiences(audiences);

        result = anno4j.findByID(Annotation.class, annotation.getResourceAsString());

        assertEquals(2, result.getAudiences().size());
    }

    @Iri("https://schema.org/TestAudience")
    public interface TestAudience extends Audience {

    }

    @Test
    public void testStyle() throws RepositoryException, IllegalAccessException, InstantiationException, ParseException, MalformedQueryException, QueryEvaluationException {
        Annotation annotation = this.anno4j.createObject(Annotation.class);

        CssStylesheet sheet = this.anno4j.createObject(CssStylesheet.class);
        annotation.setStyledBy(sheet);

        QueryService qs = this.anno4j.createQueryService();
        qs.addCriteria("oa:styledBy[is-a oa:CssStyle]");

        List<Annotation> result = qs.execute(Annotation.class);

        assertEquals(1, result.size());
    }
}