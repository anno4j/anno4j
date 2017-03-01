package com.github.anno4j.rdfs_parser.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.rdfs_parser.building.OntGenerationConfig;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSClazz;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSProperty;
import com.squareup.javapoet.*;

import java.util.Set;

/**
 * Support class (of {@link ExtendedRDFSProperty}) for generating reomve-methods for
 * the corrpesonding property.
 */
@Partial
public abstract class RemoverImplementationSupport extends RemoverSupport implements ExtendedRDFSProperty {

    @Override
    public MethodSpec buildRemoverImplementation(OntGenerationConfig config) {
        // Get the signature of a remove* method for this property:
        MethodSpec signature = buildRemoverSignature(config);
        if(signature != null) {
            MethodSpec.Builder removerBuilder = signature.toBuilder();

            // Implementations in support classes have the @Override annotation:
            AnnotationSpec overrideAnnotation = AnnotationSpec.builder(Override.class)
                    .build();

            // Add actual adding code:
            MethodSpec getter = buildGetter(config);
            MethodSpec setter = buildSetter(config);

            // Get the class name of the properties range:
            ExtendedRDFSClazz range = findSingleRangeClazz();
            ClassName rangeClassName = range.getJavaPoetClassName(config);

            // Prepare class names of used types:
            ClassName set = ClassName.get(Set.class);
            TypeName rangeSet = ParameterizedTypeName.get(set, rangeClassName);
            ParameterSpec param = signature.parameters.get(0);

            // Add the actual removal code:
            removerBuilder.addStatement("$T values = $N()", rangeSet, getter)
                    .addStatement("boolean removed = values.remove($N)", param)
                    .addStatement("$N(values)", setter)
                    .beginControlFlow("if(removed)");

            // The value can be safely removed also from superproperties
            // if it was actually removed from this one (see above generated if-clause):
            for (ExtendedRDFSProperty superProperty : getSuperproperties()) {
                MethodSpec superRemover = superProperty.buildRemover(config);
                removerBuilder.addStatement("$N($N)", superRemover, param);
            }

            removerBuilder.endControlFlow()
                          .addStatement("return removed");

            // Build the removers method specification:
            return removerBuilder.addAnnotation(overrideAnnotation)
                    .build();

        } else {
            return null;
        }
    }
}
