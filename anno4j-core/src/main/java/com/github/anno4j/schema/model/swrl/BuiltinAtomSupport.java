package com.github.anno4j.schema.model.swrl;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObjectSupport;
import com.github.anno4j.schema.model.swrl.builtin.Computation;
import com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltInService;
import com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltin;
import com.github.anno4j.schema.model.swrl.engine.SWRLInferenceEngine;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Support class for {@link BuiltinAtom}.
 */
@Partial
public abstract class BuiltinAtomSupport extends ResourceObjectSupport implements BuiltinAtom {

    @Override
    public SWRLBuiltin getBuiltin() throws InstantiationException {
        SWRLBuiltInService service = SWRLBuiltInService.getBuiltInService();
        return service.getBuiltIn(getBuiltinResource().getResourceAsString(), getArguments());
    }

    @Override
    public Variable getComputableVariable(AtomList atomList) throws InstantiationException, SWRLInferenceEngine.UnboundVariableException {
        // This atom must be capable of computing bindings:
        if(!(getBuiltin() instanceof Computation)) {
            return null;
        }
        // This atom must be contained in the given atom list:
        if(!atomList.contains(this)) {
            throw new IllegalArgumentException("The atom " + toString() + " isn't contained in the given atom list.");
        }


        Collection<Variable> freeVariables = atomList.getFreeVariables();

        // Iterate as long new variables can be determined bound by a built-in computation:
        boolean changed = true;
        while (changed) {
            changed = false;

            for (BuiltinAtom builtinAtom : atomList.getBuiltInAtoms()) {
                if (builtinAtom.getBuiltin() instanceof Computation) {
                    // Get the arguments of the computation that are still considered free:
                    Collection<Variable> variables = new ArrayList<>(builtinAtom.getVariables());
                    variables.retainAll(freeVariables);

                    // If there is only one free variable, it can be bound by the computation:
                    if(variables.size() == 1) {
                        // The variable is bound by this computation. Remove it from free variable set:
                        freeVariables.removeAll(variables);
                        changed = true;
                    }

                    if (builtinAtom.equals(this)) {
                        // If this atom is the searched one and it has only one free variable. This variable is the one that's computable:
                        if(variables.size() == 1) {
                            return variables.iterator().next();

                        } else if(variables.size() == 0) { // No free variables? No computation (atom is simply a boolean predicate)
                            return null;
                        }
                    }
                }
            }
        }

        // At least one built-in atom has more than one free variable:
        throw new SWRLInferenceEngine.UnboundVariableException("Variables " + freeVariables.toString()
                + " can neither be bound by a class atom, role atom or computable built-in atom.");
    }
}
