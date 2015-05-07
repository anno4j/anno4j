package com.github.anno4j.querying;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.impl.annotation.AnnotationDefault;
import com.github.anno4j.model.mock.TestBody;
import org.junit.Test;
import org.openrdf.repository.object.ObjectConnection;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class QueryServiceTest {

    @Test
    public void queryTest() throws Exception {
        ObjectConnection connection = Anno4j.getInstance().getObjectRepository().getConnection();

        // Create test annotation
        TestBody body = new TestBody();
        body.setValue("Example Value");

        Annotation annotation = new AnnotationDefault();
        annotation.setSerializedAt("07.05.2015");
        annotation.setBody(body);

        // persist annotation
        connection.addObject(annotation);

        // Querying for the persisted body
        QueryService<AnnotationDefault> queryService = new QueryService(AnnotationDefault.class);
        List<AnnotationDefault> defaultList = queryService
                .addPrefix("ex", "http://www.example.com/schema#")
                .setBodyCriteria("ex:value", "Example Value")
                .execute();

        assertEquals(1, defaultList.size());

        AnnotationDefault annotationDefault = defaultList.get(0);
        assertEquals("07.05.2015", annotationDefault.getSerializedAt());

        TestBody testBody = (TestBody) annotationDefault.getBody();
        assertEquals("Example Value", testBody.getValue());

        connection.close();
    }
}