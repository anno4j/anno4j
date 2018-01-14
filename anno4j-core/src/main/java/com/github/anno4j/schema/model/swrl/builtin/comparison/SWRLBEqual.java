package com.github.anno4j.schema.model.swrl.builtin.comparison;

import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.swrl.Variable;
import com.github.anno4j.schema.model.swrl.builtin.*;
import com.github.anno4j.schema.model.swrl.engine.Bindings;
import com.github.anno4j.schema.model.swrl.engine.SolutionSet;

import java.util.List;

/**
 * Implementation of the SWRL Built-in function
 * <a href="http://www.w3.org/2003/11/swrlb#equal">http://www.w3.org/2003/11/swrlb#equal</a> .
 * It receives two arguments x, y. The predicate is true iff x = y holds.
 * This implementation is capable of binding a variable by computing its unique value.
 * This built-in is SPARQL serializable.
 */
@SWRLBuiltinIri(SWRLB.EQUAL)
public class SWRLBEqual extends SWRLBuiltin implements SPARQLSerializable, Computation {

    /**
     * Initializes the built-in.
     * @param arguments List of two arguments.
     * @throws IllegalArgumentException Thrown if less than two arguments are passed.
     */
    public SWRLBEqual(List<Object> arguments) {
        super(arguments);

        if(arguments.size() != 2) {
            throw new IllegalArgumentException("swrlb:equal expects two arguments. " + arguments.size() + " passed.");
        }
    }

    @Override
    public boolean evaluate(Bindings bindings) {
        Object value1 = getParameterValue(0, bindings);
        Object value2 = getParameterValue(1, bindings);

        if(value1 instanceof Number && value2 instanceof Number) {
            return ((Number) value1).doubleValue() == ((Number) value2).doubleValue();
        } else {
            return value1.equals(value2);
        }
    }

    @Override
    public String asSPARQLFilterExpression() {
        return getArgumentAsFilterExpression(0) +
                " = " +
                getArgumentAsFilterExpression(1);
    }

    @Override
    public SolutionSet solve(Bindings bindings) throws InfiniteResultException, IllegalArgumentException, UnderDeterminedSolutionException {
        SolutionSet solutions = new SolutionSet();

        Object x = getParameterValue(0, bindings);
        Object y = getParameterValue(1, bindings);

        if (getArgument(0) instanceof Variable && !(getArgument(1) instanceof Variable)) {
            solutions.add(new Bindings(bindings, (Variable) getArgument(0), y));
        } else if (getArgument(1) instanceof Variable && !(getArgument(0) instanceof Variable)) {
            solutions.add(new Bindings(bindings, (Variable) getArgument(1), x));
        } else {
            throw new UnderDeterminedSolutionException();
        }

        return solutions;
    }
}
