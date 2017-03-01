package com.github.anno4j.rdfs_parser.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.rdfs_parser.building.OntGenerationConfig;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSClazz;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSProperty;
import com.github.anno4j.rdfs_parser.validation.Validator;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;

/**
 * Support class (of {@link ExtendedRDFSProperty}) for generating
 * setter methods of support classes.
 */
@Partial
public abstract class SetterImplementationSupport extends SetterSupport implements ExtendedRDFSProperty {

    @Override
    public MethodSpec buildSetterImplementation(OntGenerationConfig config) {
        // Get the signature of a setter for this property:
        MethodSpec signature = buildSignature(config);

        if(signature != null) {
            // Override annotation of the method:
            AnnotationSpec overrideAnnotation = AnnotationSpec.builder(Override.class).build();

            MethodSpec.Builder setterBuilder = signature.toBuilder();

            // Add validation code:
            ExtendedRDFSClazz range = findSingleRangeClazz();
            ClassName rangeClassName = range.getJavaPoetClassName(config);
            String paramName = signature.parameters.get(0).name;
            for (Validator validator : config.getValidators()) {
                if(validator.isValueSpaceConstrained(range)) {
                    ParameterSpec current = ParameterSpec.builder(rangeClassName, "current").build();
                    setterBuilder.beginControlFlow("for($T $N : " + paramName + ")", rangeClassName, current);
                    validator.addValueSpaceCheck(setterBuilder, current, range);
                    setterBuilder.endControlFlow();
                }
            }

            // Remove all currently stored values by using removeAll-method
            // This ensures that values are also (safely) removed from superproperties:
            MethodSpec removerAll = buildRemoverAll(config);
            MethodSpec getter = buildGetter(config);
            setterBuilder.addStatement("$N($N())", removerAll, getter);

            // Generate code for adding new values also to superproperties:
            for (ExtendedRDFSProperty superProperty : getSuperproperties()) {
                MethodSpec superAdderAll = superProperty.buildAdderAll(config);

                setterBuilder.addStatement("$N(" + paramName + ")", superAdderAll);
            }

            return setterBuilder.addAnnotation(overrideAnnotation)
                                .addStatement("return")
                                .build();

        } else {
            return null;
        }
    }
}
