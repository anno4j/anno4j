package com.github.anno4j.rdfs_parser.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.rdfs_parser.building.OntGenerationConfig;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSClazz;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSProperty;
import com.github.anno4j.rdfs_parser.validation.Validator;
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
            String paramName = signature.parameters.get(0).name;
            ParameterSpec current = ParameterSpec.builder(rangeClassName, "current").build();

            // Add validation code:
            for (Validator validator : config.getValidators()) {
                if(validator.isValueSpaceConstrained(range)) {
                    adderBuilder.beginControlFlow("for($T $N : " + paramName + ")", rangeClassName, current);
                    validator.addValueSpaceCheck(adderBuilder, current, range);
                    adderBuilder.endControlFlow();
                }
            }

            // Generate code for adding also to superproperties:
            for (ExtendedRDFSProperty superProperty : getSuperproperties()) {
                MethodSpec superAdderAll = superProperty.buildAdderAll(config);
                adderBuilder.addStatement(superAdderAll.name + "(" + paramName + ")");
            }

            TypeName set = ParameterizedTypeName.get(ClassName.get("java.util", "Set"), rangeClassName);
            TypeName hashSet = ParameterizedTypeName.get(ClassName.get("java.util", "HashSet"), rangeClassName);
            MethodSpec getter = buildGetter(config);
            MethodSpec setter = buildSetter(config);

            return adderBuilder.addAnnotation(overrideAnnotation)
                    .addStatement("$T set = new $T()", set, hashSet)
                    .beginControlFlow("if(" + getter.name + "() != null)")
                    .addStatement("set.addAll(" + getter.name + "())")
                    .endControlFlow()
                    .addStatement("set.addAll(values)")
                    .addStatement(setter.name + "(set)")
                    .build();

        } else {
            return null;
        }
    }
}
