package com.github.anno4j.querying.tests;

import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Body;
import com.github.anno4j.querying.Comparison;
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
 * Containing all tests, that do query for different comparison methods.
 */
public class ComparisonTest extends QuerySetup {


    @Test
    /**
     * Querying for all annotations, that has bodies where the string value starts with a certain value.
     */
    public void startWithTest() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List<Annotation> list = queryService
                .addPrefix("ex", "http://www.example.com/schema#")
                .addCriteria("oa:hasBody/ex:comparisonBodyStringValue", "Test", Comparison.STARTS_WITH)
                .execute();

        assertEquals(2, list.size());
    }


    @Test
    /**
     * Querying for all annotations, that has bodies where the string value ends with a certain value.
     */
    public void endsWithTest() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List<Annotation> list = queryService
                .addPrefix("ex", "http://www.example.com/schema#")
                .addCriteria("oa:hasBody/ex:comparisonBodyStringValue", "Test", Comparison.ENDS_WITH)
                .execute();
        assertEquals(2, list.size());
    }

    @Test
    /**
     * Querying for all annotations, that has bodies where the string value contains a certain value.
     */
    public void containsTest() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List<Annotation> list = queryService
                .addPrefix("ex", "http://www.example.com/schema#")
                .addCriteria("oa:hasBody/ex:comparisonBodyStringValue", "Test", Comparison.CONTAINS)
                .execute();

        assertEquals(3, list.size());
    }

    @Test
    /**
     * Querying for all annotations, that has bodies where the string value matches exact a certain value.
     */
    public void exactMatchTest() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List<Annotation> list = queryService
                .addPrefix("ex", "http://www.example.com/schema#")
                .addCriteria("oa:hasBody/ex:comparisonBodyStringValue", "Test", Comparison.EQ)
                .execute();

        assertEquals(1, list.size());
    }

    @Override
    public void persistTestData() throws RepositoryException, InstantiationException, IllegalAccessException {
        // Persisting some data
        Annotation annotation = anno4j.createObject(Annotation.class);
        ComparisonBody comparisonBody = anno4j.createObject(ComparisonBody.class);
        comparisonBody.setStringValue("Test");
        annotation.addBody(comparisonBody);

        Annotation annotation1 = anno4j.createObject(Annotation.class);
        ComparisonBody comparisonBody1 = anno4j.createObject(ComparisonBody.class);
        comparisonBody1.setStringValue("Test1");
        annotation1.addBody(comparisonBody1);

        Annotation annotation2 = anno4j.createObject(Annotation.class);
        ComparisonBody comparisonBody2 = anno4j.createObject(ComparisonBody.class);
        comparisonBody2.setStringValue("2Test");
        annotation2.addBody(comparisonBody2);
    }

    @Iri("http://www.example.com/schema#comparisonBody")
    public static interface ComparisonBody extends Body {

        @Iri("http://www.example.com/schema#comparisonBodyStringValue")
        String getStringValue();

        @Iri("http://www.example.com/schema#comparisonBodyStringValue")
        void setStringValue(String stringValue);
    }
}
