package com.github.anno4j.annotations;

/**
 * An annotation specifying how many values a property must at most have.
 * Corresponds to <a href="https://www.w3.org/TR/owl-ref/#maxCardinality-def">owl:maxCardinality</a>.
 */
public @interface MaxCardinality {

    /**
     * @return Returns the number of values that the property can at most have.
     */
    int value();
}
