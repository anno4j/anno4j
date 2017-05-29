package com.github.anno4j.annotations;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.schema.model.owl.OWLClazz;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation specifying how many values a property must at least have.
 * Corresponds to <a href="https://www.w3.org/TR/owl-ref/#minCardinality-def">owl:minCardinality</a>.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD})
public @interface MinCardinality {

    /**
     * @return The minimum number of values a property must have.
     */
    int value();

    /**
     * The class on which the restriction is imposed.
     * owl:Class is allowed to make the restriction unqualified.
     * @return The class on which this restriction is imposed.
     */
    Class<? extends ResourceObject> onClass() default OWLClazz.class;
}
