package com.github.anno4j.querying;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.mock.TestBody;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.repository.object.LangString;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test class for the QueryService functionality
 */
public class QueryServiceTest {

    @Before
    /**
     * Refresh the backend before each unit test
     */
    public void setUp() throws Exception {
        SailRepository repository = new SailRepository(new MemoryStore());
        repository.initialize();
        Anno4j.getInstance().setRepository(repository);
    }

    /**
     * Testing simple LDPath path expression.
     */
    @Test
    public void simplePathTest() throws Exception {

        // Create test annotation
        TestBody body = new TestBody();
        body.setValue("Example Value");

        Annotation annotation = new Annotation();
        annotation.setSerializedAt("07.05.2015");
        annotation.setBody(body);

        // persist annotation
        Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation);

        // Querying for the persisted annotation
        QueryService<Annotation> queryService = Anno4j.getInstance().createQueryService(Annotation.class);
        List<Annotation> defaultList = queryService
                .addPrefix("ex", "http://www.example.com/schema#")
                .setBodyCriteria("ex:value", "Example Value")
                .execute();

        assertEquals(1, defaultList.size());

        // Testing against the serialization date
        Annotation annotationDefault = defaultList.get(0);
        assertEquals("07.05.2015", annotationDefault.getSerializedAt());

        // Testing if the body was persisted correctly
        TestBody testBody = (TestBody) annotationDefault.getBody();
        assertEquals("Example Value", testBody.getValue());

    }

    @Test
    /**
     * Testing the LDPath language testing functionality.
     */
    public void langTest() throws Exception {

        TestBody body = new TestBody();
        body.setLangValue(new LangString("Example Value", "en"));

        Annotation annotation = new Annotation();
        annotation.setBody(body);

        // persist annotation
        Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation);

        // Querying for the persisted annotation
        QueryService<Annotation> queryService = Anno4j.getInstance().createQueryService(Annotation.class);
        List<Annotation> defaultList = queryService
                .addPrefix("ex", "http://www.example.com/schema#")
                .setBodyCriteria("ex:langValue[@en]", "Example Value")
                .execute();

        // Testing if the body was persisted correctly
        TestBody testBody = (TestBody) defaultList.get(0).getBody();
        assertEquals("en", testBody.getLangValue().getLang());
        assertEquals("Example Value", testBody.getLangValue().toString());
    }

    @Test
    /**
     * Testing a query without setting a constraint for the used BodyCriteria.
     */
    public void constraintLessTest() throws Exception {
        // Create test annotation
        TestBody body = new TestBody();
        body.setValue("Example Value");

        Annotation annotation = new Annotation();
        annotation.setSerializedAt("07.05.2015");
        annotation.setBody(body);

        // persist annotation
        Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation);

        // Querying for the persisted annotation
        QueryService<Annotation> queryService = Anno4j.getInstance().createQueryService(Annotation.class);
        List<Annotation> defaultList = queryService
                .addPrefix("ex", "http://www.example.com/schema#")
                .setBodyCriteria("ex:value")
                .execute();

        assertEquals(1, defaultList.size());

        // Testing against the serialization date
        Annotation annotationDefault = defaultList.get(0);
        assertEquals("07.05.2015", annotationDefault.getSerializedAt());

        // Testing if the body was persisted correctly
        TestBody testBody = (TestBody) annotationDefault.getBody();
        assertEquals("Example Value", testBody.getValue());
    }

    @Test
    /**
     * Querying for a specific data type (e.g. xsd:double)
     */
    public void dataTypeTest() throws Exception {
        // Create test annotation
        TestBody body = new TestBody();
        body.setDoubleValue(2.0);

        Annotation annotation = new Annotation();
        annotation.setBody(body);

        // persist annotation
        Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation);

        QueryService<Annotation> queryService = Anno4j.getInstance().createQueryService(Annotation.class);
        List<Annotation> defaultList = queryService
                .addPrefix("ex", "http://www.example.com/schema#")
                .setBodyCriteria("ex:doubleValue[^^xsd:double]")
                .execute();

        TestBody testBody = (TestBody) defaultList.get(0).getBody();

        assertEquals(new Double(2.0), testBody.getDoubleValue());
    }

    @Test
    /**
     * Testing a query without setting a constraint but type
     */
    public void isATest() throws Exception {
        // Create test annotation
        TestBody body = new TestBody();
        body.setValue("Example Value");

        Annotation annotation = new Annotation();
        annotation.setSerializedAt("07.05.2015");
        annotation.setBody(body);

        // persist annotation
        Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation);

        // Querying for the persisted annotation
        QueryService<Annotation> queryService = Anno4j.getInstance().createQueryService(Annotation.class);
        List<Annotation> defaultList = queryService
                .addPrefix("ex", "http://www.example.com/schema#")
                .setBodyCriteria("[is-a ex:bodyType]")
                .execute();

        assertEquals(1, defaultList.size());

        // Testing against the serialization date
        Annotation annotationDefault = defaultList.get(0);
        assertEquals("07.05.2015", annotationDefault.getSerializedAt());

        // Testing if the body was persisted correctly
        TestBody testBody = (TestBody) annotationDefault.getBody();
        assertEquals("Example Value", testBody.getValue());
    }

}