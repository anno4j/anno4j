package com.github.anno4j.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the restrictions made on the cardinality of the annotated property.
 * Multiple qualified restrictions can be made on a property in order to
 * specify the number of values of a certain type the property must have.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD})
public @interface Cardinalities {

    /**
     * Returns the cardinality constraints imposed.
     * @return The cardinality constraints imposed.
     */
    Cardinality[] value();
}
