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

            // Prepare types:
            TypeName set = ParameterizedTypeName.get(ClassName.get(Set.class), rangeClassName);
            TypeName hashSet = ParameterizedTypeName.get(ClassName.get(HashSet.class), rangeClassName);

            adderBuilder.addAnnotation(overrideAnnotation)
                        .addStatement("$T _acc = new $T()", set, hashSet);

            // Add the value(s) returned by getter:
            MethodSpec getter = buildGetter(domainClazz, config);
            if (isSingleValueProperty(domainClazz)) {
                adderBuilder.addStatement("_acc.add($N())", getter);
            } else {
                adderBuilder.addStatement("_acc.addAll($N())", getter);
            }

            adderBuilder.addStatement("_acc.addAll($N)", param);

            // Set the new values:
            if(isSingleValueProperty(domainClazz)) {
                // If this is a single value property check whether the there is only one value:
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
