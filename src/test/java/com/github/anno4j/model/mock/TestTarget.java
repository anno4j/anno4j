package com.github.anno4j.model.mock;

import com.github.anno4j.model.Target;
import org.openrdf.annotations.Iri;

/**
 * Class used for testing purposes of targets.
 */
@Iri("http://www.example.com/schema#TargetType")
public class TestTarget extends Target {

    public TestTarget() {};

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
        return "TestTarget{" +
                "value='" + value + '\'' +
                '}';
    }
}
