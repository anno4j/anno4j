package com.github.anno4j.schema.model.swrl.builtin.string;

import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.swrl.builtin.SPARQLSerializable;
import com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltin;
import com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltinIri;
import com.github.anno4j.schema.model.swrl.engine.Bindings;
import com.github.anno4j.schema.model.swrl.engine.SWRLInferenceEngine;

import java.util.List;

/**
 * Implementation of the SWRL Built-in function
 * <a href="http://www.w3.org/2003/11/swrlb#stringLength">http://www.w3.org/2003/11/swrlb#stringLength</a> .
 * Satisfied iff the first argument is equal to the length of the second argument.
 * This built-in is SPARQL serializable.
 */
@SWRLBuiltinIri(SWRLB.STRING_LENGTH)
public class SWRLBStringLength extends SWRLBuiltin implements SPARQLSerializable {

    /**
     * Initializes the built-in.
     * @param arguments List of 2 arguments.
     * @throws IllegalArgumentException Thrown if there are not 2 arguments are passed.
     */
    public SWRLBStringLength(List<Object> arguments) {
        super(arguments);

        // Check that there are two arguments:
        if(arguments.size() != 2) {
            throw new IllegalArgumentException("swrlb:stringLength expects two arguments. " + arguments.size() + " passed.");
        }
    }

    @Override
    public String asSPARQLFilterExpression() {
        return getArgumentAsFilterExpression(0) +
                " = STRLEN(" +
                getArgumentAsFilterExpression(1) + ")";
    }

    @Override
    public boolean evaluate(Bindings bindings) {
        Object value1 = getParameterValue(0, bindings);
        Object value2 = getParameterValue(1, bindings);

        if(value1 instanceof Integer && value2 instanceof CharSequence) {
            return (Integer) value1 == ((CharSequence) value2).length();
        } else {
            return false;
        }
    }
}
