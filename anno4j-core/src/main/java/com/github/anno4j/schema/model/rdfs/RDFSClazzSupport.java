package com.github.anno4j.schema.model.rdfs;

import com.github.anno4j.annotations.Partial;

import java.util.HashSet;
import java.util.Set;

/**
 * Support class for the RDFSClazz interface.
 */
@Partial
public abstract class RDFSClazzSupport extends RDFSSchemaResourceSupport implements RDFSClazz {

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSuperclazz(RDFSClazz subClazz) {
        Set<RDFSClazz> superClazzes = new HashSet<>();

        if(this.getSuperclazzes() != null ) {
            superClazzes.addAll(this.getSuperclazzes());
        }

        superClazzes.add(subClazz);
        this.setSuperclazzes(superClazzes);
    }
}
