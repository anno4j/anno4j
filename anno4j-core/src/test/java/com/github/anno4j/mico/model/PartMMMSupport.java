package com.github.anno4j.mico.model;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.AnnotationSupport;
import com.github.anno4j.model.Target;

/**
 * Support class for the Part.
 */
@Partial
public abstract class PartMMMSupport extends AnnotationSupport implements PartMMM {

    @Override
    public void addTarget(Target target) {
        this.getTarget().add(target);
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public void addInput(ResourceMMM input) {
        this.getInputs().add(input);
    }
}
