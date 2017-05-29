package com.github.anno4j.annotations;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.schema.model.owl.OWLClazz;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation specifying the cardinality of a property.
 * This means the numbers of values that the property may have.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD})
public @interface Cardinality {

    /**
     * @return Returns the number of values the annotated property must have.
     */
    int value();

    /**
     * The class on which the restriction is imposed.
     * owl:Class is allowed to make the restriction unqualified.
     * @return The class on which this restriction is imposed.
     */
    Class<? extends ResourceObject> onClass() default OWLClazz.class;
}
