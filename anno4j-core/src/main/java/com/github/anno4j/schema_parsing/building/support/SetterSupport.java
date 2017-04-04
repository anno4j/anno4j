package com.github.anno4j.schema_parsing.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.model.ExtendedRDFSClazz;
import com.github.anno4j.schema_parsing.model.ExtendedRDFSProperty;
import com.github.anno4j.schema_parsing.naming.ClassNameBuilder;
import com.github.anno4j.schema_parsing.naming.IdentifierBuilder;
import com.github.anno4j.schema_parsing.naming.MethodNameBuilder;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.net.URISyntaxException;

/**
 * Support class belonging to {@link ExtendedRDFSProperty} that adds functionality
 * to generate a JavaPoet specification for a setter method of this property.
 */
@Partial
public abstract class SetterSupport extends PropertyBuildingSupport implements ExtendedRDFSProperty {

    /**
     * Generates the signature of a setter method for this property.
     * The signature is in the following format:<br>
     * <code>public void set%Property-Name%(Set<%Range%>)</code>
     * Note that no annotations are added to the signature.
     * JavaDoc is added to the signature if possible.
     *
     * @param config Configuration of the generation process, e.g. which
     *               language to use for the JavaDoc.
     * @return Returns the JavaPoet specification of the signature.
     */
    @Override
    MethodSpec buildSignature(OntGenerationConfig config) {
        if (getRanges() != null) {
            // Find most specific common superclass:
            ExtendedRDFSClazz rangeClazz = findSingleRangeClazz();
            // Find a name for the parameter:
            String paramName;
            try {
                paramName = ClassNameBuilder.builder(getResourceAsString())
                        .lowercaseIdentifier() + "s";
            } catch (IdentifierBuilder.NameBuildingException | URISyntaxException e) {
                paramName = "values";
            }
            ClassName set = ClassName.get("java.util", "Set");

            // JavaDoc of the method:
            CodeBlock.Builder javaDoc = CodeBlock.builder();
            CharSequence preferredComment = getPreferredRDFSComment(config);
            if (preferredComment != null) {
                javaDoc.add(preferredComment.toString());
            }
            javaDoc.add("\n@param " + paramName + " The elements to set.");

            // Add a throws declaration if the value space is constrained:
            addJavaDocExceptionInfo(javaDoc, rangeClazz, config);

            // Get the type of parameter type elements:
            TypeName setType = rangeClazz.getJavaPoetClassName(config);

            // For convenience the parameter type for strings should be a wildcard, i.e. Set<? extends CharSequence>:
            if (setType.equals(ClassName.get(CharSequence.class))) {
                setType = WildcardTypeName.subtypeOf(setType);
            }

            TypeName paramType = ParameterizedTypeName.get(set, setType);

            // Create name builder with the preferred RDFS label if available:
            MethodNameBuilder methodNameBuilder = MethodNameBuilder.builder(getResourceAsString());
            CharSequence preferredLabel = getPreferredRDFSLabel(config);
            if (preferredLabel != null) {
                methodNameBuilder.withRDFSLabel(getPreferredRDFSLabel(config).toString());
            }

            return methodNameBuilder
                    .getJavaPoetMethodSpec("set", true)
                    .toBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(paramType, paramName)
                    .returns(void.class)
                    .addJavadoc(javaDoc.build())
                    .build();
        } else {
            return null;
        }
    }

    @Override
    public MethodSpec buildSetter(OntGenerationConfig config) {
        // Get the signature of a setter for this property:
        MethodSpec signature = buildSignature(config);

        if (signature != null) {
            // Add an abstract modifier to the signature:
            return signature.toBuilder()
                    .addModifiers(Modifier.ABSTRACT)
                    .build();

        } else {
            return null;
        }
    }
}
