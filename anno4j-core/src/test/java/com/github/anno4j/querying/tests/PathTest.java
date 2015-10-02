package com.github.anno4j.querying.tests;


import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Body;
import com.github.anno4j.querying.QueryService;
import com.google.gson.Gson;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.annotations.Iri;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Containing all tests with simple path expressions.
 */
public class PathTest {

    private QueryService queryService = null;

    @Before
    public void resetQueryService() {
        queryService = Anno4j.getInstance().createQueryService();
        queryService.addPrefix("ex", "http://www.example.com/schema#");
    }

    @BeforeClass
    public static void setUp() throws RepositoryException {
        // Persisting some data
        Annotation annotation = new Annotation();
        annotation.setSerializedAt("07.05.2015");
        annotation.setBody(new PathTestBody("Value1"));
        Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation);

        Annotation annotation1 = new Annotation();
        annotation1.setAnnotatedAt("01.01.2011");
        annotation1.setBody(new PathTestBody("Value2"));
        Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation1);
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
    public static class PathTestBody extends Body {

        @Iri("http://www.example.com/schema#value")
        private String value;

        public PathTestBody() {
        }

        public PathTestBody(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }
}
