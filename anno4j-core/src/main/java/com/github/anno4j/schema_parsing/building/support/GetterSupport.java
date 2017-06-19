package com.github.anno4j.schema_parsing.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.model.BuildableRDFSClazz;
import com.github.anno4j.schema_parsing.model.BuildableRDFSProperty;
import com.github.anno4j.schema_parsing.naming.MethodNameBuilder;
import com.squareup.javapoet.*;
import org.openrdf.repository.RepositoryException;

import javax.lang.model.element.Modifier;

/**
 * Support class (of {@link BuildableRDFSProperty}) for generating getter methods
 * for this property.
 */
@Partial
public abstract class GetterSupport extends PropertyBuildingSupport implements BuildableRDFSProperty {

    /**
     * Returns whether this getter should have a single value return type.
     * This is the case when the property has a fixed cardinality of one.
     * @param domainClazz The class for which to generate the getter.
     * @return Returns true iff the getter should have a single value return type.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    boolean hasSingleValueReturnType(RDFSClazz domainClazz) throws RepositoryException {
        // If there is cardinality set to one for this property, let it have a single value return type:
        Integer cardinality = getCardinality(domainClazz);
        return cardinality != null && cardinality == 1;
    }

    @Override
    MethodSpec buildSignature(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException {
        if (getRanges() != null) {
            // JavaDoc of the method:
            CodeBlock.Builder javaDoc = CodeBlock.builder();
            if (getComments() != null && !getComments().isEmpty()) {
                CharSequence preferredComment = getPreferredRDFSComment(config);
                if (preferredComment != null) {
                    javaDoc.add(preferredComment.toString());
                }
            }

            // Find most specific common superclass:
            BuildableRDFSClazz rangeClazz = findSingleRangeClazz();

            // Get the type of return type elements:
            TypeName valueType = rangeClazz.getJavaPoetClassName(config);

            // For convenience the return type for strings should be a wildcard, i.e. Set<? extends CharSequence>:
            if(!hasSingleValueReturnType(domainClazz) && valueType.equals(ClassName.get(CharSequence.class))) {
                valueType = WildcardTypeName.subtypeOf(valueType);
            }

            TypeName returnType;
            if(hasSingleValueReturnType(domainClazz)) {
                returnType = valueType;

            } else { // Otherwise it has Set return type:
                ClassName set = ClassName.get("java.util", "Set");
                returnType = ParameterizedTypeName.get(set, valueType);
            }

            return MethodNameBuilder.forObjectRepository(getObjectConnection())
                    .getJavaPoetMethodSpec("get", this, config, !hasSingleValueReturnType(domainClazz))
                    .toBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .returns(returnType)
                    .addJavadoc(javaDoc.build())
                    .build();

        } else {
            return null;
        }
    }

    @Override
    public MethodSpec buildGetter(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException {
        return buildSignature(domainClazz, config)
                .toBuilder()
                .addModifiers(Modifier.ABSTRACT)
                .build();
    }
}
