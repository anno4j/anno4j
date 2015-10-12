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
 * Containing all tests, that do not provide a constraint value
 * when defining a criteria.
 */
public class ConstraintLessTest {

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
        annotation.setBody(new ConstraintLessBody("Value 1"));
        Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation);

        Annotation annotation1 = new Annotation();
        annotation1.setAnnotatedAt("01.01.2011");
        annotation1.setBody(new ConstraintLessBody("Value 2"));
        Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation1);

        // This
        Annotation annotation2 = new Annotation();
        annotation2.setAnnotatedAt("01.01.2011");
        annotation2.setBody(new ConstraintLessBody());
        Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation2);
    }

    @Test
    /**
     * Querying for all annotation objects, where the containing body has a specific attribute set.
     */
    public void retrieveAll() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List<Annotation> list = queryService
                .setBodyCriteria("/ex:constraintLessValue")
                .execute();

        assertEquals(2, list.size());

        // Test for annotation specific attributes
        assertEquals("07.05.2015", list.get(0).getSerializedAt());
        assertEquals("01.01.2011", list.get(1).getAnnotatedAt());

        // Test for the value attribute of the body object
        assertEquals("Value 1", ((ConstraintLessBody) list.get(0).getBody()).getValue());
        assertEquals("Value 2", ((ConstraintLessBody) list.get(1).getBody()).getValue());
    }

    @Test
    /**
     * Trying to query for an object that was not persisted in the first place.
     */
    public void falseTest() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List<Annotation> list = queryService
                .setBodyCriteria("ex:nonExistingVaue")
                .execute();

        assertEquals(0, list.size());
    }


    @Iri("http://www.example.com/schema#constraintLessBody")
    public static class ConstraintLessBody extends Body {

        @Iri("http://www.example.com/schema#constraintLessValue")
        private String value;

        public ConstraintLessBody() {
        }

        public ConstraintLessBody(String value) {
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
