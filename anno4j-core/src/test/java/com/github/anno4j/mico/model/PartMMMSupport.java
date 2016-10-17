package com.github.anno4j.mico.model;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.mico.namespace.MMM;
import com.github.anno4j.model.AnnotationSupport;
import com.github.anno4j.model.Target;
import org.openrdf.annotations.Iri;
import org.openrdf.annotations.Precedes;

import java.util.Set;

/**
 * Support class for the Part.
 */
@Partial
@Precedes(AnnotationSupport.class)
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

    @Iri(MMM.HAS_TARGET)
    private Set<Target> targets;

    @Override
    public Set<Target> getTarget() {
        return targets;
    }

    @Override
    public void setTarget(Set<Target> targets) {
        this.targets = targets;
    }
}
