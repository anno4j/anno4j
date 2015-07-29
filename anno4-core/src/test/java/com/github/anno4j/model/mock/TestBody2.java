package com.github.anno4j.model.mock;

import com.github.anno4j.model.Body;
import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.LangString;

/**
 *
 */
@Iri("http://www.example.com/schema#bodyType2")
public class TestBody2 extends Body {

    public TestBody2() {
    }

    @Iri("http://www.example.com/schema#value")
    private String value;

    @Iri("http://www.example.com/schema#langValue")
    private LangString langValue;

    @Iri("http://www.example.com/schema#doubleValue")
    private Double doubleValue;

    public Double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public LangString getLangValue() {
        return langValue;
    }

    public void setLangValue(LangString langValue) {
        this.langValue = langValue;
    }

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
