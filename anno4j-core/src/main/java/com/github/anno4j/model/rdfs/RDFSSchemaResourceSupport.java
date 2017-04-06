package com.github.anno4j.model.rdfs;

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
    public void addLabel(CharSequence label) {
        Set<CharSequence> labels = new HashSet<>();

        if(this.getLabels() != null) {
            labels.addAll(this.getLabels());
        }

        labels.add(label);
        this.setLabels(labels);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addComment(CharSequence comment) {
        Set<CharSequence> comments = new HashSet<>();

        if(this.getComments() != null) {
            comments.addAll(getComments());
        }

        comments.add(comment);
        setComments(comments);
    }
}
