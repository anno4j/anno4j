package com.github.anno4j.model.impl.collection;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.impl.ResourceObjectSupport;

import java.util.HashSet;
import java.util.Set;

/**
 * Support class for the AnnotationPage interface.
 */
@Partial
public abstract class AnnotationPageSupport extends ResourceObjectSupport implements AnnotationPage {

    /**
     * {@inheritDoc}
     */
    @Override
    public void addItem(Annotation annotation) {
        HashSet<Annotation> annotations = new HashSet<>();

        Set<Annotation> current = this.getItems();

        if(current != null) {
            annotations.addAll(current);
        }

        annotations.add(annotation);
        this.setItems(annotations);
    }
}
