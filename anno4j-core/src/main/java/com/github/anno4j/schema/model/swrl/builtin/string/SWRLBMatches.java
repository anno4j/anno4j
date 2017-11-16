package com.github.anno4j.schema.model.swrl.builtin.string;

import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.swrl.builtin.SPARQLSerializable;
import com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltin;
import com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltinIri;
import com.github.anno4j.schema.model.swrl.engine.Bindings;
import com.github.anno4j.schema.model.swrl.engine.SWRLInferenceEngine;

import java.util.List;
import java.util.regex.PatternSyntaxException;

/**
 * Implementation of the SWRL Built-in function
 * <a href="http://www.w3.org/2003/11/swrlb#matches">http://www.w3.org/2003/11/swrlb#matches</a> .
 * Satisfied iff the first argument matches the regular expression the second argument.
 * This built-in is SPARQL serializable.
 */
@SWRLBuiltinIri(SWRLB.MATCHES)
public class SWRLBMatches extends SWRLBuiltin implements SPARQLSerializable {

    /**
     * Initializes the built-in.
     * @param arguments List of arguments.
     * @throws IllegalArgumentException Thrown if the wrong number of arguments is passed.
     */
    public SWRLBMatches(List<Object> arguments) {
        super(arguments);

        // Check that there are three arguments:
        if(arguments.size() != 2) {
            throw new IllegalArgumentException("swrlb:matches expects two arguments. " + arguments.size() + " passed.");
        }
    }

    @Override
    public String asSPARQLFilterExpression() {
        return "REGEX(" + getArgumentAsFilterExpression(0) + ", " + getArgumentAsFilterExpression(1) + ")";
    }

    @Override
    public boolean evaluate(Bindings bindings) {
        Object value1 = getParameterValue(0, bindings);
        Object value2 = getParameterValue(1, bindings);

        if(value1 instanceof CharSequence && value2 instanceof CharSequence) {
            String s1 = "" + value1;
            String s2 = "" + value2;
            try {
                return s1.matches(s2);
            } catch (PatternSyntaxException ignored) {
                return false;
            }
        } else {
            return false;
        }
    }
}
