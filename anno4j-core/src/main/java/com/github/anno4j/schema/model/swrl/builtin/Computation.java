package com.github.anno4j.schema.model.swrl.builtin;

import com.github.anno4j.schema.model.swrl.engine.Bindings;
import com.github.anno4j.schema.model.swrl.engine.SolutionSet;

public interface Computation {

    /**
     * Determines solutions for the free variable of the built-in given the bindings for other variables.
     * @param bindings Bindings for all variables but one.
     * @return Returns a set of solution bindings for the computable variable. This contains {@code bindings}.
     * @throws UnderDeterminedSolutionException Thrown if more than one variable is not bound by {@code bindings}.
     * @throws InfiniteResultException Thrown if there is an infinite number of solutions.
     * @throws IllegalArgumentException Thrown if any parameter has a wrong type.
     */
    SolutionSet solve(Bindings bindings) throws IllegalArgumentException, UnderDeterminedSolutionException;
}
