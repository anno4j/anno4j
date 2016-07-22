package com.github.anno4j.model;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObjectSupport;

import java.util.HashSet;

/**
 * Support class for the Selector interface.
 */
@Partial
public abstract class SelectorSupport extends ResourceObjectSupport implements Selector {

    /**
     * {@inheritDoc}
     */
    @Override
    public void addRefinedSelector(Selector selector) {
        HashSet<Selector> selectors = new HashSet<>();

        if(this.getRefinedSelectors() != null) {
            selectors.addAll(this.getRefinedSelectors());
        }

        selectors.add(selector);
        this.setRefinedSelectors(selectors);
    }
}
