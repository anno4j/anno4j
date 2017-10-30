package com.github.anno4j.schema.model.swrl;

import com.github.anno4j.model.impl.ResourceObject;

import java.util.Collection;

/**
 * Type of all atoms that may occur in SWRL rules.
 */
public interface Atom extends ResourceObject {

    /**
     * @return Returns all variables (s. {@link Variable} that
     * occur as a parameter of this atom.
     */
    Collection<Variable> getVariables();
}
