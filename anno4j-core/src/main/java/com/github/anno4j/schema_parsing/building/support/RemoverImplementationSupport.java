package com.github.anno4j.schema_parsing.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.github.anno4j.schema.model.rdfs.RDFSProperty;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.model.BuildableRDFSClazz;
import com.github.anno4j.schema_parsing.model.BuildableRDFSProperty;
import com.squareup.javapoet.*;
import org.openrdf.repository.RepositoryException;

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

            // For string types the return type of getters is a wildcard type:
            if(rangeType.equals(ClassName.get(CharSequence.class))) {
                rangeType = WildcardTypeName.subtypeOf(rangeType);
            }

            // Prepare class names of used types:
            ClassName set = ClassName.get(Set.class);
            TypeName rangeSet = ParameterizedTypeName.get(set, rangeType);
            ParameterSpec param = signature.parameters.get(0);

            // Get the annotated field for this property:
            FieldSpec field = buildAnnotatedField(domainClazz, config);

            // Add the actual removal code and sanitize schema afterwards:
            removerBuilder.beginControlFlow("if(this.$N.contains($N))", field, param)
                          .addStatement("removeValue($S, $N)", getResourceAsString(), param)
                          .addStatement("sanitizeSchema($S)", getResourceAsString())
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
