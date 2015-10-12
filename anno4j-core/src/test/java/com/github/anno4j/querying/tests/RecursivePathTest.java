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
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *  Containing all tests with recursive path expressions.
 */
public class RecursivePathTest {

    private QueryService<Annotation> queryService = null;

    @Before
    public void resetQueryService() throws RepositoryConfigException, RepositoryException {
        SailRepository repository = new SailRepository(new MemoryStore());
        repository.initialize();
        Anno4j.getInstance().setRepository(repository);

        queryService = Anno4j.getInstance().createQueryService(Annotation.class);
        queryService.addPrefix("ex", "http://www.example.com/schema#");

        // Persisting some data
        Annotation annotation = new Annotation();
        annotation.setSerializedAt("07.05.2015");
        annotation.setBody(new RecursiveBody("Some Testing Value"));
        Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation);

        Annotation annotation1 = new Annotation();
        annotation1.setAnnotatedAt("01.01.2011");
        annotation1.setBody(new RecursiveBody("Another Testing Value"));
        Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation1);
    }

    @Test
    /**
     * Test method for OneOrMorePath
     *
     * @see <a href="http://www.w3.org/TR/sparql11-query/#pp-language">http://www.w3.org/TR/sparql11-query/#pp-language</a>
     */
    public void oneOrMoreTest() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException, RepositoryConfigException {
        List<Annotation> annotations = queryService
                .setAnnotationCriteria("(oa:hasTarget)+")
                .execute();
        assertEquals(0, annotations.size());

        resetQueryService();

        annotations = queryService
                .setAnnotationCriteria("(oa:hasBody)+")
                .setBodyCriteria("/ex:recursiveBodyValue", "Another Testing Value")
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
    public void zeroOrMoreTest() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException, RepositoryConfigException {
        List<Annotation> annotations = queryService.setAnnotationCriteria("(oa:hasBody/ex:recursiveBodyValue)*", "Some Testing Value").execute();
        assertEquals(1, annotations.size());
        assertEquals("Some Testing Value", ((RecursiveBody) annotations.get(0).getBody()).getValue());

        resetQueryService();

        annotations = queryService.setAnnotationCriteria("(oa:hasTarget)*").execute();
        assertEquals(2, annotations.size());
    }

    @Iri("http://www.example.com/schema#recursiveBody")
    public static class RecursiveBody extends Body {

        @Iri("http://www.example.com/schema#recursiveBodyValue")
        private String value;

        public RecursiveBody() {
        }

        public RecursiveBody(String value) {
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
