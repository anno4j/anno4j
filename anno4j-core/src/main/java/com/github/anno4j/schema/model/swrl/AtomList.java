package com.github.anno4j.schema.model.swrl;

import com.github.anno4j.model.namespaces.SWRL;
import com.github.anno4j.schema.model.rdfs.collections.RDFList;
import org.openrdf.annotations.Iri;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * A atom list is an closed, ordered collection of atoms.
 * This type is used in SWRL rules (s. {@link Rule}) to represent conjunctions of atoms.
 * All objects contained in the list must implement {@link Atom}.
 */
@Iri(SWRL.ATOM_LIST)
public interface AtomList extends RDFList {

    /**
     * Returns all variables that occur in any of the atoms of this list.
     * @return Returns all variables.
     */
    Set<Variable> getVariables();

    /**
     * Returns all <b>bound</b> variables that occur in any of the atoms in this list.
     * A variable is said to be bound in this context if it occurs as the parameter
     * of at least one of one of the following types:
     * <ul>
     *     <li>{@link ClassAtom}: The variable represents an instance of a class.</li>
     *     <li>{@link IndividualPropertyAtom}: The variable represents the subject or object of object-relation.</li>
     *     <li>{@link DatavaluedPropertyAtom}: The variable represents the subject of a datavalued relation.</li>
     * </ul>
     * If a variable is not bound in the above sense, it's said to be free (s. {@link #getFreeVariables()}.
     * @return Returns all bound variables of this atom list.
     */
    Set<Variable> getBoundVariables();

    /**
     * Returns all free variables of this atom list. A variable is free if it's not bound by a
     * class or role atom (s. {@link #getBoundVariables()}.
     * @return Returns all free variables of this atom list.
     */
    Set<Variable> getFreeVariables();

    /**
     * Returns all class and role atoms of this atom list.
     * These may be of the following type:
     * <ul>
     *     <li>{@link ClassAtom}</li>
     *     <li>{@link IndividualPropertyAtom}</li>
     *     <li>{@link DatavaluedPropertyAtom}</li>
     * </ul>
     * @return Returns all class or role atoms.
     */
    Collection<Atom> getClassAndRoleAtoms();

    /**
     * Returns the built-in atoms of this atom list.
     * @return Returns the built-in atoms.
     */
    Collection<BuiltinAtom> getBuiltInAtoms();

    /**
     * Returns this atom list as a list of types {@link Atom}.
     * @return The converted list.
     */
    List<Atom> asList();
}
