package com.github.anno4j.schema.model.swrl.engine;

import com.github.anno4j.schema.model.swrl.Variable;

import java.util.*;

/**
 * Instances of this class represent a set of bindings of
 * {@link com.github.anno4j.schema.model.swrl.Variable}s to a concrete value.
 */
public class Bindings {

    /**
     * Function associating variables with their bound values.
     */
    private Map<Variable, Object> bindings = new HashMap<>();

    /**
     * Initializes a empty binding set.
     */
    public Bindings() {}

    /**
     * Initializes the binding set with the values contained in the specified set.
     * @param bindings The initial bindings.
     */
    public Bindings(Map<Variable, Object> bindings) {
        this.bindings.putAll(bindings);
    }

    /**
     * Creates a copy of the given bindings.
     * @param bindings The bindings to copy.
     */
    public Bindings(Bindings bindings) {
        this.bindings.putAll(bindings.asMap());
    }

    /**
     * Copies the bindings in {@code bindings} and also binds {@code variable} to {@code value}.
     * @param bindings The bindings to copy.
     * @param variable The variable to bind in addition.
     * @param value The value to be bound.
     */
    public Bindings(Bindings bindings, Variable variable, Object value) {
        this(bindings);
        bind(variable, value);
    }

    /**
     * Returns the bound value of the variable {@code v}.
     * Use {@link #get(Variable)} if the type of the bound value is unknown.
     * @param type The type of the bound value.
     * @param v The variable for which to get a binding.
     * @param <T> The type of the bound value.
     * @return Returns the bound value or null if no value is bound for the specified variable.
     */
    public <T> T get(Class<T> type, Variable v) {
        if (bindings.containsKey(v)) {
            Object value = bindings.get(v);

            if(type.isInstance(value)) {
                return (T) value;
            } else {
                throw new IllegalArgumentException("The value of " + v + " (" + value + ") doesn't have type " + type.getName());
            }
        } else {
            return null;
        }
    }

    /**
     * Returns the bound value of the variable {@code v}.
     * @param v The variable for which to get a binding.
     * @return Returns the bound value or null if no value is bound for the specified variable.
     */
    public Object get(Variable v) {
        return bindings.get(v);
    }

    /**
     * Adds a binding for a variable to this binding set overwriting the previous binding (if any).
     * @param variable The variable to bind.
     * @param value The value to be bound.
     * @return Returns this instance to allow method chaining.
     */
    public Bindings bind(Variable variable, Object value) {
        bindings.put(variable, value);
        return this;
    }

    /**
     * @param v The variable to check.
     * @return Returns true iff the variable {@code v} is bound in this binding combination.
     */
    public boolean bound(Variable v) {
        return bindings.containsKey(v);
    }

    /**
     * @return Returns all variables bound by this binding set.
     */
    public Collection<Variable> variables() {
        return bindings.keySet();
    }

    /**
     * @return Returns all values bound to variables by this binding set.
     */
    public Collection<Object> values() {
        return bindings.values();
    }

    /**
     * @return Returns the bindings as an unmodifiable map.
     */
    public Map<Variable, Object> asMap() {
        return Collections.unmodifiableMap(bindings);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bindings)) return false;

        Bindings bindings1 = (Bindings) o;

        return bindings.equals(bindings1.bindings);
    }

    @Override
    public int hashCode() {
        return bindings.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("(");

        Iterator<Variable> i = variables().iterator();
        while (i.hasNext()) {
            Variable variable = i.next();

            builder.append(variable)
                    .append(" := ")
                    .append(get(variable));
            if(i.hasNext()) {
                builder.append(", ");
            }
        }
        return builder.append(")").toString();
    }
}
