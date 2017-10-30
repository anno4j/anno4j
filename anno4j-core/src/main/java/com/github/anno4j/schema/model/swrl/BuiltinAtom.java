package com.github.anno4j.schema.model.swrl;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.SWRL;
import com.github.anno4j.schema.model.rdfs.collections.RDFList;
import com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltin;
import org.openrdf.annotations.Iri;

@Iri(SWRL.BUILTIN_ATOM)
public interface BuiltinAtom extends Atom {

    @Iri(SWRL.BUILTIN)
    ResourceObject getBuiltinResource();

    @Iri(SWRL.BUILTIN)
    void setBuiltinResource(ResourceObject builtin);

    @Iri(SWRL.ARGUMENTS)
    RDFList getArguments();

    @Iri(SWRL.ARGUMENTS)
    void setArguments(RDFList arguments);

    SWRLBuiltin getBuiltin() throws InstantiationException;
}
