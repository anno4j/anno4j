package com.github.anno4j.model;

import com.github.anno4j.persistence.annotation.Partial;

import java.util.HashSet;

@Partial
public abstract class AnnotationSupport implements Annotation {

    @Override
    public void addTarget(Target target) {
        if(this.getTarget() == null) {
            this.setTarget(new HashSet<Target>());
        }

        this.getTarget().add(target);
    }
}
