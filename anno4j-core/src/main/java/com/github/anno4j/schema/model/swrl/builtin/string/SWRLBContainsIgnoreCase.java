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
 * <a href="http://www.w3.org/2003/11/swrlb#containsIgnoreCase">http://www.w3.org/2003/11/swrlb#containsIgnoreCase</a> .
 * Satisfied iff the first argument contains the second argument (case ignored).
 * This built-in is SPARQL serializable.
 */
@SWRLBuiltinIri(SWRLB.CONTAINS_IGNORE_CASE)
public class SWRLBContainsIgnoreCase extends SWRLBuiltin implements SPARQLSerializable {

    /**
     * Initializes the built-in.
     * @param arguments List of arguments.
     * @throws IllegalArgumentException Thrown if the wrong number of arguments is passed.
     */
    public SWRLBContainsIgnoreCase(List<Object> arguments) {
        super(arguments);

        // Check that there are three arguments:
        if(arguments.size() != 2) {
            throw new IllegalArgumentException("swrlb:containsIgnoreCase expects two arguments. " + arguments.size() + " passed.");
        }
    }

    @Override
    public String asSPARQLFilterExpression() {
        return "CONTAINS(LCASE(" + getArgumentAsFilterExpression(0) + "), LCASE(" + getArgumentAsFilterExpression(1) + "))";
    }

    @Override
    public boolean evaluate(Bindings bindings) throws SWRLInferenceEngine.UnboundVariableException {
        Object value1 = getParameterValue(0, bindings);
        Object value2 = getParameterValue(1, bindings);

        if(value1 instanceof CharSequence && value2 instanceof CharSequence) {
            String s1 = "" + value1;
            String s2 = "" + value2;
            return s1.toLowerCase().contains(s2.toLowerCase());
        } else {
            return false;
        }
    }
}
