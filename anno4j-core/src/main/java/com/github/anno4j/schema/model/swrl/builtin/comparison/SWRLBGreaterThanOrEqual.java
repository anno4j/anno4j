package com.github.anno4j.schema.model.swrl.builtin.comparison;

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
 * <a href="http://www.w3.org/2003/11/swrlb#greaterThanOrEqual">http://www.w3.org/2003/11/swrlb#greaterThanOrEqual</a> .
 * It receives two arguments x, y. The predicate is true iff x > y holds.
 * This built-in is SPARQL serializable.
 */
@SWRLBuiltinIri(SWRLB.GREATER_THAN_OR_EQUAL)
public class SWRLBGreaterThanOrEqual extends SWRLBuiltin implements SPARQLSerializable {

    /**
     * Initializes the built-in.
     * @param arguments List of two arguments. Non-variable arguments must implement {@link Comparable}.
     * @throws IllegalArgumentException Thrown if less than two arguments are passed or any non-variable
     * argument isn't a {@link Comparable}.
     */
    public SWRLBGreaterThanOrEqual(List<Object> arguments) {
        super(arguments);

        if(arguments.size() != 2) {
            throw new IllegalArgumentException("swrlb:greaterThanOrEqual expects two arguments. " + arguments.size() + " passed.");
        }
        for(Object argument : arguments) {
            if(!(argument instanceof Variable || argument instanceof Comparable)) {
                throw new IllegalArgumentException("Arguments of swrlb:greaterThanOrEqual must implement java.lang.Comparable");
            }
        }
    }

    @Override
    public String asSPARQLFilterExpression() {
        return getArgumentAsFilterExpression(0) +
                " >= " +
                getArgumentAsFilterExpression(1);
    }

    @Override
    public boolean evaluate(Bindings bindings) throws SWRLInferenceEngine.UnboundVariableException {
        Object value1 = getParameterValue(0, bindings);
        Object value2 = getParameterValue(1, bindings);

        if (value1 instanceof Comparable && value2 instanceof Comparable && value1.getClass().equals(value2.getClass())) {
            return ((Comparable) value1).compareTo(value2) >= 0;
        } else if(value1 instanceof Number && value2 instanceof Number) {
            return ((Number) value1).doubleValue() >= ((Number) value2).doubleValue();
        } else {
            return false;
        }
    }
}
