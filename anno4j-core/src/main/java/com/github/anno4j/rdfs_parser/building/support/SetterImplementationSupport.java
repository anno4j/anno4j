package com.github.anno4j.rdfs_parser.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.rdfs_parser.building.OntGenerationConfig;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSClazz;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSProperty;
import com.github.anno4j.rdfs_parser.model.RDFSProperty;
import com.github.anno4j.rdfs_parser.validation.Validator;
import com.squareup.javapoet.*;

import java.util.HashSet;

/**
 * Support class (of {@link ExtendedRDFSProperty}) for generating
 * setter methods of support classes.
 */
@Partial
public abstract class SetterImplementationSupport extends SetterSupport implements ExtendedRDFSProperty {

    @Override
    public MethodSpec buildSetterImplementation(OntGenerationConfig config) {
        // Get the signature of a setter for this property:
        MethodSpec signature = buildSignature(config);

        if(signature != null) {
            // Override annotation of the method:
            AnnotationSpec overrideAnnotation = AnnotationSpec.builder(Override.class).build();

            MethodSpec.Builder setterBuilder = signature.toBuilder();

            ExtendedRDFSClazz range = findSingleRangeClazz();
            ClassName rangeClassName = range.getJavaPoetClassName(config);
            String paramName = signature.parameters.get(0).name;
            ParameterSpec current = ParameterSpec.builder(rangeClassName, "current").build();

            // Add validation code:
            for (Validator validator : config.getValidators()) {
                if(validator.isValueSpaceConstrained(range)) {
                    setterBuilder.beginControlFlow("for($T $N : $N)", rangeClassName, current, paramName);
                    validator.addValueSpaceCheck(setterBuilder, current, range);
                    setterBuilder.endControlFlow();
                }
            }

            // Get the annotated field for this property:
            FieldSpec field = buildAnnotatedField(config);

            // Remove all old values from superproperties:
            setterBuilder.addComment("Remove old values from superproperties:")
                         .beginControlFlow("if(!this.$N.isEmpty())", field);
            for (ExtendedRDFSProperty superProperty : getSuperproperties()) {
                MethodSpec superPropertyRemoverAll = superProperty.buildRemoverAll(config);
                setterBuilder.addStatement("$N($N)", superPropertyRemoverAll, field);
            }
            setterBuilder.endControlFlow(); // End if(!field.isEmpty())

            // Add new values to superproperties:
            setterBuilder.addComment("Add new values to superproperties:")
                         .beginControlFlow("if(!$N.isEmpty())", paramName);
            for (ExtendedRDFSProperty superProperty : getSuperproperties()) {
                String superAdderAllName = superProperty.buildAdderAll(config).name;
                setterBuilder.addStatement("this._invokeResourceObjectMethodIfExists($S, $N)", superAdderAllName, paramName);
            }
            setterBuilder.endControlFlow();

            // Generate code for clearing subproperties:
            setterBuilder.addComment("All subproperties loose their values:");
            for(RDFSProperty subProperty : getSubProperties()) {
                String subPropertySetterName = ((ExtendedRDFSProperty) subProperty).buildSetter(config).name;

                setterBuilder.addStatement("this._invokeResourceObjectMethodIfExists($S, new $T())", subPropertySetterName, ClassName.get(HashSet.class));
            }

            return setterBuilder.addAnnotation(overrideAnnotation)
                                .addStatement("this.$N.clear()", field)
                                .addStatement("this.$N.addAll($N)", field, paramName)
                                .build();

        } else {
            return null;
        }
    }
}
