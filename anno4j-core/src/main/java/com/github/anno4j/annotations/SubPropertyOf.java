package com.github.anno4j.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation specifying that the property which receives this annotation
 * is the subproperty of another.
 * This means that all values set for this property are also set for all superproperties.
 * This annotation corresponds to <a href="http://www.w3.org/TR/2004/REC-owl-ref-20040210/#subPropertyOf-def">rdfs:subPropertyOf</a>.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface SubPropertyOf {

    /**
     * @return Returns the IRIs of the properties which the annotated property is a sub-property of.
     */
    String[] value();
}
