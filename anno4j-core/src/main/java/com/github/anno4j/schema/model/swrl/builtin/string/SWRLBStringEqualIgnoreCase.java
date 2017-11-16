package com.github.anno4j.schema.model.swrl.builtin.string;

import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.swrl.Variable;
import com.github.anno4j.schema.model.swrl.builtin.SPARQLSerializable;
import com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltin;
import com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltinIri;
import com.github.anno4j.schema.model.swrl.engine.Bindings;
import com.github.anno4j.schema.model.swrl.engine.SWRLInferenceEngine;

import java.util.List;

/**
 * Implementation of the SWRL Built-in function
 * <a href="http://www.w3.org/2003/11/swrlb#stringEqualIgnoreCase">http://www.w3.org/2003/11/swrlb#stringEqualIgnoreCase</a> .
 * Satisfied iff the first argument is the same as the second argument (upper/lower case ignored).
 * This built-in is SPARQL serializable.
 */
@SWRLBuiltinIri(SWRLB.STRING_EQUAL_IGNORE_CASE)
public class SWRLBStringEqualIgnoreCase extends SWRLBuiltin implements SPARQLSerializable {

    /**
     * Initializes the built-in.
     * @param arguments List of two arguments.
     * @throws IllegalArgumentException Thrown if less than two arguments are passed or any non-variable
     * argument isn't an instance of {@link CharSequence}.
     */
    public SWRLBStringEqualIgnoreCase(List<Object> arguments) {
        super(arguments);

        // Check that there are two arguments:
        if(arguments.size() != 2) {
            throw new IllegalArgumentException("swrlb:stringEqualIgnoreCase expects two arguments. " + arguments.size() + " passed.");
        }
        // Check all non-variable arguments are char sequences:
        for(Object argument : arguments) {
            if(!(argument instanceof Variable || argument instanceof CharSequence)) {
                throw new IllegalArgumentException("swrlb:stringEqualIgnoreCase expects non-variable arguments to be instances of java.util.CharSequence.");
            }
        }
    }

    @Override
    public String asSPARQLFilterExpression() {
        return "LCASE(" + getArgumentAsFilterExpression(0) +
                ") = LCASE(" +
                getArgumentAsFilterExpression(1) + ")";
    }

    @Override
    public boolean evaluate(Bindings bindings) {
        Object value1 = getParameterValue(0, bindings);
        Object value2 = getParameterValue(1, bindings);

        if(value1 instanceof CharSequence && value2 instanceof CharSequence) {
            return value1.toString().toLowerCase().equals(value2.toString().toLowerCase());
        } else {
            return false;
        }
    }
}
