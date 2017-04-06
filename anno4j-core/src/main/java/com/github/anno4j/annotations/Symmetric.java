package com.github.anno4j.annotations;

/**
 * An annotation specifying that the property which receives this annotation is symmetric.
 * This means that if instance <code>X</code> is related to <code>Y</code> by the property
 * then also <code>Y</code> is related to <code>X</code>.
 * This annotation corresponds to <a href="https://www.w3.org/TR/owl-ref/#SymmetricProperty-def">owl:SymmetricProperty</a>.
 */
public @interface Symmetric {
}
