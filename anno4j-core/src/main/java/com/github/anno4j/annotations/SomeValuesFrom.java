package com.github.anno4j.annotations;

import com.github.anno4j.model.impl.ResourceObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation specifying that the property which receives this annotation
 * has at least one mapping from the class it was defined on to a value of a certain type.
 * This annotation corresponds to <a href="https://www.w3.org/TR/owl-guide/#owl_someValuesFrom">owl:someValuesFrom</a>.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface SomeValuesFrom {

    /**
     * @return Returns the types which some values of the property must have at the class
     * it is defined at.
     */
    Class<? extends ResourceObject>[] value();
}
