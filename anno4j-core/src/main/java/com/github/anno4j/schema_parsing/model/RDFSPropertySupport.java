package com.github.anno4j.schema_parsing.model;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Manu on 07/11/16.
 */
@Partial
public abstract class RDFSPropertySupport extends RDFSSchemaResourceSupport implements RDFSProperty {

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
