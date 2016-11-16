package com.github.anno4j.schema_parsing.model.rdfs;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObjectSupport;

import java.util.HashSet;
import java.util.Set;

/**
 * Support class for the RDFSSchemaResource interface.
 */
@Partial
public abstract class RDFSSchemaResourceSupport extends ResourceObjectSupport implements RDFSSchemaResource {

    /**
     * {@inheritDoc}
     */
    @Override
    public void addLabel(String label) {
        Set<String> labels = new HashSet<String>();

        if(this.getLabels() != null) {
            labels.addAll(this.getLabels());
        }

        labels.add(label);
        this.setLabels(labels);
    }
}
