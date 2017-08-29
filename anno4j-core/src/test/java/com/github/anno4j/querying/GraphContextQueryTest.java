package com.github.anno4j.querying;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Body;
import com.github.anno4j.model.impl.body.TextualBody;
import com.github.anno4j.model.impl.selector.TextPositionSelector;
import com.github.anno4j.model.impl.targets.SpecificResource;
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
        Annotation annotation = anno4j.createObject(Annotation.class, subgraph);
        TestBody body =  anno4j.createObject(TestBody.class, subgraph);
        body.setValue("Example Value");
        annotation.addBody(body);

        // Querying for the persisted annotation
        QueryService queryService = anno4j.createQueryService();
        List<Annotation> defaultList = queryService
                .addPrefix("ex", "http://www.example.com/schema#")
                .addCriteria("oa:hasBody/ex:value", "Example Value")
                .execute();
        assertEquals(1, defaultList.size());

        // Testing if the body was persisted correctly
        Annotation annotationResult = defaultList.get(0);
        TestBody testBody = (TestBody) annotationResult.getBodies().iterator().next();
        assertEquals("Example Value", testBody.getValue());
    }

    @Test
    public void persistInSubGraphQuerySubGraphTest() throws Exception {
        // Create test annotation
        Annotation annotation = anno4j.createObject(Annotation.class, subgraph);
        TestBody body =  anno4j.createObject(TestBody.class, subgraph);
        body.setValue("Example Value");
        annotation.addBody(body);

        // Querying for the persisted annotation
        QueryService queryService = anno4j.createQueryService(subgraph);
        List<Annotation> defaultList = queryService
                .addPrefix("ex", "http://www.example.com/schema#")
                .addCriteria("oa:hasBody/ex:value", "Example Value")
                .execute();

        assertEquals(1, defaultList.size());

        // Testing if the body was persisted correctly
        Annotation annotationResult = defaultList.get(0);
        TestBody testBody = (TestBody) annotationResult.getBodies().iterator().next();
        assertEquals("Example Value", testBody.getValue());
    }

    @Test
    public void persistInDefaultGraphQuerySubGraphTest() throws Exception {
        // Create test annotation
        Annotation annotation = anno4j.createObject(Annotation.class);
        TestBody body = anno4j.createObject(TestBody.class);
        body.setValue("Example Value");
        annotation.addBody(body);

        // Querying for the persisted annotation
        QueryService queryService = anno4j.createQueryService(subgraph);
        List<Annotation> defaultList = queryService
                .addPrefix("ex", "http://www.example.com/schema#")
                .addCriteria("oa:hasBody/ex:value", "Example Value")
                .execute();

        assertEquals(0, defaultList.size());
    }


    @Test
    public void persisteDefaultGraphFindAll() throws Exception {
        // Create test annotation
        Annotation annotation = anno4j.createObject(Annotation.class);
        TestBody body =  anno4j.createObject(TestBody.class);
        body.setValue("Example Value");
        annotation.addBody(body);

        // Querying for the persisted annotation
        QueryService queryService = anno4j.createQueryService();
        List<Annotation> subGraphList = anno4j.findAll(Annotation.class, subgraph);
        assertEquals(0, subGraphList.size());
        List<Annotation> defaultGraphList = anno4j.findAll(Annotation.class);
        assertEquals(1, defaultGraphList.size());
    }

    @Test
    public void persistSubGraphFindAll() throws Exception {
        // Create test annotation
        Annotation annotation = anno4j.createObject(Annotation.class, subgraph);
        TestBody body =  anno4j.createObject(TestBody.class, subgraph);
        body.setValue("Example Value");
        annotation.addBody(body);

        // Querying for the persisted annotation
        QueryService queryService = anno4j.createQueryService();
        List<Annotation> subGraphList = anno4j.findAll(Annotation.class, subgraph);
        assertEquals(1, subGraphList.size());
        List<Annotation> defaultGraphList = anno4j.findAll(Annotation.class);
        assertEquals(1, defaultGraphList.size());
    }

    @Test
    public void persistDefaultAndSubGraphFindAll() throws Exception {
        // Create test annotation
        Annotation annotationDefault = anno4j.createObject(Annotation.class);
        TestBody body =  anno4j.createObject(TestBody.class);
        body.setValue("Example Value");
        annotationDefault.addBody(body);

        // Create test annotation
        Annotation annotationSubgraph = anno4j.createObject(Annotation.class, subgraph);
        TestBody body2 =  anno4j.createObject(TestBody.class, subgraph);
        body.setValue("Example Value");
        annotationSubgraph.addBody(body2);

        // Querying for the persisted annotation
        QueryService queryService = anno4j.createQueryService();
        List<Annotation> subGraphList = anno4j.findAll(Annotation.class, subgraph);
        assertEquals(1, subGraphList.size());
        List<Annotation> defaultGraphList = anno4j.findAll(Annotation.class);
        assertEquals(2, defaultGraphList.size());
    }

    @Iri("http://www.example.com/schema#GraphTestBody")
    public static interface TestBody extends Body {
        @Iri("http://www.example.com/schema#value")
        String getValue();

        @Iri("http://www.example.com/schema#value")
        void setValue(String value);
    }

    @Test
    public void testContextedBodySetResourceAsString() throws Exception {
        URI uri = new URIImpl("http://www.example.com/context#test");

        String resource = "http://www.example.com/resource#someResource";
        String resource2 = "http://www.example.com/resource#someOtherResource";

        Annotation annotation = anno4j.createObject(Annotation.class,uri);
        TextualBody body = anno4j.createObject(TextualBody.class,uri);
        annotation.addBody(body);
        body.setResourceAsString(resource);

        Annotation annotationDef = anno4j.createObject(Annotation.class,uri);
        TextualBody bodyDef = anno4j.createObject(TextualBody.class);
        annotationDef.addBody(bodyDef);
        bodyDef.setResourceAsString(resource2);

        List<TextualBody> result = anno4j.findAll(TextualBody.class, uri);
        assertEquals(1, result.size());
        assertEquals(resource, result.get(0).getResourceAsString());

        TextualBody result2 = anno4j.findByID(TextualBody.class, resource2);
        assertEquals(resource2, result2.getResourceAsString());
    }
}
