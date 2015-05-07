package com.github.anno4j.model.mock;

import com.github.anno4j.model.Body;
import org.openrdf.annotations.Iri;

/**
 * Created by schlegel on 06/05/15.
 */
@Iri("http://www.example.com/schema#bodyType")
public class TestBody extends Body {

    public TestBody() {
    }

    @Iri("http://www.example.com/schema#value")
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "TestBody{" +
                "value='" + value + '\'' +
                '}';
    }
}
