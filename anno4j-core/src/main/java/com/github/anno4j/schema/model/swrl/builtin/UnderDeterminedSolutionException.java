package com.github.anno4j.schema.model.swrl.builtin;

/**
 * This exception is thrown if no solution can be
 */
public class UnderDeterminedSolutionException extends ComputationException {

    public UnderDeterminedSolutionException() {
    }

    public UnderDeterminedSolutionException(String message) {
        super(message);
    }
}
