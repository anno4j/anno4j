package com.github.anno4j.schema.model.swrl;

import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.SWRL;
import org.openrdf.annotations.Iri;

/**
 * Instances represent variables in SWRL rules.
 */
@Iri(SWRL.VARIABLE)
public interface Variable extends ResourceObject {

    /**
     * @return Returns an unique identifier for the variable. The identifier consists solely of alphanumerical
     * characters and the underscore.
     */
    String getVariableName();
}
