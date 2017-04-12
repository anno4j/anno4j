package com.github.anno4j.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation specifying that the property which receives this annotation is symmetric.
 * This means that if instance <code>X</code> is related to <code>Y</code> by the property
 * then also <code>Y</code> is related to <code>X</code>.
 * This annotation corresponds to <a href="https://www.w3.org/TR/owl-ref/#SymmetricProperty-def">owl:SymmetricProperty</a>.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD})
public @interface Symmetric {
}
