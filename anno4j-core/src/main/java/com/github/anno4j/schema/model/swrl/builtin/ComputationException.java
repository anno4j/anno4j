package com.github.anno4j.schema.model.swrl.builtin;

import com.github.anno4j.schema.model.swrl.engine.SWRLException;

/**
 * This exception is thrown if an error occurs while evaluating
 * a {@link Computation}.
 */
public class ComputationException extends SWRLException {
    public ComputationException() {
    }

    public ComputationException(String msg) {
        super(msg);
    }

    public ComputationException(Throwable t) {
        super(t);
    }
}
