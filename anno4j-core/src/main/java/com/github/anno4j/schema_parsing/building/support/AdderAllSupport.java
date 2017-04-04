package com.github.anno4j.schema_parsing.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.model.ExtendedRDFSClazz;
import com.github.anno4j.schema_parsing.model.ExtendedRDFSProperty;
import com.github.anno4j.schema_parsing.naming.MethodNameBuilder;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;

/**
 * Support class (of {@link ExtendedRDFSProperty}) for generating resource class addAll-methods
 * for this property.
 */
@Partial
public abstract class AdderAllSupport extends PropertyBuildingSupport implements ExtendedRDFSProperty {

    @Override
    MethodSpec buildSignature(OntGenerationConfig config) {
        if(getRanges() != null) {
            // Get the most specific class describing all of the properties range classes:
            ExtendedRDFSClazz range = findSingleRangeClazz();
            ClassName set = ClassName.get("java.util", "Set");

            // Get the type of parameter type elements:
            TypeName setType = range.getJavaPoetClassName(config);

            // For convenience the parameter type for strings should be a wildcard, i.e. Set<? extends CharSequence>:
            if(setType.equals(ClassName.get(CharSequence.class))) {
                setType = WildcardTypeName.subtypeOf(setType);
            }

            TypeName paramType = ParameterizedTypeName.get(set, setType);

            // Generate Javadoc if a rdfs:comment literal is available:
            CodeBlock.Builder javaDoc = CodeBlock.builder();
            CharSequence preferredComment = getPreferredRDFSComment(config);
            if (preferredComment != null) {
                javaDoc.add(preferredComment.toString());
            }
            javaDoc.add("\n@param values The elements to be added.");

            // Add a throws declaration if the value space is constrained:
            addJavaDocExceptionInfo(javaDoc, range, config);

            // Create name builder with the preferred RDFS label if available:
            MethodNameBuilder methodNameBuilder = MethodNameBuilder.builder(getResourceAsString());
            CharSequence preferredLabel = getPreferredRDFSLabel(config);
            if (preferredLabel != null) {
                methodNameBuilder.withRDFSLabel(getPreferredRDFSLabel(config).toString());
            }

            return methodNameBuilder
                    .getJavaPoetMethodSpec("addAll", true)
                    .toBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(paramType, "values")
                    .addJavadoc(javaDoc.build())
                    .returns(void.class)
                    .build();

        } else {
            return null;
        }
    }

    @Override
    public MethodSpec buildAdderAll(OntGenerationConfig config) {
        // Add the abstract modifier to the signature, because there is no implementation in the interface:
        return buildSignature(config)
                .toBuilder()
                .addModifiers(Modifier.ABSTRACT)
                .build();
    }
}
