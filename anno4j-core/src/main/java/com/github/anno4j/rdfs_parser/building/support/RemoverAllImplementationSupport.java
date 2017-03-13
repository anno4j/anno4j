package com.github.anno4j.rdfs_parser.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.rdfs_parser.building.OntGenerationConfig;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSClazz;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSProperty;
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

            // Prepare used methods:
            MethodSpec getter = buildGetter(config);

            // Iterate the input values and check if the value was actually removed from this property:
            removerAllBuilder.addStatement("boolean changed = false")
                    .addStatement("$T removedValues = new $T()", set, hashSet)
                    .beginControlFlow("for($T $N : $N)", rangeClassName, current, param)
                    .beginControlFlow("if($N().contains($N))", getter, current) // If the value is actual a value of this property
                    .addStatement("changed |= true") // Set changed flag
                    .addStatement("removedValues.add($N)", current) // Add to collection of removed values
                    .endControlFlow() // End if
                    .endControlFlow() // End for
                    .beginControlFlow("for($T $N : removedValues)", rangeClassName, current)
                    .addStatement("$N($N)", remover, current);

            // If the value was actually removed, we can safely remove it also from superproperties:
            for (ExtendedRDFSProperty superProp : getSuperproperties()) {
                MethodSpec superPropRemover = superProp.buildRemover(config);
                removerAllBuilder.addStatement("$N($N)", superPropRemover, current);
            }

            removerAllBuilder.endControlFlow() // Add second for
                             .addStatement("return changed");

            // Add the override annotation and output:
            return removerAllBuilder.addAnnotation(overrideAnnotation)
                                    .build();

        } else {
            return null;
        }
    }
}
