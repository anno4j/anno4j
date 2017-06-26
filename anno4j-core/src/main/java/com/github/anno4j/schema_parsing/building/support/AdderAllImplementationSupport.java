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

/**
 * Support class (of {@link BuildableRDFSProperty}) for generating
 * addAll* methods of support classes.
 */
@Partial
public abstract class AdderAllImplementationSupport extends AdderAllSupport implements BuildableRDFSProperty {

    @Override
    public MethodSpec buildAdderAllImplementation(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException {
        // Get the signature of a adder for this property:
        MethodSpec signature = buildSignature(domainClazz, config);

        if(signature != null) {
            // Override annotation of the method:
            AnnotationSpec overrideAnnotation = AnnotationSpec.builder(Override.class).build();

            MethodSpec.Builder adderBuilder = signature.toBuilder();

            BuildableRDFSClazz range = findSingleRangeClazz();
            ClassName rangeClassName = range.getJavaPoetClassName(config);
            ParameterSpec param = signature.parameters.get(0);
            ParameterSpec current = ParameterSpec.builder(rangeClassName, "current").build();

            // Add validation code:
            for (Validator validator : config.getValidators()) {
                if(validator.isValueSpaceConstrained(range)) {
                    adderBuilder.beginControlFlow("for($T $N : $N)", rangeClassName, current, param);
                    validator.addValueSpaceCheck(adderBuilder, current, range);
                    adderBuilder.endControlFlow();
                }
            }

            // Get the annotated field for this property:
            FieldSpec field = buildAnnotatedField(domainClazz, config);

            return adderBuilder.addAnnotation(overrideAnnotation)
                    .addStatement("this.$N.addAll(values)", field)
                    .addStatement("sanitizeSchema($S)", getResourceAsString()) // Handles adding to superproperties
                    .build();

        } else {
            return null;
        }
    }
}
