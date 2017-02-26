package com.github.anno4j.rdfs_parser.model;

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
    public void addSubClazz(RDFSClazz subClazz) {
        Set<RDFSClazz> subClazzes = new HashSet<RDFSClazz>();

        if(this.getSubClazzes() != null ) {
            subClazzes.addAll(this.getSubClazzes());
        }

        subClazzes.add(subClazz);
        this.setSubClazzes(subClazzes);

    }
}
