package com.github.anno4j.schema_parsing.model.owl;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Support class for the OWLObjectProperty interface, implementing additional behaviour.
 */
@Partial
public abstract class OWLObjectPropertySupport extends OWLSchemaResourceSupport implements OWLObjectProperty {

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSubProperty(ResourceObject subProperty) {
        Set<ResourceObject> subProperties = new HashSet<ResourceObject>();

        if(this.getSubProperties() != null) {
            subProperties.addAll(this.getSubProperties());
        }

        subProperties.add(subProperty);
        this.setSubProperties(subProperties);
    }
}
