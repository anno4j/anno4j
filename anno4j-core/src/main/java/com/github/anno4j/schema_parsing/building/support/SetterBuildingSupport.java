package com.github.anno4j.schema_parsing.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.github.anno4j.schema.model.rdfs.RDFSProperty;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.model.BuildableRDFSClazz;
import com.github.anno4j.schema_parsing.model.BuildableRDFSProperty;
import com.github.anno4j.schema_parsing.naming.ClassNameBuilder;
import com.github.anno4j.schema_parsing.naming.IdentifierBuilder;
import com.github.anno4j.schema_parsing.naming.MethodNameBuilder;
import com.github.anno4j.schema_parsing.validation.Validator;
import com.squareup.javapoet.*;
import org.openrdf.repository.RepositoryException;

import javax.lang.model.element.Modifier;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;

/**
 * This partial class belonging to {@link com.github.anno4j.schema_parsing.model.BuildableRDFSProperty}
 * provides functionality to generate setter method stubs to its inheriting classes.
 */
@Partial
public abstract class SetterBuildingSupport extends PropertyBuildingSupport implements BuildableRDFSProperty {

    /**
     * Returns a name for the parameter of the setter based on its RDFS labels and/or other information about
     * the property mapped.
     * @return Tries to find a meaningful name for a setter parameter. Returns {@code "values"} on failure.
     */
    String getParameterName() {
        try {
            return ClassNameBuilder.builder(getResourceAsString())
                    .lowercaseIdentifier() + "s";
        } catch (IdentifierBuilder.NameBuildingException | URISyntaxException e) {
            return  "values";
        }
    }

