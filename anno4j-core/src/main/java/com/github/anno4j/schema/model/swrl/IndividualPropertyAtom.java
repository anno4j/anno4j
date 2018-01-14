package com.github.anno4j.schema.model.swrl;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.SWRL;
import org.openrdf.annotations.Iri;

/**
 * Instances are SWRL atoms that have a property of type {@code owl:ObjectProperty} as their predicate symbol and
 * two arguments. The atom is fulfilled if the first argument is related to the second argument, where both arguments
 * individuals.
 */
@Iri(SWRL.INDIVIDUAL_PROPERTY_ATOM)
public interface IndividualPropertyAtom extends ResourceObject, Atom {

    /**
     * @return Returns the property that is the predicate symbol of the atom.
     */
    @Iri(SWRL.PROPERTY_PREDICATE)
    ResourceObject getPropertyPredicate();

    /**
     * @param property The property that is the predicate symbol of the atom.
     */
    @Iri(SWRL.PROPERTY_PREDICATE)
    void setPropertyPredicate(ResourceObject property);

    /**
     * @return Returns the first argument of the atom.
     */
    @Iri(SWRL.ARGUMENT1)
    Object getArgument1();

    /**
     * @param argument1 The first argument of the atom.
     */
    @Iri(SWRL.ARGUMENT1)
    void setArgument1(Object argument1);

    /**
     * @return Returns the second argument of the atom.
     */
    @Iri(SWRL.ARGUMENT2)
    Object getArgument2();

    /**
     * @param argument2 The second argument of the atom.
     */
    @Iri(SWRL.ARGUMENT2)
    void setArgument2(Object argument2);
}
