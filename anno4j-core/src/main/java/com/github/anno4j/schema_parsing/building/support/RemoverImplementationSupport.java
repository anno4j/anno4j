package com.github.anno4j.schema_parsing.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.model.ExtendedRDFSClazz;
import com.github.anno4j.schema_parsing.model.ExtendedRDFSProperty;
import com.github.anno4j.model.rdfs.RDFSProperty;
import com.squareup.javapoet.*;

import java.util.Set;

/**
 * Support class (of {@link ExtendedRDFSProperty}) for generating remove-methods for
 * the corresponding property.
 */
@Partial
public abstract class RemoverImplementationSupport extends RemoverSupport implements ExtendedRDFSProperty {

    @Override
    public MethodSpec buildRemoverImplementation(OntGenerationConfig config) {
        // Get the signature of a remove* method for this property:
        MethodSpec signature = buildSignature(config);
        if(signature != null) {
            MethodSpec.Builder removerBuilder = signature.toBuilder();

            // Implementations in support classes have the @Override annotation:
            AnnotationSpec overrideAnnotation = AnnotationSpec.builder(Override.class)
                    .build();

            // Get the class name of the properties range:
            ExtendedRDFSClazz range = findSingleRangeClazz();
            TypeName rangeType = range.getJavaPoetClassName(config);

            // For string types the return type of getters is a wildcard type:
            if(rangeType.equals(ClassName.get(CharSequence.class))) {
                rangeType = WildcardTypeName.subtypeOf(rangeType);
            }

            // Prepare class names of used types:
            ClassName set = ClassName.get(Set.class);
            TypeName rangeSet = ParameterizedTypeName.get(set, rangeType);
            ParameterSpec param = signature.parameters.get(0);

            // Get the annotated field for this property:
            FieldSpec field = buildAnnotatedField(config);

            // Add the actual removal code:
            removerBuilder.addStatement("boolean contained = this.$N.contains($N)", field, param)
                    .addStatement("this.$N.remove($N)", field, param)
                    .addComment("Cascade changes if value was actually set for this property:")
                    .beginControlFlow("if(contained)");

            // The value can be safely removed also from superproperties
            // if it was actually removed from this one (see above generated if-clause):
            removerBuilder.addComment("Remove from superproperties:");
            for (ExtendedRDFSProperty superProperty : getSuperproperties()) {
                String superRemoverName = superProperty.buildRemover(config).name;
                removerBuilder.addStatement("this._invokeResourceObjectMethodIfExists($S, $N)", superRemoverName, param);
            }

            // Same for subproperties. The value can be safely removed from subproperties
            // if it was actually removed from this one (see above generated if-clause):
            removerBuilder.addComment("Remove from subproperties:");
            for(RDFSProperty subProperty : getSubProperties()) {
                String subPropertyRemoverName = ((ExtendedRDFSProperty) subProperty).buildRemover(config).name;

                removerBuilder.addStatement("this._invokeResourceObjectMethodIfExists($S, $N)", subPropertyRemoverName, param);
            }

            removerBuilder.endControlFlow() // End if(removed)
                          .addStatement("return contained");

            // Build the removers method specification:
            return removerBuilder.addAnnotation(overrideAnnotation)
                    .build();

        } else {
            return null;
        }
    }
}
