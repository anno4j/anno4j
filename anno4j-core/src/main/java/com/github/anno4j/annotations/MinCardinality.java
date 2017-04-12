package com.github.anno4j.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation specifying how many values a property must at least have.
 * Corresponds to <a href="https://www.w3.org/TR/owl-ref/#minCardinality-def">owl:minCardinality</a>.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD})
public @interface MinCardinality {

    /**
     * @return The minimum number of values a property must have.
     */
    int value();
}
