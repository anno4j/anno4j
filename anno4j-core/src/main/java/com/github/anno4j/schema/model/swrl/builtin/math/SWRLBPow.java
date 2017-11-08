package com.github.anno4j.schema.model.swrl.builtin.math;

import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.swrl.Variable;
import com.github.anno4j.schema.model.swrl.builtin.*;
import com.github.anno4j.schema.model.swrl.engine.Bindings;
import com.github.anno4j.schema.model.swrl.engine.SWRLInferenceEngine;
import com.github.anno4j.schema.model.swrl.engine.SolutionSet;

import java.util.List;

/**
 * Implementation of the SWRL Built-in function
 * <a href="http://www.w3.org/2003/11/swrlb#multiply">http://www.w3.org/2003/11/swrlb#multiply</a> .
 * It receives three arguments x, y, z. The predicate is true iff x = y / z holds.
 * This implementation is capable of binding a variable by computing its unique value.
 */
@SWRLBuiltinIri(SWRLB.POW)
public class SWRLBPow extends SWRLBuiltin implements Computation {

    /**
     * Initializes the built-in.
     * @param arguments List of three arguments. Must be numeric or variables.
     * @throws IllegalArgumentException Thrown if less than three arguments are passed or any non-variable
     * argument is not numeric.
     */
    public SWRLBPow(List<Object> arguments) {
        super(arguments);

        // Check that there are three arguments:
        if(arguments.size() != 3) {
            throw new IllegalArgumentException("swrlb:pow expects three arguments. " + arguments.size() + " passed.");
        }
        // Check all non-variable arguments are numeric:
        for(Object argument : arguments) {
            if(!(argument instanceof Variable || argument instanceof Number)) {
                throw new IllegalArgumentException("swrlb:pow expects non-variable arguments to be numeric.");
            }
        }
    }

    @Override
    public SolutionSet solve(Bindings bindings) throws InfiniteResultException, IllegalArgumentException, UnderDeterminedSolutionException {
        SolutionSet solutions = new SolutionSet();

        Object x = getParameterValue(0, bindings);
        Object y = getParameterValue(1, bindings);
        Object z = getParameterValue(2, bindings);

        // x = y ^ z
        // y = root(x, z) = x ^ (1/z)
        // z = log(x, y) = log(x) / log(y)
        if (getArgument(0) instanceof Variable && x == null && y != null && z != null) {
            validateNumeric(y, z);
            solutions.add(new Bindings(bindings, (Variable) getArgument(0), Math.pow(((Number) y).doubleValue(), ((Number) z).doubleValue())));
        } else if (getArgument(1) instanceof Variable && y == null && x != null && z != null) {
            validateNumeric(x, z);
            solutions.add(new Bindings(bindings, (Variable) getArgument(1), Math.pow(((Number) x).doubleValue(), 1.0/((Number) z).doubleValue())));
        } else if (getArgument(2) instanceof Variable && z == null && x != null && y != null) {
            validateNumeric(x, y);
            if(((Number) y).doubleValue() != 1.0) {
                solutions.add(new Bindings(bindings, (Variable) getArgument(2), Math.log(((Number) x).doubleValue()) / Math.log(((Number) y).doubleValue())));
            }
        } else {
            throw new UnderDeterminedSolutionException();
        }

        return solutions;
    }

    @Override
    public boolean evaluate(Bindings bindings) throws SWRLInferenceEngine.UnboundVariableException {
        Object x = getParameterValue(0, bindings);
        Object y = getParameterValue(1, bindings);
        Object z = getParameterValue(2, bindings);

        if(x instanceof Number && y instanceof Number && z instanceof Number) {
            return ((Number) x).doubleValue() == Math.pow(((Number) y).doubleValue(), ((Number) z).doubleValue());
        } else {
            return false;
        }
    }
}
