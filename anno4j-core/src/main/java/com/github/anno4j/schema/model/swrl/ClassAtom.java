package com.github.anno4j.schema.model.swrl;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.SWRL;
import org.openrdf.annotations.Iri;

@Iri(SWRL.CLASS_ATOM)
public interface ClassAtom extends ResourceObject, Atom {

    @Iri(SWRL.CLASS_PREDICATE)
    ResourceObject getClazzPredicate();

    @Iri(SWRL.CLASS_PREDICATE)
    void setClazzPredicate(ResourceObject clazz);

    @Iri(SWRL.ARGUMENT1)
    ResourceObject getArgument1();

    @Iri(SWRL.ARGUMENT1)
    void setArgument1(ResourceObject argument1);
}
