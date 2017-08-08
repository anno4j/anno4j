package com.github.anno4j.schema_parsing.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.model.BuildableRDFSClazz;
import com.github.anno4j.schema_parsing.model.BuildableRDFSProperty;
import com.squareup.javapoet.*;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.advisers.helpers.PropertySet;

import java.util.HashSet;
import java.util.Set;

/**
 * Support class (of {@link BuildableRDFSProperty}) for generating
 * removeAll* methods of support classes.
 */
@Partial
public abstract class RemoverAllImplementationSupport extends RemoverAllSupport implements BuildableRDFSProperty {

    @Override
    public MethodSpec buildRemoverAllImplementation(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException {
        // Get the signature of a remover for this property:
        MethodSpec signature = buildSignature(domainClazz, config);

        if(signature != null) {
            // We will extend the signature by its implementation:
            MethodSpec.Builder removerAllBuilder = signature.toBuilder();

            // Override annotation of the method:
            AnnotationSpec overrideAnnotation = AnnotationSpec.builder(Override.class).build();

            // Get the Java class name of the properties range:
            BuildableRDFSClazz range = findSingleRangeClazz();
            ClassName rangeClassName = range.getJavaPoetClassName(config);

            // Get a JavaPoet name for the methods single parameter:
            String paramName = signature.parameters.get(0).name;
            TypeName paramType = ParameterizedTypeName.get(ClassName.get("java.util", "Set"), rangeClassName);

            // Prepare iterating the values of the parameter set:
            ParameterSpec param = ParameterSpec.builder(paramType, paramName).build();
            ParameterSpec current = ParameterSpec.builder(rangeClassName, "current").build();
            MethodSpec remover = buildRemover(domainClazz, config);

            // Prepare types:
            TypeName set = ParameterizedTypeName.get(ClassName.get(Set.class), rangeClassName);
            TypeName hashSet = ParameterizedTypeName.get(ClassName.get(HashSet.class), rangeClassName);

            // Iterate the input values and check if the value was actually removed from this property:
            removerAllBuilder.addStatement("boolean changed = false");

            // Get the value(s) returned by getter:
            MethodSpec getter = buildGetter(domainClazz, config);
            if (isSingleValueProperty(domainClazz)) {
                removerAllBuilder.addStatement("$T _oldValues = new $T()", set, hashSet)
                            .addStatement("_oldValues.add($N())", getter);
            } else {
                removerAllBuilder.addStatement("$T _oldValues = $N()", set, getter);
            }

            removerAllBuilder.addStatement("$T _containedValues = new $T()", set, hashSet)
                            .beginControlFlow("for($T $N : $N)", rangeClassName, current, param)
                            .beginControlFlow("if(_oldValues.contains($N))", current) // If the value is actual a value of this property
                            .addStatement("changed |= true") // Set changed flag
                            .addStatement("_containedValues.add($N)", current) // Add to collection of removed values
                            .endControlFlow() // End if
                            .endControlFlow(); // End for

            // Only propagate removal if there is actually something to be removed:
            removerAllBuilder.beginControlFlow("if(!_containedValues.isEmpty())");

            // Remove all values and sanitize schema afterwards using SchemaSanitizingObjectSupport:
            removerAllBuilder.beginControlFlow("for($T _current : _containedValues)", rangeClassName)
                             .addStatement("removeValue($S, _current)", getResourceAsString())
                             .endControlFlow()
                             .addStatement("sanitizeSchema($S)", getResourceAsString());

            TypeName propertySet = ClassName.get(PropertySet.class);
            removerAllBuilder.endControlFlow() // End if(!_containedValues.isEmpty())
                             .addComment("Refresh values:")
                             .beginControlFlow("if($N() instanceof $T)", getter, propertySet)
                             .addStatement("(($T) $N()).refresh()", propertySet, getter)
                            .endControlFlow();

            removerAllBuilder.addStatement("return changed");

            // Add the override annotation and output:
            return removerAllBuilder.addAnnotation(overrideAnnotation)
                                    .build();

        } else {
            return null;
        }
    }
}
