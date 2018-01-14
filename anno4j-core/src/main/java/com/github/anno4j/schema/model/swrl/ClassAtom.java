package com.github.anno4j.schema.model.swrl;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.SWRL;
import org.openrdf.annotations.Iri;

/**
 * A class atom is a SWRL atom with a OWL class or class description {@code C} as predicate symbol and a single argument.
 * The atom is fulfilled iff the argument is an instance of the class {@code C}.
 */
@Iri(SWRL.CLASS_ATOM)
public interface ClassAtom extends ResourceObject, Atom {

    /**
     * @return Returns the class or class description.
     */
    @Iri(SWRL.CLASS_PREDICATE)
    ResourceObject getClazzPredicate();

    /**
     * @param clazz The class or class description.
     */
    @Iri(SWRL.CLASS_PREDICATE)
    void setClazzPredicate(ResourceObject clazz);

    /**
     * @return Returns the single argument of the atom.
     */
    @Iri(SWRL.ARGUMENT1)
    ResourceObject getArgument1();

    /**
     * @param argument1 The single argument of the atom.
     */
    @Iri(SWRL.ARGUMENT1)
    void setArgument1(ResourceObject argument1);
}
