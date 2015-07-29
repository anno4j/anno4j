package com.github.anno4j.querying;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Body;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.annotations.Iri;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests the insertion and querying in sub-graph context
 */
public class GraphContextQueryTest {

    URI subgraph = new URIImpl("http://www.example.com/TESTGRAPH");

    @Before
    public void setUp() throws Exception {
        SailRepository repository = new SailRepository(new MemoryStore());
        repository.initialize();
        Anno4j.getInstance().setRepository(repository);
    }

    @Test
     public void persistInSubGraphQueryDefaultGraphTest() throws Exception {
        // Create test annotation
        Annotation annotation = new Annotation();
        TestBody body = new TestBody();
        body.setValue("Example Value");
        annotation.setBody(body);

        // persist annotation
        Anno4j.getInstance().createPersistenceService(subgraph).persistAnnotation(annotation);

        // Querying for the persisted annotation
        QueryService<Annotation> queryService = Anno4j.getInstance().createQueryService(Annotation.class);
        List<Annotation> defaultList = queryService
                .addPrefix("ex", "http://www.example.com/schema#")
                .setBodyCriteria("ex:value", "Example Value")
                .execute();

        assertEquals(1, defaultList.size());

        // Testing if the body was persisted correctly
        Annotation annotationResult = defaultList.get(0);
        TestBody testBody = (TestBody) annotationResult.getBody();
        assertEquals("Example Value", testBody.getValue());
    }

    @Test
    public void persistInSubGraphQuerySubGraphTest() throws Exception {
        // Create test annotation
        Annotation annotation = new Annotation();
        TestBody body = new TestBody();
        body.setValue("Example Value");
        annotation.setBody(body);

        // persist annotation
        Anno4j.getInstance().createPersistenceService(subgraph).persistAnnotation(annotation);

        // Querying for the persisted annotation
        QueryService<Annotation> queryService = Anno4j.getInstance().createQueryService(Annotation.class, subgraph);
        List<Annotation> defaultList = queryService
                .addPrefix("ex", "http://www.example.com/schema#")
                .setBodyCriteria("ex:value", "Example Value")
                .execute();

        assertEquals(1, defaultList.size());

        // Testing if the body was persisted correctly
        Annotation annotationResult = defaultList.get(0);
        TestBody testBody = (TestBody) annotationResult.getBody();
        assertEquals("Example Value", testBody.getValue());
    }

    @Test
    public void persistInDefaultGraphQuerySubGraphTest() throws Exception {
        // Create test annotation
        Annotation annotation = new Annotation();
        TestBody body = new TestBody();
        body.setValue("Example Value");
        annotation.setBody(body);

        // persist annotation
        Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation);

        // Querying for the persisted annotation
        QueryService<Annotation> queryService = Anno4j.getInstance().createQueryService(Annotation.class, subgraph);
        List<Annotation> defaultList = queryService
                .addPrefix("ex", "http://www.example.com/schema#")
                .setBodyCriteria("ex:value", "Example Value")
                .execute();

        assertEquals(0, defaultList.size());
    }

    @Iri("http://www.example.com/schema#GraphTestBody")
    public static class TestBody extends Body {

        @Iri("http://www.example.com/schema#value")
        String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
