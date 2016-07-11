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
import org.openrdf.repository.config.RepositoryConfigException;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *  Containing all tests with recursive path expressions.
 */
public class RecursivePathTest extends QuerySetup {

    @Test
    /**
     * Test method for OneOrMorePath
     *
     * @see <a href="http://www.w3.org/TR/sparql11-query/#pp-language">http://www.w3.org/TR/sparql11-query/#pp-language</a>
     */
    public void oneOrMoreTest() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException, RepositoryConfigException, IllegalAccessException, InstantiationException {
        List<Annotation> annotations = queryService
                .addCriteria("(oa:hasTarget)+")
                .execute();
        assertEquals(0, annotations.size());

        super.setupUpQueryTest();

        annotations = queryService
                .addCriteria("(oa:hasBody)+")
                .addCriteria("oa:hasBody/ex:recursiveBodyValue", "Another Testing Value")
                .execute();
        assertEquals(1, annotations.size());
        assertEquals("Another Testing Value", ((RecursiveBody) annotations.get(0).getBody()).getValue());
    }

    @Test
    /**
     * Test method for ZeroOrMorePath.
     *
     * @see <a href="http://www.w3.org/TR/sparql11-query/#pp-language">http://www.w3.org/TR/sparql11-query/#pp-language</a>
     */
    public void zeroOrMoreTest() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException, RepositoryConfigException, IllegalAccessException, InstantiationException {
        List<Annotation> annotations = queryService.addCriteria("(oa:hasBody/ex:recursiveBodyValue)*", "Some Testing Value").execute();
        assertEquals(1, annotations.size());
        assertEquals("Some Testing Value", ((RecursiveBody) annotations.get(0).getBody()).getValue());

        super.setupUpQueryTest();

        annotations = queryService.addCriteria("(oa:hasTarget)*").execute();
        assertEquals(2, annotations.size());
    }

    @Override
    public void persistTestData() throws RepositoryException, InstantiationException, IllegalAccessException {
        // Persisting some data
        Annotation annotation = anno4j.createObject(Annotation.class);
        annotation.setGenerated("2015-01-28T12:00:00Z");
        RecursiveBody recursiveBody = anno4j.createObject(RecursiveBody.class);
        recursiveBody.setValue("Some Testing Value");
        annotation.setBody(recursiveBody);

        Annotation annotation1 = anno4j.createObject(Annotation.class);
        annotation1.setCreated("2015-01-28T12:00:00Z");
        RecursiveBody recursiveBody2 = anno4j.createObject(RecursiveBody.class);
        recursiveBody2.setValue("Another Testing Value");
        annotation1.setBody(recursiveBody2);

    }

    @Iri("http://www.example.com/schema#recursiveBody")
    public static interface RecursiveBody extends Body {
        @Iri("http://www.example.com/schema#recursiveBodyValue")
        String getValue();

        @Iri("http://www.example.com/schema#recursiveBodyValue")
        void setValue(String value);
    }
}
