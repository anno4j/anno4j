package com.github.anno4j.annotations;

/**
 * An annotation specifying that the property which receives this annotation is functional.
 * This means that the property can not have multiple distinct values for each instance.
 * This annotation corresponds to <a href="https://www.w3.org/TR/owl-ref/#FunctionalProperty-def">owl:FunctionalProperty</a>.
 */
public @interface Functional {
}
