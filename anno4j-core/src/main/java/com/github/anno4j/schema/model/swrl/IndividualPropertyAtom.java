package com.github.anno4j.schema.model.swrl;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.SWRL;
import org.openrdf.annotations.Iri;

@Iri(SWRL.INDIVIDUAL_PROPERTY_ATOM)
public interface IndividualPropertyAtom extends ResourceObject, Atom {

    @Iri(SWRL.PROPERTY_PREDICATE)
    ResourceObject getPropertyPredicate();

    @Iri(SWRL.PROPERTY_PREDICATE)
    void setPropertyPredicate(ResourceObject property);

    @Iri(SWRL.ARGUMENT1)
    Object getArgument1();

    @Iri(SWRL.ARGUMENT1)
    void setArgument1(Object argument1);

    @Iri(SWRL.ARGUMENT2)
    Object getArgument2();

    @Iri(SWRL.ARGUMENT2)
    void setArgument2(Object argument2);
}
