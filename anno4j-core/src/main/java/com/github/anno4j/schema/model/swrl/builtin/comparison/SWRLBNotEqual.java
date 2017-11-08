package com.github.anno4j.schema.model.swrl.builtin.comparison;

import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.swrl.builtin.SPARQLSerializable;
import com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltin;
import com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltinIri;
import com.github.anno4j.schema.model.swrl.engine.Bindings;
import com.github.anno4j.schema.model.swrl.engine.SWRLInferenceEngine;

import java.util.List;

/**
 * Implementation of the SWRL Built-in function
 * <a href="http://www.w3.org/2003/11/swrlb#notEqual">http://www.w3.org/2003/11/swrlb#notEqual</a> .
 * It receives two arguments x, y. The predicate is true iff x != y holds.
 * This built-in is SPARQL serializable.
 */
@SWRLBuiltinIri(SWRLB.NOT_EQUAL)
public class SWRLBNotEqual extends SWRLBuiltin implements SPARQLSerializable {

    /**
     * Initializes the built-in instantiation.
     * @param arguments List of two arguments.
     * @throws IllegalArgumentException Thrown if less than two arguments are passed.
     */
    public SWRLBNotEqual(List<Object> arguments) {
        super(arguments);

        if(arguments.size() != 2) {
            throw new IllegalArgumentException("swrlb:equal expects two arguments. " + arguments.size() + " passed.");
        }
    }

    @Override
    public String asSPARQLFilterExpression() {
        return new StringBuilder()
                .append(getArgumentAsFilterExpression(0))
                .append(" != ")
                .append(getArgumentAsFilterExpression(1))
                .toString();
    }

    @Override
    public boolean evaluate(Bindings bindings) throws SWRLInferenceEngine.UnboundVariableException {
        Object value1 = getParameterValue(0, bindings);
        Object value2 = getParameterValue(1, bindings);

        if(value1 instanceof Number && value2 instanceof Number) {
            return ((Number) value1).doubleValue() != ((Number) value2).doubleValue();
        } else {
            return !value1.equals(value2);
        }
    }
}
