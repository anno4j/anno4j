package com.github.anno4j.model.impl.collection;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObjectSupport;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Manu on 20/07/16.
 */
@Partial
public abstract class AnnotationCollectionSupport extends ResourceObjectSupport implements AnnotationCollection {

    /**
     * {@inheritDoc}
     */
    @Override
    public void addLabel(String label) {
        HashSet<String> labels = new HashSet<>();

        Set<String> current = this.getLabels();

        if(current != null) {
            labels.addAll(current);
        }

        labels.add(label);
        this.setLabels(labels);
    }

    @Override
    public int getTotal() {
        int total = 0;
        AnnotationPage page = this.getFirstPage();

        while(page != null) {
            total += page.getItems().size();
            page = page.getNext();
        }

        return total;
    }
}
