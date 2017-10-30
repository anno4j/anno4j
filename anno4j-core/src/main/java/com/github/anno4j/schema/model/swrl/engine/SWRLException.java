package com.github.anno4j.schema.model.swrl.engine;

import org.openrdf.repository.RepositoryException;

/**
 * This exception is thrown if an error occurs while
 * reasoning over SWRL rules or if a rule is not supported.
 */
public class SWRLException extends RepositoryException {
    public SWRLException() {
    }

    public SWRLException(String msg) {
        super(msg);
    }

    public SWRLException(Throwable t) {
        super(t);
    }
}
