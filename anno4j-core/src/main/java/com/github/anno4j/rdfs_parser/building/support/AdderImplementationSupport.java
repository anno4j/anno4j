package com.github.anno4j.rdfs_parser.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.rdfs_parser.building.OntGenerationConfig;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSClazz;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSProperty;
import com.github.anno4j.rdfs_parser.validation.Validator;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;

/**
 * Support class (of {@link ExtendedRDFSProperty}) for generating add-methods for
 * the corresponding property.
 */
@Partial
public abstract class AdderImplementationSupport extends AdderSupport implements ExtendedRDFSProperty {

    @Override
    public MethodSpec buildAdderImplementation(OntGenerationConfig config) {
        // Get the signature of a add* method for this property:
        MethodSpec adder = buildSignature(config);
        if(adder != null) {
            MethodSpec.Builder adderBuilder = adder.toBuilder();

            // Implementations in support classes have the @Override annotation:
            AnnotationSpec overrideAnnotation = AnnotationSpec.builder(Override.class)
                    .build();

            // Add validation code:
            ExtendedRDFSClazz range = findSingleRangeClazz();
            ParameterSpec param = adder.parameters.get(0);
            for (Validator validator : config.getValidators()) {
                if (validator.isValueSpaceConstrained(range)) {
                    validator.addValueSpaceCheck(adderBuilder, param, range);
                }
            }

            // Generate code for adding also to superproperties:
            for (ExtendedRDFSProperty superProperty : getSuperproperties()) {
                String superAdderName = superProperty.buildAdder(config).name;
                adderBuilder.addStatement("this._invokeResourceObjectMethodIfExists($S, $N)", superAdderName, param);
            }

            // Get the annotated field for this property:
            FieldSpec field = buildAnnotatedField(config);

            // Build the adders method specification:
            return adderBuilder.addAnnotation(overrideAnnotation)
                                .addStatement("this.$N.add($N)", field, param) // Actual adding code
                                .build();

        } else {
            return null;
        }
    }
}
