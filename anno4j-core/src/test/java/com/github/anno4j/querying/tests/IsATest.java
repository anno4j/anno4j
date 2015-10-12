package com.github.anno4j.querying.tests;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Body;
import com.github.anno4j.querying.QueryService;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.annotations.Iri;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Containing all tests, that do query for the rdf:type.
 */
public class IsATest {

    private QueryService queryService = null;
    private Anno4j anno4j;

    @Before
    public void resetQueryService() throws RepositoryConfigException, RepositoryException, InstantiationException, IllegalAccessException {
        this.anno4j = new Anno4j();
        queryService = anno4j.createQueryService();
        queryService.addPrefix("ex", "http://www.example.com/schema#");

        // Persisting some data
        Annotation annotation = anno4j.createObject(Annotation.class);
        FirstTestBody firstTestBody = anno4j.createObject(FirstTestBody.class);
        firstTestBody.setValue("First Value");
        annotation.setBody(firstTestBody);
        anno4j.createPersistenceService().persistAnnotation(annotation);

        Annotation annotation1 = anno4j.createObject(Annotation.class);
        SecondTestBody secondTestBody = anno4j.createObject(SecondTestBody.class);
        secondTestBody.setValue("Second Value");
        annotation1.setBody(secondTestBody);
        anno4j.createPersistenceService().persistAnnotation(annotation1);
    }

    @Test
    /**
     * Querying for the annotation, that contains the FirstTestBody
     */
    public void firstBodyTest() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List<Annotation> list = queryService
                .addCriteria("oa:hasBody[is-a ex:firstBodyType]")
                .execute();

        assertEquals(1, list.size());
        assertEquals("First Value", ((FirstTestBody) list.get(0).getBody()).getValue());
    }

    @Test
    /**
     * Querying for the annotation, that contains the SecondTestBody
     */
    public void secondBodyTest() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List<Annotation> list = queryService
                .addCriteria("oa:hasBody[is-a ex:secondBodyType]")
                .execute();

        assertEquals(1, list.size());
        assertEquals("Second Value", ((SecondTestBody) list.get(0).getBody()).getValue());
    }

    @Test
    /**
     * Querying for the annotation, that contains a body with the rdf:type ex:thirdBodyType. This test should
     * return an empty annotation list, because no such object was persisted before.
     */
    public void falseTest() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {

        List<Annotation> list = queryService
                .addCriteria("oa:hasBody[is-a ex:thirdBodyType]")
                .execute();

        assertEquals(0, list.size());
    }

    @Iri("http://www.example.com/schema#firstBodyType")
    public static interface FirstTestBody extends Body {
        @Iri("http://www.example.com/schema#firstValue")
        String getValue();

        @Iri("http://www.example.com/schema#firstValue")
        void setValue(String value);
    }

    @Iri("http://www.example.com/schema#secondBodyType")
    public static interface SecondTestBody extends Body {
        @Iri("http://www.example.com/schema#secondValue")
        String getValue();

        @Iri("http://www.example.com/schema#secondValue")
        void setValue(String value);

    }
}
