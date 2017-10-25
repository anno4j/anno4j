package com.github.anno4j.schema_parsing.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.model.BuildableRDFSClazz;
import com.github.anno4j.schema_parsing.model.BuildableRDFSProperty;
import com.github.anno4j.schema_parsing.naming.MethodNameBuilder;
import com.squareup.javapoet.*;
import org.openrdf.annotations.Iri;
import org.openrdf.repository.RepositoryException;

import javax.lang.model.element.Modifier;

/**
 * Support class (of {@link BuildableRDFSProperty}) for generating getter methods
 * for this property.
 */
@Partial
public abstract class GetterSupport extends PropertyBuildingSupport implements BuildableRDFSProperty {

    @Override
    MethodSpec buildSignature(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException {
        if (getRanges() != null) {
            // JavaDoc of the method:
            CodeBlock.Builder javaDoc = CodeBlock.builder();
            if (getComments() != null && !getComments().isEmpty()) {
                CharSequence preferredComment = getPreferredRDFSComment(config);
                if (preferredComment != null) {
                    javaDoc.add(preferredComment.toString());
                }
            }

            // Find most specific common superclass:
            BuildableRDFSClazz rangeClazz = findSingleRangeClazz();

            // Get the type of return type elements:
            TypeName valueType = rangeClazz.getJavaPoetClassName(config);

            TypeName returnType;
            if(isSingleValueProperty(domainClazz)) {
                returnType = valueType;

            } else { // Otherwise it has Set return type:
                ClassName set = ClassName.get("java.util", "Set");
                returnType = ParameterizedTypeName.get(set, valueType);
            }

            return MethodNameBuilder.forObjectRepository(getObjectConnection())
                    .getJavaPoetMethodSpec("get", this, config, !isSingleValueProperty(domainClazz))
                    .toBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .returns(returnType)
                    .addJavadoc(javaDoc.build())
                    .build();

        } else {
            return null;
        }
    }

    @Override
    public MethodSpec buildGetter(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException {
        // @Iri annotation of the property:
        AnnotationSpec iriAnnotation = AnnotationSpec.builder(Iri.class)
                .addMember("value", "$S", getResourceAsString())
                .build();

        return buildSignature(domainClazz, config)
                .toBuilder()
                .addModifiers(Modifier.ABSTRACT)
                .addAnnotation(iriAnnotation)
                .addAnnotations(buildSchemaAnnotations(domainClazz, config))
                .build();
    }
}
