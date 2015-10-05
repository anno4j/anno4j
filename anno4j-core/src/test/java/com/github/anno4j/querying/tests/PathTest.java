package com.github.anno4j.querying.tests;


import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Body;
import com.github.anno4j.querying.QueryService;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.annotations.Iri;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Containing all tests with simple path expressions.
 */
public class PathTest {

    private QueryService queryService = null;
    private Anno4j anno4j;

    @Before
    public void resetQueryService() throws RepositoryConfigException, RepositoryException {
        this.anno4j = new Anno4j();
        queryService = anno4j.createQueryService();
        queryService.addPrefix("ex", "http://www.example.com/schema#");
    }

    @BeforeClass
    public void setUp() throws RepositoryException, InstantiationException, IllegalAccessException {
        // Persisting some data
        Annotation annotation = anno4j.createObject(Annotation.class);
        annotation.setSerializedAt("07.05.2015");
        PathTestBody pathTestBody = anno4j.createObject(PathTestBody.class);
        pathTestBody.setValue("Value1");
        annotation.setBody(pathTestBody);
        anno4j.createPersistenceService().persistAnnotation(annotation);

        Annotation annotation1 = anno4j.createObject(Annotation.class);
        annotation1.setAnnotatedAt("01.01.2011");
        PathTestBody pathTestBody2 = anno4j.createObject(PathTestBody.class);
        pathTestBody2.setValue("Value2");
        annotation1.setBody(pathTestBody2);
        anno4j.createPersistenceService().persistAnnotation(annotation1);
    }

    @Test
    public void testFirstBody() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List<Annotation> annotations = queryService
                .setAnnotationCriteria("oa:hasBody/ex:value", "Value1")
                .execute();

        assertEquals(1, annotations.size());

        // Testing against the serialization date
        Annotation annotation = annotations.get(0);
        assertEquals("07.05.2015", annotation.getSerializedAt());

        // Testing if the body was persisted correctly
        PathTestBody testBody = (PathTestBody) annotation.getBody();
        assertEquals("Value1", testBody.getValue());
    }

    @Test
    public void testSecondBody() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List<Annotation> annotations = queryService
                .setBodyCriteria("ex:value", "Value2")
                .execute();

        assertEquals(1, annotations.size());

        // Testing against the serialization date
        Annotation annotation = annotations.get(0);
        assertEquals("01.01.2011", annotation.getAnnotatedAt());

        // Testing if the body was persisted correctly
        PathTestBody testBody = (PathTestBody) annotation.getBody();
        assertEquals("Value2", testBody.getValue());
    }

    @Test
    public void falseTest() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List<Annotation> annotations = queryService
                .setBodyCriteria("ex:value", "Value3")
                .execute();

        assertEquals(0, annotations.size());
    }

    @Iri("http://www.example.com/schema#pathBody")
    public static interface PathTestBody extends Body {
        @Iri("http://www.example.com/schema#value")
        String getValue();

        @Iri("http://www.example.com/schema#value")
        void setValue(String value);
    }
}
