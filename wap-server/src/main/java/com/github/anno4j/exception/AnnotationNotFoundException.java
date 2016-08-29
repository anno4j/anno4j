package com.github.anno4j.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception used when a non-existing Annotation is to be retrieved.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class AnnotationNotFoundException extends RuntimeException {

    public AnnotationNotFoundException(String annoId) {
        super("Could not find Annotation with id: " + annoId);
    }
}
