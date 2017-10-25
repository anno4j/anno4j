package com.github.anno4j.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation specifying that the property which receives this annotation,
 * is a transitive property.
 * This means that if instance <code>X</code> is related to <code>Y</code> by the property
 * and <code>Y</code> to <code>Z</code>, then also <code>X</code> is related to <code>Z</code>
 * by the property.
 * This annotation corresponds to <a href="https://www.w3.org/TR/owl-ref/#TransitiveProperty-def">owl:TransitiveProperty</a>.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD})
public @interface Transitive {
}
