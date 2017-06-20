package com.github.anno4j.schema_parsing.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.model.BuildableRDFSProperty;
import com.squareup.javapoet.MethodSpec;
import org.openrdf.repository.RepositoryException;

/**
 * Support class belonging to {@link com.github.anno4j.schema_parsing.model.BuildableRDFSProperty} for
 * generating support class setter method implementations with variable arguments.
 */
@Partial
public abstract class VarArgSetterImplementationSupport extends VarArgSetterSupport implements BuildableRDFSProperty {

    /**
     * {@inheritDoc}
     */
    @Override
    public MethodSpec buildVarArgSetterImplementation(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException {
        // Get the signature of a setter for this property:
        MethodSpec signature = buildSignature(domainClazz, config);

        if(signature != null) {
            // Add the setter implementation code to the signature:
            return addSetterImplementationCode(signature.toBuilder(), domainClazz, config, false)
                    .build();

        } else {
            return null;
        }
    }
}
