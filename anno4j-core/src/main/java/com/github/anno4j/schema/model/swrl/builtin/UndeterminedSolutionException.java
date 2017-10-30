package com.github.anno4j.schema.model.swrl.builtin;

/**
 * This exception is thrown if no solution can be
 */
public class UndeterminedSolutionException extends ComputationException {

    public UndeterminedSolutionException() {
    }

    public UndeterminedSolutionException(String message) {
        super(message);
    }
}
