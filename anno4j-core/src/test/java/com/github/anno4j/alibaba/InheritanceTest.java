package com.github.anno4j.alibaba;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Body;
import com.github.anno4j.annotations.Partial;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.annotations.Iri;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test inheritance of annotated classes. RDF of Superclass should be mapped to a Subclass. Subclass can add more functionality and access superclass information.
 */
public class InheritanceTest {
    private Anno4j anno4j;

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
    }

    @Test
    public void inheritanceTest() throws Exception {
        // Create the base annotation
        Annotation annotation = anno4j.createObject(Annotation.class);

        // Create the body
        Superclass body = anno4j.createObject(Superclass.class);
        body.setValue("myValue");
        annotation.addBody(body);

        // Query persisted object
        List<Annotation> result = anno4j.findAll(Annotation.class);

        assertEquals(1, result.size());

        Annotation resultObject = result.get(0);
        Body resultBody = resultObject.getBodies().iterator().next();

        assertTrue(resultBody instanceof  Superclass);
        assertEquals("myValue", ((Superclass) resultBody).getValue());
        assertTrue(resultBody instanceof Subclass);
        assertEquals("myValueCUSTOM", ((Subclass) resultBody).getCustomValue());
    }

    @Iri("http://www.example.com/Superclass")
    public static interface Superclass extends Body {
        @Iri("http://www.example.com/Superclass/value")
        public String getValue();

        @Iri("http://www.example.com/Superclass/value")
        public void setValue(String value);
    }

    @Iri("http://www.example.com/Superclass")
    public static interface Subclass extends Superclass{
        String getCustomValue();
    }

    @Partial
    public static abstract class SubclassSupport implements Subclass{
        public String getCustomValue(){
            return this.getValue() + "CUSTOM";
        }
    }
}
