package com.github.anno4j.exceptions;

/**
 *  Signals that the /META-INF/org.openrdf.concepts file was not found. This (empty) file is required by openrdf Alibaba
 *  to persist annotated objects.
 */
public class ConceptNotFoundException extends IllegalStateException {

    /**
     * Constructor
     * @param message Exception message
     */
    public ConceptNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor
     * @param message Exception message
     * @param cause Exception cause
     */
    public ConceptNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
