package com.github.anno4j.schema.model.swrl;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObjectSupport;
import com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltInService;
import com.github.anno4j.schema.model.swrl.builtin.SWRLBuiltin;

@Partial
public abstract class BuiltinAtomSupport extends ResourceObjectSupport implements BuiltinAtom {

    @Override
    public SWRLBuiltin getBuiltin() throws InstantiationException {
        SWRLBuiltInService service = SWRLBuiltInService.getBuiltInService();
        return service.getBuiltIn(getBuiltinResource().getResourceAsString(), getArguments());
    }
}
