package com.github.anno4j.schema_parsing.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.model.ExtendedRDFSClazz;
import com.github.anno4j.schema_parsing.model.ExtendedRDFSProperty;
import com.github.anno4j.schema_parsing.validation.Validator;
import com.squareup.javapoet.*;

/**
 * Support class (of {@link ExtendedRDFSProperty}) for generating
 * addAll* methods of support classes.
 */
@Partial
public abstract class AdderAllImplementationSupport extends AdderAllSupport implements ExtendedRDFSProperty {

    @Override
    public MethodSpec buildAdderAllImplementation(OntGenerationConfig config) {
        // Get the signature of a adder for this property:
        MethodSpec signature = buildSignature(config);

        if(signature != null) {
            // Override annotation of the method:
            AnnotationSpec overrideAnnotation = AnnotationSpec.builder(Override.class).build();

            MethodSpec.Builder adderBuilder = signature.toBuilder();

            ExtendedRDFSClazz range = findSingleRangeClazz();
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

            // Generate code for adding also to superproperties:
            adderBuilder.addComment("Add values also to superproperties:")
                        .beginControlFlow("if(!$N.isEmpty())", param);
            for (ExtendedRDFSProperty superProperty : getSuperproperties()) {
                String superAdderAllName = superProperty.buildAdderAll(config).name;
                adderBuilder.addStatement("this._invokeResourceObjectMethodIfExists($S, $N)", superAdderAllName, param);
            }
            adderBuilder.endControlFlow(); // End if(!param.isEmpty())

            // Get the annotated field for this property:
            FieldSpec field = buildAnnotatedField(config);

            return adderBuilder.addAnnotation(overrideAnnotation)
                    .addStatement("this.$N.addAll(values)", field)
                    .build();

        } else {
            return null;
        }
    }
}
