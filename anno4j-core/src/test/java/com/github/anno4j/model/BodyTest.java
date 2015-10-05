package com.github.anno4j.model;

import com.github.anno4j.Anno4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.LangString;
import org.openrdf.repository.object.ObjectConnection;

import static org.junit.Assert.assertEquals;

/**
 * Created by schlegel on 06/05/15.
 */
public class BodyTest {

    private Anno4j anno4j;
    private ObjectConnection connection;

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
        this.connection = this.anno4j.getObjectRepository().getConnection();
    }

    @After
    public void tearDown() throws Exception {
        connection.close();
    }

    @Test
    public void testPersistAnnotation() throws Exception {
        // Create test annotation
        TestBody body = anno4j.createObject(TestBody.class);
        body.setValue("Example Value");

        Annotation annotation = anno4j.createObject(Annotation.class);
        annotation.setBody(body);


        // persist annotation
        connection.addObject(annotation);

        // query persisted object and check test body implementation
        Annotation result = connection.getObject(Annotation.class, annotation.getResource());
        assertEquals(((TestBody)annotation.getBody()).getValue(), ((TestBody) result.getBody()).getValue());
    }

    @Iri("http://www.example.com/schema#bodyType")
    public static interface TestBody extends Body {
        @Iri("http://www.example.com/schema#doubleValue")
        Double getDoubleValue();

        @Iri("http://www.example.com/schema#doubleValue")
        void setDoubleValue(Double doubleValue);

        @Iri("http://www.example.com/schema#langValue")
        LangString getLangValue();

        @Iri("http://www.example.com/schema#langValue")
        void setLangValue(LangString langValue);

        @Iri("http://www.example.com/schema#value")
        String getValue();

        @Iri("http://www.example.com/schema#value")
        void setValue(String value);
    }
}