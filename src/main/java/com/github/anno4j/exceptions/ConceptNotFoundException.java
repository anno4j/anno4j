package com.github.anno4j.exceptions;

/**
 *  Signals that the /META-INF/org.openrdf.concepts file was not found. This (empty) file is required by openrdf Alibaba
 *  to persist annotated objects.
 */
public class ConceptNotFoundException extends IllegalStateException {

    public ConceptNotFoundException(String s) {
        super(s);
    }

    public ConceptNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
