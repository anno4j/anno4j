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
 * <a href="http://www.w3.org/2003/11/swrlb#lowerCase">http://www.w3.org/2003/11/swrlb#lowerCase</a> .
 * Satisfied iff the first argument is equal to the lower-cased value of the second argument.
 * This built-in is SPARQL serializable.
 */
@SWRLBuiltinIri(SWRLB.LOWERCASE)
public class SWRLBLowerCase extends SWRLBuiltin implements SPARQLSerializable {

    /**
     * Initializes the built-in.
     * @param arguments List of arguments.
     * @throws IllegalArgumentException Thrown if the wrong number of arguments is passed.
     */
    public SWRLBLowerCase(List<Object> arguments) {
        super(arguments);

        // Check that there are three arguments:
        if(arguments.size() != 2) {
            throw new IllegalArgumentException("swrlb:lowerCase expects two arguments. " + arguments.size() + " passed.");
        }
    }

    @Override
    public String asSPARQLFilterExpression() {
        return getArgumentAsFilterExpression(0) + " = LCASE(" + getArgumentAsFilterExpression(1) + ")";
    }

    @Override
    public boolean evaluate(Bindings bindings) throws SWRLInferenceEngine.UnboundVariableException {
        Object value1 = getParameterValue(0, bindings);
        Object value2 = getParameterValue(1, bindings);

        if(value1 instanceof CharSequence && value2 instanceof CharSequence) {
            String s2 = "" + value2;
            return value1.equals(s2.toLowerCase());
        } else {
            return false;
        }
    }
}
