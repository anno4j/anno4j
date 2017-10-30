package com.github.anno4j.schema.model.swrl.engine;

import com.github.anno4j.schema.model.swrl.Variable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A collection of {@link Bindings}. Thus every element of contains
 * a possible solution binding for a problem.
 */
class SolutionSet implements Iterable<Bindings> {

    /**
     * The different solution bindings.
     */
    private Collection<Bindings> solutions = new HashSet<>();

    /**
     * Initializes an empty solution set.
     */
    public SolutionSet() {}

    /**
     * Initializes with a single solution.
     * @param solution The single solution.
     */
    public SolutionSet(Bindings solution) {
        solutions.add(solution);
    }

    /**
     * Initializes a solution set with the given bindings.
     * @param solutions The initial solutions.
     */
    public SolutionSet(Collection<Bindings> solutions) {
        this.solutions.addAll(solutions);
    }

    /**
     * Adds a solution binding combination to the set.
     * @param solution The solution to add.
     */
    public void add(Bindings solution) {
        solutions.add(solution);
    }

    /**
     * Adds a solution containing all bindings in {@code solution} and {@code variable}
     * bound to {@code value}.
     * @param solution Bindings for variables being part of the solution.
     * @param variable The variable that will be additionally bound.
     * @param value The value to which the additionally bound variable is bound.
     */
    public void add(Bindings solution, Variable variable, Object value) {
        Bindings enriched = new Bindings(solution);
        enriched.bind(variable, value);
    }

    public void addAll(SolutionSet solutions) {
        this.solutions.addAll(solutions.getBindings());
    }

    @Override
    public Iterator<Bindings> iterator() {
        return solutions.iterator();
    }

    public int size() {
        return solutions.size();
    }

    public Collection<Bindings> getBindings() {
        return Collections.unmodifiableCollection(solutions);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SolutionSet)) return false;

        SolutionSet that = (SolutionSet) o;

        return solutions != null ? solutions.equals(that.solutions) : that.solutions == null;
    }

    @Override
    public int hashCode() {
        return solutions != null ? solutions.hashCode() : 0;
    }

    @Override
    public String toString() {
        return solutions.toString();
    }
}
