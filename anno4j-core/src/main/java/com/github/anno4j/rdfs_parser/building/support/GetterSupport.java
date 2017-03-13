package com.github.anno4j.rdfs_parser.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.rdfs_parser.building.OntGenerationConfig;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSClazz;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSProperty;
import com.github.anno4j.rdfs_parser.naming.IdentifierBuilder;
import com.github.anno4j.rdfs_parser.naming.MethodNameBuilder;
import com.squareup.javapoet.*;
import org.openrdf.annotations.Iri;

import javax.lang.model.element.Modifier;
import java.net.URISyntaxException;

/**
 * Support class (of {@link ExtendedRDFSProperty}) for generating getter methods
 * for this property.
 */
@Partial
public abstract class GetterSupport extends PropertyBuildingSupport implements ExtendedRDFSProperty {

    @Override
    MethodSpec buildSignature(OntGenerationConfig config) {
        if (getRanges() != null) {
            MethodSpec.Builder getter;
            try {
                MethodNameBuilder nameBuilder = MethodNameBuilder.builder(getResourceAsString());

                // Identifier building is enhanced by rdfs:label literals:
                if (getLabels() != null && !getLabels().isEmpty()) {
                    CharSequence preferredLabel = getPreferredRDFSLabel(config);
                    if (preferredLabel != null) {
                        nameBuilder = nameBuilder.withRDFSLabel(preferredLabel.toString());
                    }
                }

                getter = MethodSpec.methodBuilder("get" + nameBuilder.capitalizedPluralIdentifier());
            } catch (URISyntaxException | IdentifierBuilder.NameBuildingException e) {
                return null;
            }

            // JavaDoc of the method:
            CodeBlock.Builder javaDoc = CodeBlock.builder();
            if (getComments() != null && !getComments().isEmpty()) {
                CharSequence preferredComment = getPreferredRDFSComment(config);
                if (preferredComment != null) {
                    javaDoc.add(preferredComment.toString());
                }
            }

            // IRI annotation of the method:
            AnnotationSpec iriAnnotation = AnnotationSpec.builder(Iri.class)
                    .addMember("value", "$S", getResourceAsString())
                    .build();

            ClassName set = ClassName.get("java.util", "Set");

            // Find most specific common superclass:
            ExtendedRDFSClazz rangeClazz = findSingleRangeClazz();

            // Get the type of return type elements:
            TypeName setType = rangeClazz.getJavaPoetClassName(config);

            // For convenience the return type for strings should be a wildcard, i.e. Set<? extends CharSequence>:
            if(setType.equals(ClassName.get(CharSequence.class))) {
                setType = WildcardTypeName.subtypeOf(setType);
            }

            TypeName returnType = ParameterizedTypeName.get(set, setType);

            return getter
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(iriAnnotation)
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
