package com.github.anno4j.mico.model;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.mico.namespace.MMM;
import com.github.anno4j.model.AnnotationSupport;
import com.github.anno4j.model.Target;
import org.openrdf.annotations.Iri;

import java.util.Set;

/**
 * Support class for the Part.
 */
@Partial
public abstract class PartMMMSupport extends AnnotationSupport implements PartMMM {

    @Override
    public Set<Target> getTarget() {
        return target;
    }

    @Override
    public void setTarget(Set<Target> target) {
        this.target = target;
    }

    @Iri(MMM.HAS_TARGET)
    private Set<Target> target;

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
