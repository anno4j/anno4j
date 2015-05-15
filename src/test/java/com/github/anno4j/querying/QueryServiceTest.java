package com.github.anno4j.querying;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.mock.TestBody;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test method for the QueryService functionality
 */
public class QueryServiceTest {

    /**
     * A simple query test. Queries the data that was persisted in the first place.
     *
     * @throws Exception
     */
    @Test
    public void queryTest() throws Exception {

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
}