package com.github.anno4j.annotations;

import com.github.anno4j.model.impl.ResourceObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation specifying that the property which receives this annotation
 * has a local range restriction associated with it on the particular class it is defined at.
 * This means that the values of the property must all be of the type defined as value of this
 * annotation.
 * This annotation corresponds to <a href="https://www.w3.org/TR/owl-guide/#owl_allValuesFrom">owl:allValuesFrom</a>.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface AllValuesFrom {

    /**
     * @return Returns the types which all values of the property must have at the class
     * it is defined at.
     */
    Class<? extends ResourceObject>[] value();
}
