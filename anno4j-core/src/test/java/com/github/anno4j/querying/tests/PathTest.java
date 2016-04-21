package com.github.anno4j.querying.tests;


import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Body;
import com.github.anno4j.querying.QuerySetup;
import org.apache.marmotta.ldpath.parser.ParseException;
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
public class PathTest extends QuerySetup {

    @Test
    public void testFirstBody() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List<Annotation> annotations = queryService
                .addCriteria("oa:hasBody/ex:value", "Value1")
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
                .addCriteria("oa:hasBody/ex:value", "Value2")
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
                .addCriteria("oa:hasBody/ex:value", "Value3")
                .execute();

        assertEquals(0, annotations.size());
    }

    @Override
    public void persistTestData() throws RepositoryException, InstantiationException, IllegalAccessException {
        // Persisting some data
        Annotation annotation = anno4j.createObject(Annotation.class);
        annotation.setSerializedAt("07.05.2015");
        PathTestBody pathTestBody = anno4j.createObject(PathTestBody.class);
        pathTestBody.setValue("Value1");
        annotation.setBody(pathTestBody);

        Annotation annotation1 = anno4j.createObject(Annotation.class);
        annotation1.setAnnotatedAt("01.01.2011");
        PathTestBody pathTestBody2 = anno4j.createObject(PathTestBody.class);
        pathTestBody2.setValue("Value2");
        annotation1.setBody(pathTestBody2);
    }

    @Iri("http://www.example.com/schema#pathBody")
    public static interface PathTestBody extends Body {
        @Iri("http://www.example.com/schema#value")
        String getValue();

        @Iri("http://www.example.com/schema#value")
        void setValue(String value);
    }
}
