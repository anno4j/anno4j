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
public class SolutionSet implements Iterable<Bindings> {

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
        solutions.add(enriched);
    }

    /**
     * Adds all bindings in {@code solutions} to this solution set.
     * @param solutions The bindings to add.
     */
    public void addAll(SolutionSet solutions) {
        this.solutions.addAll(solutions.getBindings());
    }

    /**
     * Removes all bindings from this solution set that are both in this set and in {@code solutions}.
     * @param solutions The solutions to remove (if present).
     */
    public void removeAll(SolutionSet solutions) {
        this.solutions.removeAll(solutions.getBindings());
    }

    /**
     * Removes all bindings from this solution set that have (maybe among others) the same variable bindings
     * as one binding in {@code removeBindings}.
     * @param removeBindings The solutions to remove if they appear as part of a solution in this set.
     */
    public void removeAllContaining(SolutionSet removeBindings) {
        Collection<Bindings> toRemove = new HashSet<>();
        for(Bindings binding : this.solutions) {
            for(Bindings remove: removeBindings) {
                if(binding.contains(remove)) {
                    toRemove.add(binding);
                }
            }
        }
        solutions.removeAll(toRemove);
    }

    /**
     * Returns all variables that are bound in any binding of this solution set.
     * @return Returns the set of all variables.
     */
    public Collection<Variable> getVariables() {
        Collection<Variable> variables = new HashSet<>();
        for (Bindings solution : solutions) {
            variables.addAll(solution.variables());
        }
        return variables;
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
