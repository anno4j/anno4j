package com.github.anno4j.schema.model.swrl;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.SWRL;
import com.github.anno4j.schema.model.rdfs.collections.RDFList;
import com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltin;
import com.github.anno4j.schema.model.swrl.engine.SWRLInferenceEngine;
import org.openrdf.annotations.Iri;

/**
 * A atom that has a SWRL built-in function as its predicate symbol.
 * Instances identify the SWRL built-in function via {@link #getBuiltinResource()}
 * and the arguments of the atom via {@link #getArguments()}.
 * For a list of all built-ins defined by the SWRL submission see:
 * <a href="https://www.w3.org/Submission/SWRL/#8">SWRL Built-Ins</a>
 */
@Iri(SWRL.BUILTIN_ATOM)
public interface BuiltinAtom extends Atom {

    /**
     * @return Returns the built-in function that is the predicate symbol of the atom.
     */
    @Iri(SWRL.BUILTIN)
    ResourceObject getBuiltinResource();

    /**
     * @param builtin The built-in function that is the predicate symbol of the atom.
     */
    @Iri(SWRL.BUILTIN)
    void setBuiltinResource(ResourceObject builtin);

    /**
     * @return Returns the arguments of the atom.
     */
    @Iri(SWRL.ARGUMENTS)
    RDFList getArguments();

    /**
     * @param arguments The arguments of the atom.
     */
    @Iri(SWRL.ARGUMENTS)
    void setArguments(RDFList arguments);

    /**
     * Creates an representation of the atom via {@link com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltInService}.
     * This instance can be used to evaluate and determine bindings.
     * @return Returns an instance of {@link SWRLBuiltin} that represents this built-in atom.
     * @throws InstantiationException Thrown if the built-in service can't instantiate the representation.
     */
    SWRLBuiltin getBuiltin() throws InstantiationException;

    /**
     * Returns the variable for which bindings can be determined by this built-in atom.
     * In the supported SWRL fragment bindings are computable for built-in atoms
     * with a certain built-in function (see {@link com.github.anno4j.schema.model.swrl.builtin.SPARQLSerializable})
     * having only one variable with undetermined bindings.
     * @param list The atom list in dependency order on basis of which to determine the variable.
     *             This atom must be part of this list.
     * @return Returns the variable for which bindings can be computed by this atom on basis of the given atom list.
     * Returns null if this atom can't compute bindings or all variable occuring in the atom are determined.
     * @throws IllegalArgumentException Thrown if this atom is not part of {@code list}.
     * @throws InstantiationException Thrown if the {@link SWRLBuiltin} object for this atom can't be instantiated
     * by {@link com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltInService}.
     * @throws SWRLInferenceEngine.UnboundVariableException Thrown if more than
     * one variable doesn't have determined bindings in any atom of {@code list}.
     */
    Variable getComputableVariable(AtomList list) throws InstantiationException, SWRLInferenceEngine.UnboundVariableException;
}
