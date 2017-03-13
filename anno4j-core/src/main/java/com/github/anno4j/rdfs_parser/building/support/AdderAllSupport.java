package com.github.anno4j.rdfs_parser.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.rdfs_parser.building.OntGenerationConfig;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSClazz;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSProperty;
import com.github.anno4j.rdfs_parser.naming.IdentifierBuilder;
import com.github.anno4j.rdfs_parser.naming.MethodNameBuilder;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.net.URISyntaxException;

/**
 * Support class (of {@link ExtendedRDFSProperty}) for generating resource class addAll-methods
 * for this property.
 */
@Partial
public abstract class AdderAllSupport extends PropertyBuildingSupport implements ExtendedRDFSProperty {

    @Override
    MethodSpec buildSignature(OntGenerationConfig config) {
        if(getRanges() != null) {
            // Find a name for this method:
            String methodName;
            try {
                // Name building is enhanced by rdfs:label literals.
                // Find one matching the preference defined in the configuration:
                MethodNameBuilder nameBuilder = MethodNameBuilder.builder(getResourceAsString());
                CharSequence preferredLabel = getPreferredRDFSLabel(config);
                if (preferredLabel != null) {
                    nameBuilder = nameBuilder.withRDFSLabel(preferredLabel.toString());
                }

                methodName = "addAll" + nameBuilder.capitalizedPluralIdentifier();
            } catch (IdentifierBuilder.NameBuildingException | URISyntaxException e) {
                return null;
            }

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


            return MethodSpec.methodBuilder(methodName)
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
