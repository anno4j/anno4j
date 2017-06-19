package com.github.anno4j.schema_parsing.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.model.BuildableRDFSProperty;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import org.openrdf.repository.RepositoryException;

import javax.lang.model.element.Modifier;

/**
 * Support class belonging to {@link BuildableRDFSProperty} that adds functionality
 * to generate a JavaPoet specification for a setter method with variable arguments.
 */
@Partial
public abstract class VarArgSetterSupport extends SetterBuildingSupport implements BuildableRDFSProperty {

    @Override
    MethodSpec buildSignature(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException {
        if (getRanges() != null) {
            MethodSpec.Builder setter = buildParameterlessSetterSignature(domainClazz, config);

            // Get the vararg parameter type:
            TypeName paramType = ArrayTypeName.of(getParameterType(config, false));


            return setter.addParameter(paramType, "values")
                         .varargs()
                         .build();
        } else {
            return null;
        }
    }

    @Override
    public MethodSpec buildVarArgSetter(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException {
        // Get the signature of the vararg setter for this property:
        MethodSpec signature = buildSignature(domainClazz, config);

        if (signature != null) {
            // Add an abstract modifier to the signature:
            return signature.toBuilder()
                    .addModifiers(Modifier.ABSTRACT)
                    .build();

        } else {
            return null;
        }
    }
}
