package com.github.anno4j.schema_parsing.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.github.anno4j.schema.model.rdfs.RDFSProperty;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.model.BuildableRDFSClazz;
import com.github.anno4j.schema_parsing.model.BuildableRDFSProperty;
import com.github.anno4j.schema_parsing.validation.Validator;
import com.squareup.javapoet.*;
import org.openrdf.repository.RepositoryException;

import java.util.HashSet;

/**
 * Support class (of {@link BuildableRDFSProperty}) for generating
 * setter methods of support classes.
 */
@Partial
public abstract class SetterImplementationSupport extends SetterSupport implements BuildableRDFSProperty {

    @Override
    public MethodSpec buildSetterImplementation(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException {
        // Get the signature of a setter for this property:
        MethodSpec signature = buildSignature(domainClazz, config);

        if(signature != null) {
            // Add the setter implementation code to the signature:
            return addSetterImplementationCode(signature.toBuilder(), domainClazz, config, true)
                    .build();

        } else {
            return null;
        }
    }
}
