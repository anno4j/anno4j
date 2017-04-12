package com.github.anno4j.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation specifying that the property which receives this annotation is bijective.
 * This means that the mapping done by the property is identifying in both directions,
 * i.e. the property is functional and inverse functional (see {@link Functional}, {@link InverseFunctional}).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD})
public @interface Bijective {
}
