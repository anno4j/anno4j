package com.github.anno4j.schema_parsing.mapping;

/**
 * Exception signalizing that the mapping performed by a {@link DatatypeMapper}
 * is not a Anno4j supported Java type.
 */
public class IllegalMappingException extends RuntimeException {

    /**
     * {@inheritDoc}
     */
    public IllegalMappingException() {
    }

    /**
     * {@inheritDoc}
     */
    public IllegalMappingException(String message) {
        super(message);
    }
}
