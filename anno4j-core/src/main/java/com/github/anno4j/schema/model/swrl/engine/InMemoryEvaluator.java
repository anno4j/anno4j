package com.github.anno4j.schema.model.swrl.engine;

import com.github.anno4j.schema.model.swrl.Atom;
import com.github.anno4j.schema.model.swrl.AtomList;
import com.github.anno4j.schema.model.swrl.BuiltinAtom;
import com.github.anno4j.schema.model.swrl.Variable;
import com.github.anno4j.schema.model.swrl.builtin.Computation;
import com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltInService;
import com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltin;

import java.util.List;
import java.util.ListIterator;

/**
 * This evaluator executes suffices of execution plans (cf. {@link ExecutionPlanner}) that are not
 * SPARQL-serializable. The evaluator receives a set of candidate solutions resulting from the plans
 * prefix and executes the suffix reducing the candidates solutions to the total solution.
 * To do so the evaluator determines possible bindings for computable, free variables and checks for those
 * bindings whether all predicates in the suffix of the plan are satisfied.
 */
class InMemoryEvaluator {

    /**
     * This exception is thrown if an error occurs while executing a suffix of a plan in memory.
     */
    private class InMemoryEvaluationException extends SWRLException {

        public InMemoryEvaluationException() {
        }

        public InMemoryEvaluationException(String message) {
            super(message);
        }

        public InMemoryEvaluationException(Throwable cause) {
            super(cause);
        }
    }

    /**
     * The built-in service is used to instantiate implementations of SWRL built-ins for
     * built-in atoms.
     */
    private SWRLBuiltInService builtInService = SWRLBuiltInService.getBuiltInService();

    /**
     * Returns the suffix of the atom sequence {@code atoms} that is not SPARQL-serializable,
     * i.e. this method returns all atoms that are after the prefix determined by
     * {@link BodySPARQLEvaluator#longestSPARQLSerializablePrefix(AtomList)}.
     * @param atoms The sequence of atoms for which the suffix should be determined.
     * @return Returns the non-SPARQL-serializable suffix of the atom sequence.
     * @throws InstantiationException Thrown if a SWRL built-in implementation derived from a built-in atom in the sequence
     * could not be instantiated.
     */
    private List<Atom> getNonSPARQLSerializableSuffix(List<Atom> atoms) throws InstantiationException {
        List<Atom> sparqlSerializable = new BodySPARQLEvaluator().longestSPARQLSerializablePrefix(atoms);
        return atoms.subList(sparqlSerializable.size(), atoms.size());
    }

    /**
     * Determines bindings for computable variables occurring in the built-ins of the given plan based on the
     * initial bindings {@code bindings}. A variable is said to be computable by a builtin atom {@code b}
     * if it is the free and for all other variables {@code v} in {@code b} holds one of:
     * <ul>
     *     <li>{@code v} is bound by a class or role atom</li>
     *     <li>{@code v} is computable by another built-in atom {@code b'}</li>
     * </ul>
     * And addition the {@link SWRLBuiltin} implementation as derived by {@link SWRLBuiltInService}
     * for {@code b} must be a {@link Computation}.
     * @param plan The non-SPARQL-serializable suffix of a plan. This list must only contain {@link BuiltinAtom}s.
     * @param bindings Bindings for the non-computable variables.
     * @return Returns all possible solutions of the plan based on the initial bindings.
     * @throws SWRLException Thrown if an error occurs while determining the computable variable bindings for the plan.
     * @throws InstantiationException Thrown if any implementation of a SWRL built-in atom can't be instantiated.
     */
    private SolutionSet computeFreeVariables(List<Atom> plan, Bindings bindings) throws SWRLException, InstantiationException {
        // This function recursively computes bindings for all variables.
        // Base case: If there are no more atoms then all variables have been bound by previous calls.
        if(plan.isEmpty()) {
            return new SolutionSet(bindings);
        }

        // Recursive case:
        // If the current atom is a computation:
        if (plan.get(0) instanceof BuiltinAtom) {
            SWRLBuiltin builtin = builtInService.getBuiltIn((BuiltinAtom) plan.get(0));

            // We only need to compute something if the built-in has a free variable (that can be bound by it):
            boolean hasFreeVariable = false;
            for (Object arg : builtin.getArguments()) {
                if(arg instanceof Variable && !bindings.bound((Variable) arg)) {
                    hasFreeVariable = true;
                }
            }

            if(hasFreeVariable && builtin instanceof Computation) {
                Computation computation = (Computation) builtin;

                // Enrich the bindings by binding one more variable:
                SolutionSet enrichedSolutions = computation.solve(bindings);

                // Further enrich these bindings by recursively executing all following computations:
                SolutionSet fullBindings = new SolutionSet();
                for (Bindings enrichedBinding : enrichedSolutions) {
                    fullBindings.addAll(computeFreeVariables(plan.subList(1, plan.size()), enrichedBinding));
                }
                return fullBindings;

            } else { // If this is not a computation then we can't bind a variable in this step:
                return computeFreeVariables(plan.subList(1, plan.size()), bindings);
            }

        } else {
            throw new InMemoryEvaluationException("Non-builtin atom can't be executed in memory.");
        }
    }

