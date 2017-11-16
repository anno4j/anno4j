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
 * <a href="http://www.w3.org/2003/11/swrlb#unaryPlus">http://www.w3.org/2003/11/swrlb#unaryPlus</a> .
 * It receives two arguments x, y. The predicate is true iff x = +y holds.
 * This implementation is capable of binding a variable by computing its unique value.
 * This built-in is SPARQL serializable.
 */
@SWRLBuiltinIri(SWRLB.UNARY_PLUS)
public class SWRLBUnaryPlus extends SWRLBuiltin implements SPARQLSerializable, Computation {

    /**
     * Initializes the built-in.
     * @param arguments List of two arguments.
     * @throws IllegalArgumentException Thrown if less than two arguments are passed or any non-variable
     * argument is non-numeric.
     */
    public SWRLBUnaryPlus(List<Object> arguments) {
        super(arguments);

        // Check that there are two arguments:
        if(arguments.size() != 2) {
            throw new IllegalArgumentException("swrlb:unaryPlus expects two arguments. " + arguments.size() + " passed.");
        }
        // Check all non-variable arguments are numeric:
        for(Object argument : arguments) {
            if(!(argument instanceof Variable || argument instanceof Number)) {
                throw new IllegalArgumentException("swrlb:unaryPlus expects non-variable arguments to be numeric.");
            }
        }
    }

    @Override
    public SolutionSet solve(Bindings bindings) throws IllegalArgumentException, UnderDeterminedSolutionException {
        SolutionSet solutions = new SolutionSet();

        Object x = getParameterValue(0, bindings);
        Object y = getParameterValue(1, bindings);

        // x = +y
        // y = x
        if (getArgument(0) instanceof Variable && !(getArgument(1) instanceof Variable)) {
            solutions.add(new Bindings(bindings, (Variable) getArgument(0), ((Number) y).doubleValue()));
        } else if (getArgument(1) instanceof Variable && !(getArgument(0) instanceof Variable)) {
            solutions.add(new Bindings(bindings, (Variable) getArgument(1), ((Number) x).doubleValue()));
        } else {
            throw new UnderDeterminedSolutionException();
        }

        return solutions;
    }

    @Override
    public String asSPARQLFilterExpression() {
        return getArgumentAsFilterExpression(0) +
                " = " +
                getArgumentAsFilterExpression(1);
    }

    @Override
    public boolean evaluate(Bindings bindings) {
        Object value1 = getParameterValue(0, bindings);
        Object value2 = getParameterValue(1, bindings);

        return ((Number) value1).doubleValue() == +((Number) value2).doubleValue();
    }
}
