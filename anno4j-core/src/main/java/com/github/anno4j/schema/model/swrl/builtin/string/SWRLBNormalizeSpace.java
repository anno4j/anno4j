package com.github.anno4j.schema.model.swrl.builtin.string;

import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltin;
import com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltinIri;
import com.github.anno4j.schema.model.swrl.engine.Bindings;
import com.github.anno4j.schema.model.swrl.engine.SWRLInferenceEngine;

import java.util.List;

/**
 * Implementation of the SWRL Built-in function
 * <a href="http://www.w3.org/2003/11/swrlb#normalizeSpace">http://www.w3.org/2003/11/swrlb#normalizeSpace</a> .
 * Satisfied iff the first argument is equal to the whitespace-normalized value of the second argument.
 */
@SWRLBuiltinIri(SWRLB.NORMALIZE_SPACE)
public class SWRLBNormalizeSpace extends SWRLBuiltin {

    /**
     * Initializes the built-in.
     * @param arguments List of 2 arguments.
     * @throws IllegalArgumentException Thrown if there are not 2 arguments are passed.
     */
    public SWRLBNormalizeSpace(List<Object> arguments) {
        super(arguments);

        // Check that there are two arguments:
        if(arguments.size() != 2) {
            throw new IllegalArgumentException("swrlb:normalizeSpace expects two arguments. " + arguments.size() + " passed.");
        }
    }

    @Override
    public boolean evaluate(Bindings bindings) {
        Object value1 = getParameterValue(0, bindings);
        Object value2 = getParameterValue(1, bindings);

        if(value1 instanceof CharSequence && value2 instanceof CharSequence) {
            String s2 = "" + value2;
            return value1.equals(s2.replaceAll("\\s+", " ").trim());
        } else {
            return false;
        }
    }
}
