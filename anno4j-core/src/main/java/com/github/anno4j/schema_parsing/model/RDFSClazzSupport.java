package com.github.anno4j.schema_parsing.model;

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
    public void addSubClazz(String subClazz) {
        Set<String> subClazzes = new HashSet<String>();

        if(this.getSubClazzes() != null ) {
            subClazzes.addAll(this.getSubClazzes());
        }

        subClazzes.add(subClazz);
        this.setSubClazzes(subClazzes);

    }
}
