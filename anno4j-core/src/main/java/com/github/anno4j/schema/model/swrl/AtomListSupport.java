package com.github.anno4j.schema.model.swrl;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObjectSupport;

import java.util.*;

/**
 * Support class for {@link AtomList}.
 */
@Partial
public abstract class AtomListSupport extends ResourceObjectSupport implements AtomList {

    @Override
    public Set<Variable> getVariables() {
        Set<Variable> variables = new HashSet<>();
        for(Object item : this) {
            if(item instanceof Atom) {
                variables.addAll(((Atom) item).getVariables());
            } else {
                throw new IllegalArgumentException("Atom lists must contain atoms.");
            }
        }
        return variables;
    }

    @Override
    public Set<Variable> getBoundVariables() {
        Set<Variable> bound = new HashSet<>();

        for(Object item : this) {
            // Variables are bound if they occur in a class or role atom:
            if(item instanceof ClassAtom || item instanceof DatavaluedPropertyAtom || item instanceof IndividualPropertyAtom) {
                bound.addAll(((Atom) item).getVariables());

            } else { // All atoms must be an Anno4j ResourceObject:
                throw new IllegalArgumentException("Atom lists must contain atoms.");
            }
        }

        return bound;
    }

    @Override
    public Set<Variable> getFreeVariables() {
        // Get all variables:
        Set<Variable> variables = getVariables();

        // Remove bound variables. Free variables remain:
        variables.removeAll(getBoundVariables());

        return variables;
    }

    @Override
    public Collection<Atom> getClassAndRoleAtoms() {
        Collection<Atom> classAndRoleAtoms = new HashSet<>();
        for(Object item : this) {
            if((item instanceof ClassAtom) || (item instanceof DatavaluedPropertyAtom) || (item instanceof IndividualPropertyAtom)) {
                classAndRoleAtoms.add((Atom) item);
            }
        }
        return classAndRoleAtoms;
    }

    @Override
    public Collection<BuiltinAtom> getBuiltInAtoms() {
        Collection<BuiltinAtom> builtinAtoms = new HashSet<>();
        for(Object atom : this) {
            if(atom instanceof BuiltinAtom) {
                builtinAtoms.add((BuiltinAtom) atom);
            }
        }
        return builtinAtoms;
    }

    @Override
    public List<Atom> asList() {
        List<Atom> list = new ArrayList<>(size());
        for(Object item : this) {
            if(item instanceof Atom) {
                list.add((Atom) item);
            }
        }
        return list;
    }
}
