package com.github.anno4j.querying.tests;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Body;
import com.github.anno4j.querying.QueryService;
import com.google.gson.Gson;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.annotations.Iri;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by schlegel on 09/10/15.
 */
public class UnionTest {

    private QueryService<Annotation> queryService = null;

    @Before
    public void resetQueryService() throws RepositoryException {
        queryService = Anno4j.getInstance().createQueryService(Annotation.class);
        queryService.addPrefix("ex", "http://www.example.com/schema#");
    }

    @Test
    public void testUnionBody() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        // Persisting some data
        Annotation annotation = new Annotation();
        annotation.setSerializedAt("07.05.2015");
        annotation.setBody(new UnionTestBody1("Value1"));
        Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation);

        Annotation annotation1 = new Annotation();
        annotation1.setAnnotatedAt("01.01.2011");
        annotation1.setBody(new UnionTestBody2("Value2"));
        Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation1);

        List<Annotation> annotations = queryService
                .setAnnotationCriteria("oa:hasBody[is-a ex:unionBody1] | oa:hasBody[is-a ex:unionBody2]")
                .execute();

        assertEquals(2, annotations.size());
    }

    @Iri("http://www.example.com/schema#unionBody1")
    public static class UnionTestBody1 extends Body {

        @Iri("http://www.example.com/schema#value")
        private String value;

        public UnionTestBody1() {
        }

        public UnionTestBody1(String value) {
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

    @Iri("http://www.example.com/schema#unionBody2")
    public static class UnionTestBody2 extends Body {

        @Iri("http://www.example.com/schema#value")
        private String value;

        public UnionTestBody2() {
        }

        public UnionTestBody2(String value) {
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
