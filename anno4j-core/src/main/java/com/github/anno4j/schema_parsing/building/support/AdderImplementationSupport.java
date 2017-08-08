package com.github.anno4j.schema_parsing.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.model.BuildableRDFSClazz;
import com.github.anno4j.schema_parsing.model.BuildableRDFSProperty;
import com.github.anno4j.schema_parsing.validation.Validator;
import com.squareup.javapoet.*;
import org.openrdf.repository.RepositoryException;

import java.util.HashSet;
import java.util.Set;

/**
 * Support class (of {@link BuildableRDFSProperty}) for generating add-methods for
 * the corresponding property.
 */
@Partial
public abstract class AdderImplementationSupport extends AdderSupport implements BuildableRDFSProperty {

    @Override
    public MethodSpec buildAdderImplementation(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException {
        // Get the signature of a add* method for this property:
        MethodSpec adder = buildSignature(domainClazz, config);
        if(adder != null) {
            MethodSpec.Builder adderBuilder = adder.toBuilder();

            // Implementations in support classes have the @Override annotation:
            AnnotationSpec overrideAnnotation = AnnotationSpec.builder(Override.class)
                    .build();

            // Add validation code:
            BuildableRDFSClazz range = findSingleRangeClazz();
            ParameterSpec param = adder.parameters.get(0);
            for (Validator validator : config.getValidators()) {
                if (validator.isValueSpaceConstrained(range)) {
                    validator.addValueSpaceCheck(adderBuilder, param, range);
                }
            }

            // Prepare types:
            ClassName rangeClassName = range.getJavaPoetClassName(config);
            TypeName accumulatorType = ParameterizedTypeName.get(ClassName.get(Set.class), rangeClassName);
            TypeName hashSet = ParameterizedTypeName.get(ClassName.get(HashSet.class), rangeClassName);

            // Build the adders method specification:
            adderBuilder.addAnnotation(overrideAnnotation)
                                .addStatement("$T _acc = new $T()", accumulatorType, hashSet);

            // Add the value(s) returned by getter:
            MethodSpec getter = buildGetter(domainClazz, config);
            if (isSingleValueProperty(domainClazz)) {
                adderBuilder.addStatement("_acc.add($N())", getter);
            } else {
                adderBuilder.addStatement("_acc.addAll($N())", getter);
            }

            adderBuilder.addStatement("_acc.add($N)", param);

            // Set the new values:
            if(isSingleValueProperty(domainClazz)) {
                // If this is a single value property check wether the there is only one value:
                adderBuilder.beginControlFlow("if(_acc.size() > 1)")
                        .addStatement("throw new $T($S)", ClassName.get(IllegalArgumentException.class), "Too many values")
                        .endControlFlow()
                        .beginControlFlow("else if(_acc.size() == 1)")
                        .addStatement("$N(_acc.iterator().next())", buildSetter(domainClazz, config))
                        .endControlFlow();
            } else {
                adderBuilder.addStatement("$N(_acc)", buildSetter(domainClazz, config));
            }

            return adderBuilder.addStatement("sanitizeSchema($S)", getResourceAsString()) // Handles adding to superproperties
                                .build();

        } else {
            return null;
        }
    }
}
