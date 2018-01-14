package com.github.anno4j.schema.model.swrl;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObjectSupport;

/**
 * Support class for {@link Variable}.
 */
@Partial
public abstract class VariableSupport extends ResourceObjectSupport implements Variable {

    @Override
    public String getVariableName() {
        int hc = hashCode(); // Use the hash code as an unique identifier.
        if (hc >= 0) {
            return "var" + hc;
        } else {
            return "var_" + (-1)*hc;
        }
    }
}
