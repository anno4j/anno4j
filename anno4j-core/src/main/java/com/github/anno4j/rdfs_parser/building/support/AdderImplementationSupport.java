package com.github.anno4j.rdfs_parser.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.rdfs_parser.building.OntGenerationConfig;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSClazz;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSProperty;
import com.github.anno4j.rdfs_parser.validation.Validator;
import com.squareup.javapoet.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Support class (of {@link ExtendedRDFSProperty}) for generating add-methods for
 * the corrpesonding property.
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
                MethodSpec superAdder = superProperty.buildAdder(config);
                adderBuilder.addStatement("$N($N)", superAdder, param);
            }

            // Add actual adding code:
            MethodSpec getter = buildGetter(config);
            MethodSpec setter = buildSetter(config);
            ClassName rangeClassName = range.getJavaPoetClassName(config);
            ClassName set = ClassName.get(Set.class);
            TypeName rangeSet = ParameterizedTypeName.get(set, rangeClassName);
            ClassName hashSet = ClassName.get(HashSet.class);
            TypeName rangeHashSet = ParameterizedTypeName.get(hashSet, rangeClassName);

            adderBuilder.addStatement("$T values = new $T()", rangeSet, rangeHashSet)
                    .beginControlFlow("if($N() != null)", getter)
                    .addStatement("values.addAll($N())", getter)
                    .endControlFlow()
                    .addStatement("values.add($N)", param)
                    .addStatement("$N(values)", setter);

            // Build the adders method specification:
            return adderBuilder.addAnnotation(overrideAnnotation)
                                .build();

        } else {
            return null;
        }
    }
}
