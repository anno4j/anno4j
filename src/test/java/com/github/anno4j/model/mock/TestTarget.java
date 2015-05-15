package com.github.anno4j.model.mock;

import com.github.anno4j.model.Target;
import org.openrdf.annotations.Iri;

/**
 * Class used for testing purposes of targets.
 */
@Iri("http://www.example.com/schema#TargetType")
public class TestTarget extends Target {

    /**
     * The value of the annotation.
     */
    @Iri("http://www.example.com/schema#value")
    private String value;

    /**
     * Standard constructor.
     */
    public TestTarget() {};

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "TestTarget{" +
                "value='" + value + '\'' +
                '}';
    }

    /**
     * Gets The value of the annotation..
     *
     * @return Value of The value of the annotation..
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets new The value of the annotation..
     *
     * @param value New value of The value of the annotation..
     */
    public void setValue(String value) {
        this.value = value;
    }
}
