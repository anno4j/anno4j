package com.github.anno4j.schema_parsing.model.owl;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObjectSupport;
import org.openrdf.repository.object.LangString;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Manu on 15/11/16.
 */
@Partial
public abstract class OWLSchemaResourceSupport extends ResourceObjectSupport implements OWLSchemaResource {

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
