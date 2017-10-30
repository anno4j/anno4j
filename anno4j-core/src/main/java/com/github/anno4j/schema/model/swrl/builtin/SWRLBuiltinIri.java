package com.github.anno4j.schema.model.swrl.builtin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to be used for specifying the IRI of a {@link SWRLBuiltin} type.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE})
public @interface SWRLBuiltinIri {

    /**
     * @return Returns the IRI of the SWRL builtin function.
     */
    String value();
}
