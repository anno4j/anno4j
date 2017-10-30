package com.github.anno4j.schema.model.swrl.builtin.math;

import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.swrl.Variable;
import com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltin;
import com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltinIri;
import com.github.anno4j.schema.model.swrl.engine.Bindings;
import com.github.anno4j.schema.model.swrl.engine.SWRLInferenceEngine;

import java.util.List;

/**
 * Implementation of the SWRL Built-in function
 * <a href="http://www.w3.org/2003/11/swrlb#mod">http://www.w3.org/2003/11/swrlb#mod</a> .
 * It receives three arguments x, y, z. The predicate is true iff x = y % z holds.
 */
@SWRLBuiltinIri(SWRLB.MOD)
public class SWRLBMod extends SWRLBuiltin {

    /**
     * Initializes the built-in.
     * @param arguments List of three arguments. Must be numeric or variables.
     * @throws IllegalArgumentException Thrown if less than three arguments are passed or any non-variable
     * argument is not numeric.
     */
    public SWRLBMod(List<Object> arguments) {
        super(arguments);

        // Check that there are three arguments:
        if(arguments.size() != 3) {
            throw new IllegalArgumentException("swrlb:mod expects three arguments. " + arguments.size() + " passed.");
        }
        // Check all non-variable arguments are numeric:
        for(Object argument : arguments) {
            if(!(argument instanceof Variable || argument instanceof Number)) {
                throw new IllegalArgumentException("swrlb:mod expects non-variable arguments to be numeric.");
            }
        }
    }

    @Override
    public boolean evaluate(Bindings bindings) throws SWRLInferenceEngine.UnboundVariableException {
        Object x = getParameterValue(0, bindings);
        Object y = getParameterValue(1, bindings);
        Object z = getParameterValue(2, bindings);

        if(x instanceof Number && y instanceof Number && z instanceof Number) {
            return ((Number) x).doubleValue() == ((Number) y).doubleValue() % ((Number) z).doubleValue();
        } else {
            return false;
        }
    }
}
