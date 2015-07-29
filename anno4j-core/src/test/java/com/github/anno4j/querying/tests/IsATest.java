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
 * Containing all tests, that do query for the rdf:type.
 */
public class IsATest {

    private QueryService<Annotation> queryService = null;

    @Before
    public void resetQueryService() {
        queryService = Anno4j.getInstance().createQueryService(Annotation.class);
        queryService.addPrefix("ex", "http://www.example.com/schema#");
    }

    @BeforeClass
    public static void setUp() throws RepositoryException {
        // Persisting some data
        Annotation annotation = new Annotation();
        annotation.setBody(new FirstTestBody("First Value"));
        Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation);

        Annotation annotation1 = new Annotation();
        annotation1.setBody(new SecondTestBody("Second Value"));
        Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation1);
    }

    @Test
    /**
     * Querying for the annotation, that contains the FirstTestBody
     */
    public void firstBodyTest() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List<Annotation> list = queryService
                .setBodyCriteria("[is-a ex:firstBodyType]")
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
                .setBodyCriteria("[is-a ex:secondBodyType]")
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
                .setBodyCriteria("[is-a ex:thirdBodyType]")
                .execute();

        assertEquals(0, list.size());
    }

    @Iri("http://www.example.com/schema#firstBodyType")
    public static class FirstTestBody extends Body {

        @Iri("http://www.example.com/schema#firstValue")
        private String value;

        public FirstTestBody() {
        }

        public FirstTestBody(String value) {
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

    @Iri("http://www.example.com/schema#secondBodyType")
    public static class SecondTestBody extends Body {

        @Iri("http://www.example.com/schema#secondValue")
        private String value;

        public SecondTestBody() {
        }

        public SecondTestBody(String value) {
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
