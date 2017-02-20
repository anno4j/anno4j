package com.github.anno4j.rdfs_parser.model;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.rdfs_parser.building.OntGenerationConfig;
import com.github.anno4j.rdfs_parser.naming.ClassNameBuilder;
import com.github.anno4j.rdfs_parser.naming.IdentifierBuilder;
import com.github.anno4j.rdfs_parser.naming.MethodNameBuilder;
import com.github.anno4j.rdfs_parser.util.LowestCommonSuperclass;
import com.github.anno4j.schema_parsing.model.rdfs.RDFSClazz;
import com.github.anno4j.schema_parsing.model.rdfs.RDFSPropertySupport;
import com.squareup.javapoet.*;
import org.openrdf.annotations.Iri;

import javax.lang.model.element.Modifier;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;


@Partial
public abstract class ExtendedRDFSPropertySupport extends RDFSPropertySupport implements ExtendedRDFSProperty {

    @Override
    public void addDomainClazz(ExtendedRDFSClazz clazz) {
        addDomainClazz(clazz, true);
    }

    @Override
    public void addDomainClazz(ExtendedRDFSClazz clazz, boolean updateInverse) {
        if(updateInverse) {
            clazz.addOutgoingProperty(this, false);
        }
        super.addDomain(clazz);
    }

    @Override
    public void addRangeClazz(ExtendedRDFSClazz clazz) {
        addRangeClazz(clazz, true);
    }

    @Override
    public void addRangeClazz(ExtendedRDFSClazz clazz, boolean updateInverse) {
        if(updateInverse) {
            clazz.addIncomingProperty(this, false);
        }
        super.addRange(clazz);
    }

    private CharSequence getPreferredRDFSLabel(OntGenerationConfig config) {
        // Get the label in the preferred language:
        CharSequence preferredLabel = null;
        for (CharSequence label : getLabels()) {
            if(config.isPreferredForIdentifiers(label, preferredLabel)) {
                preferredLabel = label;
            }
        }

        return preferredLabel;
    }

    private CharSequence getPreferredRDFSComment(OntGenerationConfig config) {
        CharSequence preferredComment = null;
        for (CharSequence comment : getComments()) {
            if(config.isPreferredForJavaDoc(comment, preferredComment)) {
                preferredComment = comment;
            }
        }

        return preferredComment;
    }

    @Override
    public MethodSpec buildGetter(OntGenerationConfig config) {
        if(getRanges() != null) {
            MethodSpec getter;
            try {
                MethodNameBuilder builder = MethodNameBuilder.builder(getResourceAsString());

                // Identifier building is enhanced by rdfs:label literals:
                if(getLabels() != null && !getLabels().isEmpty()) {
                    CharSequence preferredLabel = getPreferredRDFSLabel(config);
                    if (preferredLabel != null) {
                        builder = builder.withRDFSLabel(preferredLabel.toString());
                    }
                }

                getter = builder.getterSpec();
            } catch (URISyntaxException | IdentifierBuilder.NameBuildingException e) {
                return null;
            }

            // JavaDoc of the method:
            CodeBlock.Builder javaDoc = CodeBlock.builder();
            if(getComments() != null && !getComments().isEmpty()) {
                CharSequence preferredComment = getPreferredRDFSComment(config);
                if(preferredComment != null) {
                    javaDoc.add(preferredComment.toString());
                }
            }

            // IRI annotation of the method:
            AnnotationSpec iriAnnotation = AnnotationSpec.builder(Iri.class)
                                                        .addMember("value", "$S", getResourceAsString())
                                                        .build();

            ClassName set = ClassName.get("java.util", "Set");

            // Get the range classes of this property:
            Collection<ExtendedRDFSClazz> ranges = new HashSet<>();
            for (RDFSClazz range : getRanges()) {
                ranges.add((ExtendedRDFSClazz) range);
            }
            // Find most specific common superclass:
            ExtendedRDFSClazz rangeClazz = LowestCommonSuperclass.getLowestCommonSuperclass(ranges);

            TypeName returnType;
            try {
                returnType = ParameterizedTypeName.get(set, rangeClazz.getJavaPoetClassName(config));

            } catch (IdentifierBuilder.NameBuildingException | URISyntaxException e) {
                returnType = ParameterizedTypeName.get(set, ClassName.get("com.github.anno4j.model.impl", "ResourceObject"));
            }

            return getter.toBuilder()
                         .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                         .addAnnotation(iriAnnotation)
                         .returns(returnType)
                         .addJavadoc(javaDoc.build())
                         .build();

        } else {
            return null;
        }
    }

    @Override
    public MethodSpec buildSetter(OntGenerationConfig config) {
        if (getRanges() != null) {
            MethodSpec setter;
            try {
                MethodNameBuilder builder = MethodNameBuilder.builder(getResourceAsString());

                // Identifier building is enhanced by rdfs:label literals:
                if(getLabels() != null && !getLabels().isEmpty()) {
                    // Identifier building is enhanced by rdfs:label literals:
                    if(getLabels() != null && !getLabels().isEmpty()) {
                        CharSequence preferredLabel = getPreferredRDFSLabel(config);
                        if (preferredLabel != null) {
                            builder = builder.withRDFSLabel(preferredLabel.toString());
                        }
                    }
                }

                setter = builder.setterSpec();

                // JavaDoc of the method:
                CodeBlock.Builder javaDoc = CodeBlock.builder();
                if(getComments() != null && !getComments().isEmpty()) {
                    CharSequence preferredComment = getPreferredRDFSComment(config);
                    if(preferredComment != null) {
                        javaDoc.add(preferredComment.toString());
                    }
                }

                // IRI annotation of the method:
                AnnotationSpec iriAnnotation = AnnotationSpec.builder(Iri.class)
                                                            .addMember("value", "$S", getResourceAsString())
                                                            .build();

                ClassName set = ClassName.get("java.util", "Set");
                // Get the range classes of this property:
                Collection<ExtendedRDFSClazz> ranges = new HashSet<>();
                for (RDFSClazz range : getRanges()) {
                    ranges.add((ExtendedRDFSClazz) range);
                }
                // Find most specific common superclass:
                ExtendedRDFSClazz rangeClazz = LowestCommonSuperclass.getLowestCommonSuperclass(ranges);

                TypeName returnType;
                try {
                    returnType = ParameterizedTypeName.get(set, rangeClazz.getJavaPoetClassName(config));

                } catch (IdentifierBuilder.NameBuildingException | URISyntaxException e) {
                    // Fallback to Anno4js base interface for resources:
                    returnType = ParameterizedTypeName.get(set, ClassName.get("com.github.anno4j.model.impl", "ResourceObject"));
                }
                String paramName;
                try {
                    paramName = ClassNameBuilder.builder(getResourceAsString())
                                                .lowercaseIdentifier() + "s";
                } catch (IdentifierBuilder.NameBuildingException | URISyntaxException e) {
                    paramName = "values";
                }

                return setter.toBuilder()
                            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                            .addAnnotation(iriAnnotation)
                            .addParameter(returnType, paramName)
                            .returns(void.class)
                            .addJavadoc(javaDoc.build())
                            .build();

            } catch (IdentifierBuilder.NameBuildingException | URISyntaxException e) {
                return null;
            }
        } else {
            return null;
        }
    }
}