package com.github.anno4j.annotations;

/**
 * An annotation specifying that the property which receives this annotation,
 * is the inverse of another.
 * This means that if instance <code>X</code> is related to <code>Y</code> by this property
 * then <code>Y</code> is also related to <code>X</code> by the inverse property.
 * This annotation corresponds to <a href="https://www.w3.org/TR/owl-ref/#inverseOf-def">owl:inverseOf</a>.
 */
public @interface InverseOf {

    /**
     * @return Returns the IRI of the inverse property.
     */
    String value();
}
