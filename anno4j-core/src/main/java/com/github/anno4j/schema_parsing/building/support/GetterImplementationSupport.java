package com.github.anno4j.schema_parsing.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.model.BuildableRDFSProperty;
import com.squareup.javapoet.*;
import org.openrdf.repository.RepositoryException;

import java.util.HashSet;
import java.util.Set;

/**
 * Support class (of {@link BuildableRDFSProperty}) for generating get-methods for
 * the corresponding property.
 */
@Partial
public abstract class GetterImplementationSupport extends GetterSupport implements BuildableRDFSProperty {

    @Override
    public MethodSpec buildGetterImplementation(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException {
        MethodSpec getter = buildSignature(domainClazz, config);
        if(getter != null) {
            MethodSpec.Builder getterBuilder = getter.toBuilder();

            // Implementations in support classes have the @Override annotation:
            AnnotationSpec overrideAnnotation = AnnotationSpec.builder(Override.class)
                                                                .build();

            // The annotated field from which we will retrieve values:
            FieldSpec field = buildAnnotatedField(domainClazz, config);

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
