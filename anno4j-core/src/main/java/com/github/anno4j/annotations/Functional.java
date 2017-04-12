package com.github.anno4j.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation specifying that the property which receives this annotation is functional.
 * This means that the property can not have multiple distinct values for each instance.
 * This annotation corresponds to <a href="https://www.w3.org/TR/owl-ref/#FunctionalProperty-def">owl:FunctionalProperty</a>.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD})
public @interface Functional {
}
