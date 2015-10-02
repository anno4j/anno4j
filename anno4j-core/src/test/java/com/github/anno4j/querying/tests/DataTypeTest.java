package com.github.anno4j.querying.tests;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Body;
import com.github.anno4j.querying.Comparison;
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
 * Containing all tests, that do query for specific data types.
 */
public class DataTypeTest {

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
        annotation.setBody(new DataTypeBody(2.0));
        Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation);

        Annotation annotation1 = new Annotation();
        annotation1.setBody(new DataTypeBody("3.0"));
        Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation1);
    }

    @Test
    /**
     * Querying for all annotations, that has bodies with xsd:double values set, using eq comparison.
     */
    public void doubleEqualTest() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List<Annotation> list = queryService
                .addPrefix("ex", "http://www.example.com/schema#")
                .setBodyCriteria("ex:doubleValue[^^xsd:double]")
                .execute();

        assertEquals(1, list.size());
        assertEquals(new Double(2.0), ((DataTypeBody) list.get(0).getBody()).getDoubleValue());
    }

    @Test
    /**
     *  Querying for all annotations, that has bodies with a xsd:double values < 4.0 set.
     */
    public void doubleLtTest() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List<Annotation> list = queryService
                .addPrefix("ex", "http://www.example.com/schema#")
                .setBodyCriteria("ex:doubleValue[^^xsd:double]", 4.0, Comparison.LT)
                .execute();

        assertEquals(1, list.size());
        assertEquals(new Double(2.0), ((DataTypeBody) list.get(0).getBody()).getDoubleValue());
    }

    @Test
    /**
     *  Querying for all annotations, that has bodies with xsd:double values > 4.0 set. This should not return any result
     *  because no such object was persisted before.
     */
    public void falseGtTest() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List<Annotation> list = queryService
                .addPrefix("ex", "http://www.example.com/schema#")
                .setBodyCriteria("ex:doubleValue[^^xsd:double]", 4.0, Comparison.GT)
                .execute();

        assertEquals(0, list.size());
    }

    @Test
    /**
     *  Querying for all annotations, that has bodies with xsd:string values set.
     */
    public void stringTest() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {

        List<Annotation> list = queryService
                .addPrefix("ex", "http://www.example.com/schema#")
                .setBodyCriteria("ex:stringValue[^^xsd:string]")
                .execute();

        assertEquals(1, list.size());
        assertEquals("3.0", ((DataTypeBody) list.get(0).getBody()).getStringValue());
        assertEquals(null, ((DataTypeBody) list.get(0).getBody()).getDoubleValue());
    }

    @Iri("http://www.example.com/schema#datatTypeBody")
    public static class DataTypeBody extends Body {

        @Iri("http://www.example.com/schema#doubleValue")
        private Double doubleValue;

        @Iri("http://www.example.com/schema#stringValue")
        private String stringValue;

        public DataTypeBody() {
        }

        public DataTypeBody(Double value) {
            this.doubleValue = value;
        }

        public DataTypeBody(String value) {
            this.stringValue = value;
        }

        public Double getDoubleValue() {
            return doubleValue;
        }

        public void setDoubleValue(double doubleValue) {
            this.doubleValue = doubleValue;
        }

        public void setDoubleValue(Double doubleValue) {
            this.doubleValue = doubleValue;
        }

        public String getStringValue() {
            return stringValue;
        }

        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }

}
