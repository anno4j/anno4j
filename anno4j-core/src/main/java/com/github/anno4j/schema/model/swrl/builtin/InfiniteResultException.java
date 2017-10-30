package com.github.anno4j.schema.model.swrl.builtin;

import java.util.Map;

/**
 * This exception is thrown if the number of possible solutions
 * computable by {@link Computation#solve(Map)} for a binding is infinite.
 */
public class InfiniteResultException extends ComputationException {

    public InfiniteResultException() {
    }

    public InfiniteResultException(String message) {
        super(message);
    }
}
