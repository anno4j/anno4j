package com.github.anno4j.querying;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Body;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.annotations.Iri;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests the insertion and querying in sub-graph context
 */
public class GraphContextQueryTest {

    URI subgraph = new URIImpl("http://www.example.com/TESTGRAPH");
    private Anno4j anno4j;

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
    }

    @Test
     public void persistInSubGraphQueryDefaultGraphTest() throws Exception {
        // Create test annotation
        Annotation annotation = anno4j.createObject(Annotation.class);
        TestBody body =  anno4j.createObject(TestBody.class);
        body.setValue("Example Value");
        annotation.setBody(body);

        // persist annotation
        anno4j.persist(annotation,subgraph);

        // Querying for the persisted annotation
        QueryService queryService = anno4j.createQueryService();
        List<Annotation> defaultList = queryService
                .addPrefix("ex", "http://www.example.com/schema#")
                .addCriteria("oa:hasBody/ex:value", "Example Value")
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
        Annotation annotation = anno4j.createObject(Annotation.class);
        TestBody body =  anno4j.createObject(TestBody.class);
        body.setValue("Example Value");
        annotation.setBody(body);

        // persist annotation
        anno4j.persist(annotation,subgraph);

        // Querying for the persisted annotation
        QueryService queryService = anno4j.createQueryService(subgraph);
        List<Annotation> defaultList = queryService
                .addPrefix("ex", "http://www.example.com/schema#")
                .addCriteria("oa:hasBody/ex:value", "Example Value")
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
        Annotation annotation = anno4j.createObject(Annotation.class);
        TestBody body = anno4j.createObject(TestBody.class);
        body.setValue("Example Value");
        annotation.setBody(body);

        // persist annotation
        anno4j.persist(annotation);

        // Querying for the persisted annotation
        QueryService queryService = anno4j.createQueryService(subgraph);
        List<Annotation> defaultList = queryService
                .addPrefix("ex", "http://www.example.com/schema#")
                .addCriteria("oa:hasBody/ex:value", "Example Value")
                .execute();

        assertEquals(0, defaultList.size());
    }

    @Iri("http://www.example.com/schema#GraphTestBody")
    public static interface TestBody extends Body {
        @Iri("http://www.example.com/schema#value")
        String getValue();

        @Iri("http://www.example.com/schema#value")
        void setValue(String value);
    }
}
