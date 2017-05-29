package com.github.anno4j.schema_parsing.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.github.anno4j.schema.model.rdfs.RDFSProperty;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.model.BuildableRDFSClazz;
import com.github.anno4j.schema_parsing.model.BuildableRDFSProperty;
import com.github.anno4j.schema_parsing.validation.Validator;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import org.openrdf.repository.RepositoryException;

/**
 * Support class (of {@link BuildableRDFSProperty}) for generating add-methods for
 * the corresponding property.
 */
@Partial
public abstract class AdderImplementationSupport extends AdderSupport implements BuildableRDFSProperty {

    @Override
    public MethodSpec buildAdderImplementation(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException {
        // Get the signature of a add* method for this property:
        MethodSpec adder = buildSignature(domainClazz, config);
        if(adder != null) {
            MethodSpec.Builder adderBuilder = adder.toBuilder();

            // Implementations in support classes have the @Override annotation:
            AnnotationSpec overrideAnnotation = AnnotationSpec.builder(Override.class)
                    .build();

            // Add validation code:
            BuildableRDFSClazz range = findSingleRangeClazz();
            ParameterSpec param = adder.parameters.get(0);
            for (Validator validator : config.getValidators()) {
                if (validator.isValueSpaceConstrained(range)) {
                    validator.addValueSpaceCheck(adderBuilder, param, range);
                }
            }

            // Generate code for adding also to superproperties:
            for (RDFSProperty superProperty : getSuperproperties()) {
                // Ignore superproperties from special vocabulary and the reflexive relation:
                if(!isFromSpecialVocabulary(superProperty) && !superProperty.equals(this)) {
                    String superAdderName = asBuildableProperty(superProperty).buildAdder(domainClazz, config).name;
                    adderBuilder.addStatement("this._invokeResourceObjectMethodIfExists($S, $N)", superAdderName, param);
                }
            }

            // Get the annotated field for this property:
            FieldSpec field = buildAnnotatedField(domainClazz, config);

            // Build the adders method specification:
            return adderBuilder.addAnnotation(overrideAnnotation)
                                .addStatement("this.$N.add($N)", field, param) // Actual adding code
                                .build();

        } else {
            return null;
        }
    }
}
