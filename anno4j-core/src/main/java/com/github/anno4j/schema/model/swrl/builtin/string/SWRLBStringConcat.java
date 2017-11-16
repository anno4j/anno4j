package com.github.anno4j.schema.model.swrl.builtin.string;

import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.swrl.Variable;
import com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltin;
import com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltinIri;
import com.github.anno4j.schema.model.swrl.engine.Bindings;
import com.github.anno4j.schema.model.swrl.engine.SWRLInferenceEngine;

import java.util.List;

/**
 * Implementation of the SWRL Built-in function
 * <a href="http://www.w3.org/2003/11/swrlb#stringConcat">http://www.w3.org/2003/11/swrlb#stringConcat</a> .
 * Satisfied iff the first argument is equal to the string resulting from the concatenation of the strings
 * the second argument through the last argument.
 */
@SWRLBuiltinIri(SWRLB.STRING_CONCAT)
public class SWRLBStringConcat extends SWRLBuiltin {

    /**
     * Initializes the built-in.
     * @param arguments List of two arguments.
     * @throws IllegalArgumentException Thrown if less than two arguments are passed or any non-variable
     * argument isn't an instance of {@link CharSequence}.
     */
    public SWRLBStringConcat(List<Object> arguments) {
        super(arguments);

        // Check that there are two arguments:
        if(arguments.size() < 2) {
            throw new IllegalArgumentException("swrlb:stringConcat expects at least two arguments. " + arguments.size() + " passed.");
        }
        // Check all non-variable arguments are char sequences:
        for(Object argument : arguments) {
            if(!(argument instanceof Variable || argument instanceof CharSequence)) {
                throw new IllegalArgumentException("swrlb:stringConcat expects non-variable arguments to be instances of java.util.CharSequence.");
            }
        }
    }

    @Override
    public boolean evaluate(Bindings bindings) {
        Object value1 = getParameterValue(0, bindings);

        if(value1 instanceof CharSequence) {
            StringBuilder concat = new StringBuilder();
            for(int argIndex = 1; argIndex < getArguments().size(); argIndex++) {
                if(getParameterValue(argIndex, bindings) instanceof CharSequence) {
                    concat.append((CharSequence) getParameterValue(argIndex, bindings));
                } else {
                    return false;
                }
            }

            return value1.toString().equals(concat.toString());

        } else {
            return false;
        }
    }
}
