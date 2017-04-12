package com.github.anno4j.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation specifying the cardinality of a property.
 * This means the numbers of values that the property may have.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD})
public @interface Cardinality {

    /**
     * @return Returns the number of values the annotated property must have.
     */
    int value();
}
