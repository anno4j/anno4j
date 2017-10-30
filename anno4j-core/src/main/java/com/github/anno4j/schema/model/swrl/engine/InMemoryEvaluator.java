package com.github.anno4j.schema.model.swrl.engine;

import com.github.anno4j.schema.model.swrl.Atom;
import com.github.anno4j.schema.model.swrl.BuiltinAtom;
import com.github.anno4j.schema.model.swrl.builtin.Computation;
import com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltInService;
import com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltin;

import java.util.List;
import java.util.ListIterator;

class InMemoryEvaluator {

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

    private SWRLBuiltInService builtInService = SWRLBuiltInService.getBuiltInService();

    private List<Atom> getNonSPARQLSerializableSuffix(List<Atom> atoms) {
        List<Atom> sparqlSerializable = new BodySPARQLEvaluator().longestSPARQLSerializablePrefix(atoms);
        return atoms.subList(sparqlSerializable.size(), atoms.size());
    }

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

            if(builtin instanceof Computation) {
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

    private SolutionSet computeFreeVariables(List<Atom> atoms, SolutionSet bindings) throws SWRLException, InstantiationException {
        SolutionSet result = new SolutionSet();
        for (Bindings binding : bindings) {
            result.addAll(computeFreeVariables(atoms, binding));
        }
        return result;
    }

    public SolutionSet evaluate(List<Atom> plan, SolutionSet candidateBindings) throws SWRLException, InstantiationException {
        SolutionSet resultBindings = new SolutionSet();

        plan = getNonSPARQLSerializableSuffix(plan);

        SolutionSet bindings = computeFreeVariables(plan, candidateBindings);

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
