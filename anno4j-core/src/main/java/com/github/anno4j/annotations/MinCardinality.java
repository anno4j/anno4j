package com.github.anno4j.annotations;

/**
 * An annotation specifying how many values a property must at least have.
 * Corresponds to <a href="https://www.w3.org/TR/owl-ref/#minCardinality-def">owl:minCardinality</a>.
 */
public @interface MinCardinality {

    /**
     * @return The minimum number of values a property must have.
     */
    int value();
}
