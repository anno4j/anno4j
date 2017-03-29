package com.github.anno4j.rdfs_parser.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.rdfs_parser.building.OntGenerationConfig;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSClazz;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSProperty;
import com.github.anno4j.rdfs_parser.naming.MethodNameBuilder;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;

/**
 * Support class (of {@link ExtendedRDFSProperty}) for generating getter methods
 * for this property.
 */
@Partial
public abstract class GetterSupport extends PropertyBuildingSupport implements ExtendedRDFSProperty {

    @Override
    MethodSpec buildSignature(OntGenerationConfig config) {
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
            ExtendedRDFSClazz rangeClazz = findSingleRangeClazz();

            // Get the type of return type elements:
            TypeName setType = rangeClazz.getJavaPoetClassName(config);

            // For convenience the return type for strings should be a wildcard, i.e. Set<? extends CharSequence>:
            if(setType.equals(ClassName.get(CharSequence.class))) {
                setType = WildcardTypeName.subtypeOf(setType);
            }

            // Prepare the return type of the method:
            ClassName set = ClassName.get("java.util", "Set");
            TypeName returnType = ParameterizedTypeName.get(set, setType);

            // Create name builder with the preferred RDFS label if available:
            MethodNameBuilder methodNameBuilder = MethodNameBuilder.builder(getResourceAsString());
            CharSequence preferredLabel = getPreferredRDFSLabel(config);
            if (preferredLabel != null) {
                methodNameBuilder.withRDFSLabel(getPreferredRDFSLabel(config).toString());
            }

            return methodNameBuilder
                    .getJavaPoetMethodSpec("get", true)
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
    public MethodSpec buildGetter(OntGenerationConfig config) {
        return buildSignature(config)
                .toBuilder()
                .addModifiers(Modifier.ABSTRACT)
                .build();
    }
}
