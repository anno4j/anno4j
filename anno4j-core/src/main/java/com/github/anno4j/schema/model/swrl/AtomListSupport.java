package com.github.anno4j.schema.model.swrl;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObjectSupport;
import com.github.anno4j.schema.model.rdfs.collections.RDFListSupport;
import org.openrdf.annotations.Precedes;
import org.openrdf.repository.object.behaviours.RDFSContainer;

import java.util.*;

/**
 * Support class for {@link AtomList}.
 */
@Partial
@Precedes({RDFSContainer.class, ResourceObjectSupport.class, RDFListSupport.class})
public abstract class AtomListSupport extends RDFListSupport implements AtomList {

    @Override
    public Set<Variable> getVariables() {
        Set<Variable> variables = new HashSet<>();
        Iterator<Object> i = iterator();
        while (i.hasNext()) {
            Object item = i.next();
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

        Iterator<Object> i = iterator();
        while (i.hasNext()) {
            Object item = i.next();
            // Variables are bound if they occur in a class or role atom:
            if(item instanceof ClassAtom || item instanceof DatavaluedPropertyAtom || item instanceof IndividualPropertyAtom) {
                bound.addAll(((Atom) item).getVariables());

            } else if(!(item instanceof BuiltinAtom)) { // All atoms must be an Anno4j ResourceObject:
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
        Iterator<Object> i = iterator();
        while (i.hasNext()) {
            Object item = i.next();
            if((item instanceof ClassAtom) || (item instanceof DatavaluedPropertyAtom) || (item instanceof IndividualPropertyAtom)) {
                classAndRoleAtoms.add((Atom) item);
            }
        }
        return classAndRoleAtoms;
    }

    @Override
    public Collection<BuiltinAtom> getBuiltInAtoms() {
        Collection<BuiltinAtom> builtinAtoms = new HashSet<>();
        Iterator<Object> i = iterator();
        while (i.hasNext()) {
            Object atom = i.next();
            if(atom instanceof BuiltinAtom) {
                builtinAtoms.add((BuiltinAtom) atom);
            }
        }
        return builtinAtoms;
    }

    @Override
    public List<Atom> asList() {
        List<Atom> list = new ArrayList<>(size());
        Iterator<Object> i = iterator();
        while (i.hasNext()) {
            Object item = i.next();
            if(item instanceof Atom) {
                list.add((Atom) item);
            }
        }
        return list;
    }
}
