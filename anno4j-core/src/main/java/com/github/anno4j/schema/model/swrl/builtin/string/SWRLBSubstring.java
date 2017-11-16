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
 * <a href="http://www.w3.org/2003/11/swrlb#substring">http://www.w3.org/2003/11/swrlb#substring</a> .
 * Satisfied iff the first argument is equal to the substring of optional length the fourth argument starting at
 * character offset the third argument in the string the second argument.
 * This built-in is SPARQL serializable.
 */
@SWRLBuiltinIri(SWRLB.SUBSTRING)
public class SWRLBSubstring extends SWRLBuiltin implements SPARQLSerializable {

    /**
     * Initializes the built-in.
     * @param arguments List of three or four arguments.
     * @throws IllegalArgumentException Thrown if there are not 3 or 4 arguments are passed.
     */
    public SWRLBSubstring(List<Object> arguments) {
        super(arguments);

        // Check that there are three arguments:
        if(arguments.size() != 3 && arguments.size() != 4) {
            throw new IllegalArgumentException("swrlb:substring expects three or four arguments. " + arguments.size() + " passed.");
        }
    }

    @Override
    public String asSPARQLFilterExpression() {
        if(getArguments().size() == 3) {

            return getArgumentAsFilterExpression(0) +
                    " = SUBSTR(" +
                    getArgumentAsFilterExpression(1) + ", "
                    + getArgumentAsFilterExpression(2) + ")";
        } else {
            return getArgumentAsFilterExpression(0) +
                    " = SUBSTR(" +
                    getArgumentAsFilterExpression(1) + ", "
                    + getArgumentAsFilterExpression(2) + ", "
                    + getArgumentAsFilterExpression(3) + ")";
        }
    }

    @Override
    public boolean evaluate(Bindings bindings) {
        Object value1 = getParameterValue(0, bindings);
        Object value2 = getParameterValue(1, bindings);
        Object value3 = getParameterValue(2, bindings);
        Object value4 = getParameterValue(3, bindings);

        if(value1 instanceof CharSequence && value2 instanceof CharSequence && value3 instanceof Integer) {
            CharSequence s1 = (CharSequence) value1;
            CharSequence s2 = (CharSequence) value2;
            int start = (Integer) value3;
            if(start >= 0 && start < s2.length() && value4 instanceof Integer) {
                int end = start + (Integer) value4;
                if(end <= s2.length()) {
                    return s1.equals(s2.subSequence(start, end));
                } else {
                    return false;
                }
            } else if(start >= 0 && start < s2.length()) {
                return s1.equals(s2.subSequence(start, s2.length()));
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
