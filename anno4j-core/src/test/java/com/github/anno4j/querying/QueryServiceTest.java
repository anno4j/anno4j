package com.github.anno4j.querying;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.mock.TestBody;
import com.github.anno4j.model.mock.TestBody2;
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

        /**
         * Persisting and Testing for objects with the ex:value attribute set.
         * Expecting to return only one result.
         */

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

        /**
         * Persisting another body setting the ex:doubleValue attribute.
         * Expecting the QueryService to return only the second annotation object.
         */

        // Create test annotation
        TestBody body1 = new TestBody();
        body1.setDoubleValue(3.0);

        Annotation annotation1 = new Annotation();
        annotation1.setBody(body1);

        // persist annotation
        Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation1);

        QueryService<Annotation> queryService1 = Anno4j.getInstance().createQueryService(Annotation.class);
        List<Annotation> defaultList1 = queryService1
                .addPrefix("ex", "http://www.example.com/schema#")
                .setBodyCriteria("ex:doubleValue")
                .execute();

        assertEquals(1, defaultList1.size());

        Annotation annotationDefault1 = defaultList1.get(0);

        // Testing if the body was persisted correctly
        TestBody testBody1 = (TestBody) annotationDefault1.getBody();
        assertEquals(new Double(3.0), testBody1.getDoubleValue());

        /**
         * Querying for objects which have set the ex:langValue attribute.
         * Expecting the QueryService to return an empty list, because we did not persist such an object in the first
         * place.
         */

        QueryService<Annotation> queryService2 = Anno4j.getInstance().createQueryService(Annotation.class);
        List<Annotation> defaultList2 = queryService2
                .addPrefix("ex", "http://www.example.com/schema#")
                .setBodyCriteria("ex:langValue")
                .execute();

        assertEquals(0, defaultList2.size());
    }

    @Test
    /**
     * Querying for a specific data type (e.g. xsd:double)
     */
    public void dataTypeTest() throws Exception {
        /**
         * Persisting two bodies. The first one has a doubleValue set, the second one not. Testing if
         * the QueryService will only return the first annotation object, containing the TestBody
         * with the doubleValue set.
         */

        // Create test annotation
        TestBody body = new TestBody();
        body.setDoubleValue(2.0);

        Annotation annotation = new Annotation();
        annotation.setBody(body);

        // persist annotation
        Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation);

        // Create second body
        TestBody body1 = new TestBody();
        body1.setValue("2.0");

        Annotation annotation1 = new Annotation();
        annotation1.setBody(body1);

        // persist annotation1
        Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation1);

        // Querying
        QueryService<Annotation> queryService = Anno4j.getInstance().createQueryService(Annotation.class);
        List<Annotation> defaultList = queryService
                .addPrefix("ex", "http://www.example.com/schema#")
                .setBodyCriteria("ex:doubleValue[^^xsd:double]")
                .execute();

        // Querying should only provide the first annotation object
        assertEquals(1, defaultList.size());

        TestBody testBody = (TestBody) defaultList.get(0).getBody();

        // Testing if the right annotation was returned
        assertEquals(new Double(2.0), testBody.getDoubleValue());
    }

    @Test
    /**
     * Testing a query without setting a constraint but type
     */
    public void isATest() throws Exception {
        /**
         *  Testing if the TestBody object will be persisted and queried as expected.
         */

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


        /**
         *  Persisting and querying a TestBody2. Querying for the type (ex:bodyType2).
         *  Expecting to only receive the TestBody2 and not the TestBody because they
         *  differ in the rdf:type.
         */

        TestBody2 body2 = new TestBody2();
        body2.setValue("Example Value 2");

        Annotation annotation2 = new Annotation();
        annotation2.setSerializedAt("07.07.2017");
        annotation2.setBody(body2);

        // persist second annotation
        Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation2);

        QueryService<Annotation> queryService2 = Anno4j.getInstance().createQueryService(Annotation.class);

        // Querying for the persisted annotation
        List<Annotation> list = queryService2
                .addPrefix("ex", "http://www.example.com/schema#")
                .setBodyCriteria("[is-a ex:bodyType2]")
                .execute();

        assertEquals(1, list.size());

        // Testing against the serialization date
        Annotation an = list.get(0);
        assertEquals("07.07.2017", an.getSerializedAt());

        // Testing if the body was persisted correctly
        TestBody2 testBody2 = (TestBody2) an.getBody();
        assertEquals("Example Value 2", testBody2.getValue());

        /**
         * Querying objects with the rdf:type ex:bodyType3. We did not persist such an object in the first place,
         * so we expect an empty result list.
         */

        QueryService<Annotation> queryService3 = Anno4j.getInstance().createQueryService(Annotation.class);
        List<Annotation> emptyList = queryService3
                .addPrefix("ex", "http://www.example.com/schema#")
                .setBodyCriteria("[is-a ex:bodyType3]")  // This criteria does not match any persisted annotation.
                .execute();

        // The list should not contain any result object
        assertEquals(0, emptyList.size());
    }
}