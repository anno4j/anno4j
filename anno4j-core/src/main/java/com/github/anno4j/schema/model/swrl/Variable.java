package com.github.anno4j.schema.model.swrl;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.SWRL;
import org.openrdf.annotations.Iri;

@Iri(SWRL.VARIABLE)
public interface Variable extends ResourceObject {

    String getVariableName();
}