    /**
     * Returns the JavaPoet representation of the parameters type.
     * @param config The configuration to use for building type names.
     * @param allowCapture Whether the parameter type should be transformed into a capture, if applicable (e.g. {@link CharSequence}).
     * @return Returns the JavaPoet representation of the parameters target type.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    TypeName getParameterType(OntGenerationConfig config, boolean allowCapture) throws RepositoryException {
        TypeName paramType = findSingleRangeClazz().getJavaPoetClassName(config);

        // For convenience the parameter type for strings should be a wildcard, i.e. Set<? extends CharSequence>:
        if (allowCapture && paramType.equals(ClassName.get(CharSequence.class))) {
            paramType = WildcardTypeName.subtypeOf(paramType);
        }

        return paramType;
    }

    /**
     * Generates a setters signature stub without any parameter for this property.
     * This stub can be used as a basis for inheriting support classes in order to generate overloading
     * setters.
     * @param domainClazz The class for which to generate a setter method.
     * @param config The configuration on basis of which to build the method stub.
     * @return Returns the method stub without any parameter or {@code null} if there is no range specified
     * for this property.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    MethodSpec.Builder buildParameterlessSetterSignature(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException {
        if (getRanges() != null) {
            // Find most specific common superclass:
            BuildableRDFSClazz rangeClazz = findSingleRangeClazz();
            // Find a name for the parameter:
            String paramName = getParameterName();

            // JavaDoc of the method:
            CodeBlock.Builder javaDoc = CodeBlock.builder();
            CharSequence preferredComment = getPreferredRDFSComment(config);
            if (preferredComment != null) {
                javaDoc.add(preferredComment.toString());
            }
            javaDoc.add("\n@param " + paramName + " The elements to set.");

            // Add a throws declaration if the value space is constrained:
            addJavaDocExceptionInfo(javaDoc, rangeClazz, config);

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
                    .returns(void.class)
                    .addJavadoc(javaDoc.build());
        } else {
            return null;
        }
    }

    /**
     * Adds code to the method stub which performs validation of the first parameter of the method according to
     * the {@link Validator}s in {@code config} and afterwards sets the values to the support classes field
     * (see {@link #buildAnnotatedField(RDFSClazz, OntGenerationConfig)}).
     * Changes are propagated to all super- and subproperties of the property mapped.
     * @param stub The JavaPoet method specification to which the generated code will be added.
     * @param domainClazz The class for which the method is defined.
     * @param config The configuration for building code.
     * @param allowSingleValueParam Whether the parameter of the method can be single valued in case
     *                              the cardinality of the property is one.
     * @return Returns a method builder representing the method after the code was added.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    MethodSpec.Builder addSetterImplementationCode(MethodSpec.Builder stub, RDFSClazz domainClazz, OntGenerationConfig config, boolean allowSingleValueParam) throws RepositoryException {
        BuildableRDFSClazz range = findSingleRangeClazz();
        ClassName rangeClassName = range.getJavaPoetClassName(config);
        ParameterSpec param = stub.build().parameters.get(0);
        String paramName = param.name;
        ParameterSpec current = ParameterSpec.builder(rangeClassName, "current").build();
        boolean isVarArg = stub.build().varargs;

        Integer cardinality = getCardinality(domainClazz);
        boolean isParameterSingleValue = allowSingleValueParam && cardinality != null && cardinality == 1;

        // Add validation code:
        for (Validator validator : config.getValidators()) {
            if(validator.isValueSpaceConstrained(range)) {
                if(isParameterSingleValue) {
                    validator.addValueSpaceCheck(stub, stub.build().parameters.get(0), range);
                } else {
                    stub.beginControlFlow("for($T $N : $N)", rangeClassName, current, paramName);
                    validator.addValueSpaceCheck(stub, current, range);
                    stub.endControlFlow();
                }
            }
        }

        // Get the annotated field for this property:
        FieldSpec field = buildAnnotatedField(domainClazz, config);

        // Remove all old values from superproperties:
        stub.addComment("Remove old values from superproperties:")
                .beginControlFlow("if(!this.$N.isEmpty())", field);
        for (RDFSProperty superProperty : getSuperproperties()) {
            // Ignore superproperties from special vocabulary and the reflexive relation:
            if(!isFromSpecialVocabulary(superProperty) && !superProperty.equals(this)) {
                MethodSpec superPropertyRemoverAll = asBuildableProperty(superProperty).buildRemoverAll(domainClazz, config);
                stub.addStatement("$N(this.$N)", superPropertyRemoverAll, field);
            }
        }
        stub.endControlFlow(); // End if(!field.isEmpty())

        // Add new values to superproperties:
        stub.addComment("Add new values to superproperties:");
        if(isVarArg) { // Emptiness check must be performed different for Set<> and array (vararg):
            stub.beginControlFlow("if($N.length == 0)", paramName);
        } else {
            stub.beginControlFlow("if(!$N.isEmpty())", paramName);
        }
        for (RDFSProperty superProperty : getSuperproperties()) {
            if(!isFromSpecialVocabulary(superProperty) && !superProperty.equals(this)) {
                // Use add* for single values and addAll* for Set<> parameter:
                String superAdderName;
                if(isParameterSingleValue) {
                    superAdderName = asBuildableProperty(superProperty).buildAdder(domainClazz, config).name;
                } else {
                    superAdderName = asBuildableProperty(superProperty).buildAdderAll(domainClazz, config).name;
                }

                stub.addStatement("this._invokeResourceObjectMethodIfExists($S, $N)", superAdderName, paramName);
            }
        }
        stub.endControlFlow();

        // Generate code for clearing subproperties:
        stub.addComment("All subproperties loose their values:");
        for(RDFSProperty subProperty : getSubProperties()) {
            if(!isFromSpecialVocabulary(subProperty) && !subProperty.equals(this)) {
                String subPropertySetterName = ((BuildableRDFSProperty) subProperty).buildSetter(domainClazz, config).name;
                stub.addStatement("this._invokeResourceObjectMethodIfExists($S, new $T())", subPropertySetterName, ClassName.get(HashSet.class));
            }
        }

        // Override annotation of the method:
        AnnotationSpec overrideAnnotation = AnnotationSpec.builder(Override.class).build();

        stub.addAnnotation(overrideAnnotation)
                .addStatement("this.$N.clear()", field);

        if(isParameterSingleValue) {
            stub.addStatement("this.$N.add($N)", field, paramName);
        } else if(isVarArg) {
            stub.addStatement("this.$N.addAll($T.asList($N))", field, ClassName.get(Arrays.class), paramName);
        } else {
            stub.addStatement("this.$N.addAll($N)", field, paramName);
        }

        return stub;
    }
}
