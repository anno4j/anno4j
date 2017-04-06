package com.github.anno4j.annotations;

/**
 * An annotation specifying that the property which receives this annotation is inverse functional.
 * This means that there can be no distinct instances <code>X</code>, <code>Y</code> which are related
 * to the same instance <code>Z</code> by this property, i.e. the object of the property
 * uniquely determines the subject.
 * This annotation corresponds to <a href="https://www.w3.org/TR/owl-ref/#InverseFunctionalProperty-def">owl:InverseFunctionalProperty</a>.
 */
public @interface InverseFunctional {
}
