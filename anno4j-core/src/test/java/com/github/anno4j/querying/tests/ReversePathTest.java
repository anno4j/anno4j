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

public class ReversePathTest {

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
        annotation.setBody(new InverseBody("Some Testing Value"));
        Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation);

        Annotation annotation1 = new Annotation();
        annotation1.setAnnotatedAt("01.01.2011");
        annotation1.setBody(new InverseBody("Another Testing Value"));
        Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation1);
    }

    @Test
    public void testFirstBody() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List<Annotation> annotations = queryService
                .setAnnotationCriteria("oa:hasBody[is-a ex:inverseBody]/^oa:hasBody/oa:serializedAt", "07.05.2015")
                .execute();

        assertEquals(1, annotations.size());

        Annotation annotation = annotations.get(0);
        assertEquals("07.05.2015", annotation.getSerializedAt());

        // Testing if the body was persisted correctly
        InverseBody testBody = (InverseBody) annotation.getBody();
        assertEquals("Some Testing Value", testBody.getValue());
    }

    @Test
    public void testSecondBody() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List<Annotation> annotations = queryService
                .setAnnotationCriteria("oa:hasBody[is-a ex:inverseBody]/^oa:hasBody/oa:annotatedAt", "01.01.2011")
                .execute();

        assertEquals(1, annotations.size());

        Annotation annotation = annotations.get(0);
        assertEquals("01.01.2011", annotation.getAnnotatedAt());

        // Testing if the body was persisted correctly
        InverseBody testBody = (InverseBody) annotation.getBody();
        assertEquals("Another Testing Value", testBody.getValue());
    }

    @Test
    public void falseTest() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List<Annotation> annotations = queryService
                .setAnnotationCriteria("oa:hasBody[is-a ex:inverseBody]/^oa:hasBody/oa:serzializedAt", "01.01.2011")
                .execute();

        assertEquals(0, annotations.size());
    }


    @Iri("http://www.example.com/schema#inverseBody")
    public static class InverseBody extends Body {

        @Iri("http://www.example.com/schema#value")
        private String value;

        public InverseBody() {
        }

        public InverseBody(String value) {
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