    /**
     * Determines bindings for computable variables occurring in the built-ins of the given plan based on the
     * set of initial bindings {@code bindings}. A variable is said to be computable by a builtin atom {@code b}
     * if it is the free and for all other variables {@code v} in {@code b} holds one of:
     * <ul>
     *     <li>{@code v} is bound by a class or role atom</li>
     *     <li>{@code v} is computable by another built-in atom {@code b'}</li>
     * </ul>
     * And addition the {@link SWRLBuiltin} implementation as derived by {@link SWRLBuiltInService}
     * for {@code b} must be a {@link Computation}.
     * @param atoms The non-SPARQL-serializable suffix of a plan. This list must only contain {@link BuiltinAtom}s.
     * @param bindings Set of bindings for the non-computable variables.
     * @return Returns all possible solutions of the plan based on each of the initial bindings.
     * @throws SWRLException Thrown if an error occurs while determining the computable variable bindings for the plan.
     * @throws InstantiationException Thrown if any implementation of a SWRL built-in atom can't be instantiated.
     */
    private SolutionSet computeFreeVariables(List<Atom> atoms, SolutionSet bindings) throws SWRLException, InstantiationException {
        SolutionSet result = new SolutionSet();
        for (Bindings binding : bindings) {
            result.addAll(computeFreeVariables(atoms, binding));
        }
        return result;
    }

    /**
     * Tries to determine free variable bindings by computations based on each candidate binding provided.
     * The plan is checked for these bindings whether all atoms are satisfied. If this is the case then the augmented
     * binding combination is part of the solution set.
     * This methods only evaluates those {@link BuiltinAtom}s that are not evaluated by {@link BodySPARQLEvaluator}
     * and expects to get the bindings determined by it as candidate bindings.
     * @param plan The plan in order of dependencies.
     * @param candidateBindings The candidate binding on which basis to evaluate the non-SPARQL-serializable suffix.
     * @return Returns the subset of candidate bindings that are a solution for the whole plan.
     * @throws SWRLException Thrown if an error occurs while determining the solutions.
     * @throws InstantiationException Thrown if any implementation of a SWRL built-in couldn't be instantiated by
     * {@link SWRLBuiltInService}.
     */
    public SolutionSet evaluate(List<Atom> plan, SolutionSet candidateBindings) throws SWRLException, InstantiationException {
        // The solution set is a subset of the candidates. So if it's empty the solution is empty:
        if(candidateBindings.size() == 0) {
            return new SolutionSet();
        }

        // Get the part that can't be evaluated by the SPARQL evaluator:
        plan = getNonSPARQLSerializableSuffix(plan);

        // Determine possible bindings for the computable variables:
        SolutionSet bindings = computeFreeVariables(plan, candidateBindings);

        /*
        bindings now contains bindings for every variable.
        Determine which of them satisfies all atoms:
         */
        SolutionSet resultBindings = new SolutionSet();
        for(Bindings binding : bindings) {
            boolean match = true;

            ListIterator<Atom> planIterator = plan.listIterator();
            while (match && planIterator.hasNext()) {
                Atom atom = planIterator.next();
                if (atom instanceof BuiltinAtom) {
                    SWRLBuiltin builtin = builtInService.getBuiltIn((BuiltinAtom) atom);
                    match = builtin.evaluate(binding);
                }
            }

            if(match) {
                resultBindings.add(binding);
            }
        }

        return resultBindings;
    }
}
