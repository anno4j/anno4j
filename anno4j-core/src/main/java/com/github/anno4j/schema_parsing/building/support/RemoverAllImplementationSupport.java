package com.github.anno4j.schema_parsing.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.model.ExtendedRDFSClazz;
import com.github.anno4j.schema_parsing.model.ExtendedRDFSProperty;
import com.github.anno4j.schema_parsing.model.RDFSProperty;
import com.squareup.javapoet.*;

/**
 * Support class (of {@link ExtendedRDFSProperty}) for generating
 * removeAll* methods of support classes.
 */
@Partial
public abstract class RemoverAllImplementationSupport extends RemoverAllSupport implements ExtendedRDFSProperty {

    @Override
    public MethodSpec buildRemoverAllImplementation(OntGenerationConfig config) {
        // Get the signature of a remover for this property:
        MethodSpec signature = buildSignature(config);

        if(signature != null) {
            // We will extend the signature by its implementation:
            MethodSpec.Builder removerAllBuilder = signature.toBuilder();

            // Override annotation of the method:
            AnnotationSpec overrideAnnotation = AnnotationSpec.builder(Override.class).build();

            // Get the Java class name of the properties range:
            ExtendedRDFSClazz range = findSingleRangeClazz();
            ClassName rangeClassName = range.getJavaPoetClassName(config);

            // Get a JavaPoet name for the methods single parameter:
            String paramName = signature.parameters.get(0).name;
            TypeName paramType = ParameterizedTypeName.get(ClassName.get("java.util", "Set"), rangeClassName);

            // Prepare iterating the values of the parameter set:
            ParameterSpec param = ParameterSpec.builder(paramType, paramName).build();
            ParameterSpec current = ParameterSpec.builder(rangeClassName, "current").build();
            MethodSpec remover = buildRemover(config);

            // Prepare Set and HashSet types:
            TypeName set = ParameterizedTypeName.get(ClassName.get("java.util", "Set"), rangeClassName);
            TypeName hashSet = ParameterizedTypeName.get(ClassName.get("java.util", "HashSet"), rangeClassName);

            // Get the annotated field for this property:
            FieldSpec field = buildAnnotatedField(config);

            // Iterate the input values and check if the value was actually removed from this property:
            removerAllBuilder.addStatement("boolean changed = false")
                    .addStatement("$T _containedValues = new $T()", set, hashSet)
                    .beginControlFlow("for($T $N : $N)", rangeClassName, current, param)
                    .beginControlFlow("if(this.$N.contains($N))", field, current) // If the value is actual a value of this property
                    .addStatement("changed |= true") // Set changed flag
                    .addStatement("_containedValues.add($N)", current) // Add to collection of removed values
                    .endControlFlow() // End if
                    .endControlFlow(); // End for

            // Only propagate removal if there is actually something to be removed:
            removerAllBuilder.beginControlFlow("if(!_containedValues.isEmpty())");

            removerAllBuilder.addStatement("this.$N.removeAll(_containedValues)", field);

            // The values can be safely removed also from superproperties
            // if they were actually removed from this one (see above generated if-clause):
            removerAllBuilder.addComment("Remove from superproperties:");
            for (ExtendedRDFSProperty superProperty : getSuperproperties()) {
                String superRemoverAllName = superProperty.buildRemoverAll(config).name;
                removerAllBuilder.addStatement("this._invokeResourceObjectMethodIfExists($S, _containedValues)", superRemoverAllName);
            }

            // The value can be safely removed from subproperties
            // if it was actually removed from this one (see above generated if-clause):
            removerAllBuilder.addComment("Remove values from subproperties:");
            for(RDFSProperty subProperty : getSubProperties()) {
                String subPropertyRemoverAllName = ((ExtendedRDFSProperty) subProperty).buildRemoverAll(config).name;

                removerAllBuilder.addStatement("this._invokeResourceObjectMethodIfExists($S, _containedValues)", subPropertyRemoverAllName);
            }

            removerAllBuilder.endControlFlow(); // End if(!_containedValues.isEmpty())

            removerAllBuilder.addStatement("return changed");

            // Add the override annotation and output:
            return removerAllBuilder.addAnnotation(overrideAnnotation)
                                    .build();

        } else {
            return null;
        }
    }
}
