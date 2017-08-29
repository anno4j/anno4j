package com.github.anno4j.schema_parsing.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.model.BuildableRDFSClazz;
import com.github.anno4j.schema_parsing.model.BuildableRDFSProperty;
import com.squareup.javapoet.*;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.advisers.helpers.PropertySet;

import java.util.HashSet;
import java.util.Set;

/**
 * Support class (of {@link BuildableRDFSProperty}) for generating remove-methods for
 * the corresponding property.
 */
@Partial
public abstract class RemoverImplementationSupport extends RemoverSupport implements BuildableRDFSProperty {

    @Override
    public MethodSpec buildRemoverImplementation(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException {
        // Get the signature of a remove* method for this property:
        MethodSpec signature = buildSignature(domainClazz, config);
        if(signature != null) {
            MethodSpec.Builder removerBuilder = signature.toBuilder();

            // Implementations in support classes have the @Override annotation:
            AnnotationSpec overrideAnnotation = AnnotationSpec.builder(Override.class)
                    .build();

            // Get the class name of the properties range:
            BuildableRDFSClazz range = findSingleRangeClazz();
            TypeName rangeType = range.getJavaPoetClassName(config);

            // Prepare class names of used types:
            TypeName set = ParameterizedTypeName.get(ClassName.get(Set.class), rangeType);
            TypeName hashSet = ParameterizedTypeName.get(ClassName.get(HashSet.class), rangeType);
            ParameterSpec param = signature.parameters.get(0);

            // Get the value(s) returned by getter:
            MethodSpec getter = buildGetter(domainClazz, config);
            if (isSingleValueProperty(domainClazz)) {
                removerBuilder.addStatement("$T _oldValues = new $T()", set, hashSet)
                            .addStatement("_oldValues.add($N())", getter);
            } else {
                removerBuilder.addStatement("$T _oldValues = $N()", set, getter);
            }

            // Add the actual removal code and sanitize schema afterwards:
            TypeName propertySet = ClassName.get(PropertySet.class);
            removerBuilder.beginControlFlow("if(_oldValues.contains($N))", param)
                          .addStatement("removeValue($S, $N)", getResourceAsString(), param)
                          .addStatement("sanitizeSchema($S)", getResourceAsString())
                          .addComment("Refresh values:")
                          .beginControlFlow("if($N() instanceof $T)", getter, propertySet)
                          .addStatement("(($T) $N()).refresh()", propertySet, getter)
                          .endControlFlow()
                          .addStatement("return true")
                          .endControlFlow()
                          .addStatement("return false");

            // Build the removers method specification:
            return removerBuilder.addAnnotation(overrideAnnotation)
                    .build();

        } else {
            return null;
        }
    }
}
