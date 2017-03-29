package com.github.anno4j.rdfs_parser.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.rdfs_parser.building.OntGenerationConfig;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSProperty;
import com.squareup.javapoet.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Support class (of {@link ExtendedRDFSProperty}) for generating get-methods for
 * the corresponding property.
 */
@Partial
public abstract class GetterImplementationSupport extends GetterSupport implements ExtendedRDFSProperty {

    @Override
    public MethodSpec buildGetterImplementation(OntGenerationConfig config) {
        MethodSpec getter = buildSignature(config);
        if(getter != null) {
            MethodSpec.Builder getterBuilder = getter.toBuilder();

            // Implementations in support classes have the @Override annotation:
            AnnotationSpec overrideAnnotation = AnnotationSpec.builder(Override.class)
                                                                .build();

            // The annotated field from which we will retrieve values:
            FieldSpec field = buildAnnotatedField(config);

            // Prepare types:
            ClassName set = ClassName.get(Set.class);
            ClassName rangeType = getRangeJavaPoetClassName(config);
            TypeName hashSet = ParameterizedTypeName.get(ClassName.get(HashSet.class), rangeType);

            getterBuilder.addStatement("$T values = new $T();", set, hashSet)
                         .addStatement("values.addAll($N)", field)
                         .addStatement("return values");

            return getterBuilder.addAnnotation(overrideAnnotation)
                                .build();
        } else {
            return null;
        }
    }
}
