package com.github.anno4j.schema.model.swrl.builtin;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.schema.model.swrl.Variable;
import com.github.anno4j.schema.model.swrl.engine.Bindings;
import com.github.anno4j.schema.model.swrl.engine.SWRLInferenceEngine;

import java.util.List;

/**
 * SWRL Built-in implementations are concrete instantiations of built-in atoms found in SWRL rules.
 * Thus a built-in implementation is initialized with the arguments defined in the respective atom rule.
 * Every built-in is a predicate and can be evaluated given a set of variable bindings (s. {@link #evaluate(Bindings)}.
 * This is the base class of all SWRL built-in implementations and provides some convenience methods.
 * Concrete SWRL built-in implementations must have the {@link SWRLBuiltinIri} annotation and can be instantiated
 * by {@link SWRLBuiltInService}.
 */
public abstract class SWRLBuiltin {

    /**
     * The arguments the built-in has. Values can be {@link Variable}, {@link ResourceObject}
     * or any primitive datatype.
     */
    private List<Object> arguments;

    /**
     * @param arguments The arguments the built-in has.
     */
    public SWRLBuiltin(List<Object> arguments) {
        this.arguments = arguments;
    }

    /**
     * Evaluates the predicate based on the specified variable bindings.
     * @param bindings Bindings for variables.
     * @return Returns true iff the predicate is fulfilled for the given variable bindings.
     * @throws SWRLInferenceEngine.UnboundVariableException Thrown
     * if any of the variables in the arguments of this built-in instantiation is not bound by {@code bindings}.
     */
    public abstract boolean evaluate(Bindings bindings);

    /**
     * @return Returns the arguments of this built-in instantiation.
     */
    public List<Object> getArguments() {
        return arguments;
    }

    /**
     * Returns the argument at a certain index of this built-in instantiation.
     * @param index The index of the argument.
     * @return Returns the argument at the specified index.
     * @throws IndexOutOfBoundsException Thrown if {@code index} is not in range.
     */
    protected Object getArgument(int index) {
        return arguments.get(index);
    }

    /**
     * Sets the argument list of this built-in instantiation.
     * @param arguments The arguments of this built-in instantiation.
     */
    void setArguments(List<Object> arguments) {
        this.arguments = arguments;
    }

    /**
     * Transforms the argument at a certain position to its SPARQL FILTER equivalent.
     * Variable names are picked using {@link Variable#getVariableName()}.
     * @param index The index of the argument to transform.
     * @return Returns the SPARQL FILTER equivalent of the argument.
     * @throws IndexOutOfBoundsException Thrown if {@code index} is not in range.
     */
    protected String getArgumentAsFilterExpression(int index) {
        Object argument = arguments.get(index);

        if (argument instanceof Variable) {
            return "?" + ((Variable) argument).getVariableName();
        } else if (argument instanceof ResourceObject) {
            return "<" + ((ResourceObject) argument).getResourceAsString() + ">";
        } else if(argument instanceof Number) {
            return argument.toString();
        } else {
            return "\"" + argument.toString() + "\"";
        }
    }

    /**
     * Checks whether all arguments are numeric primitives. Throws an exception in case
     * of a non-numeric argument.
     * @param values The values to check.
     * @throws IllegalArgumentException Thrown if any argument is non-numeric.
     */
    protected void validateNumeric(Object... values) throws IllegalArgumentException {
        for(Object v : values) {
            if (!(v instanceof Number)) {
                throw new IllegalArgumentException("Parameter must be numeric. " + v + " given.");
            }
        }
    }

    /**
     * Returns the value of a parameter at the specified position given the specified bindings.
     * If the parameter at the position is a variable the corresponding value from {@code bindings}
     * is returned. Otherwise the fixed value of the parameter is returned.
     * @param index The index of the parameter.
     * @param bindings Bindings for the variables.
     * @return Returns the value of the parameter at the specified position given the bindings.
     * Returns null if the requested parameter is a variable which is not bound by {@code bindings}.
     */
    protected Object getParameterValue(int index, Bindings bindings) {
        if(index >= 0 && index < arguments.size()) {
            Object argument = arguments.get(index);

            if(argument instanceof Variable) {
                return bindings.get((Variable) argument);
            } else {
                return argument;
            }
        } else {
            return null;
        }
    }
}
