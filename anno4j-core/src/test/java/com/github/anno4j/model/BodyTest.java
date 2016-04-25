package com.github.anno4j.model;

import com.github.anno4j.Anno4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.LangString;
import org.openrdf.repository.object.ObjectConnection;

import static org.junit.Assert.assertEquals;

public class BodyTest {

    private Anno4j anno4j;

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
    }

    @Test
    public void testPersistAnnotation() throws Exception {
        // Create test annotation
        TestBody body = anno4j.createObject(TestBody.class);
        body.setValue("Example Value");

        Annotation annotation = anno4j.createObject(Annotation.class);
        annotation.setBody(body);

        // query persisted object and check test body implementation
        Annotation result = anno4j.findByID(Annotation.class, annotation.getResourceAsString());
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