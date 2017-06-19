package com.github.anno4j.schema_parsing.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.model.BuildableRDFSClazz;
import com.github.anno4j.schema_parsing.model.BuildableRDFSProperty;
import com.github.anno4j.schema_parsing.naming.MethodNameBuilder;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import org.openrdf.repository.RepositoryException;

import javax.lang.model.element.Modifier;

/**
 * Support class (of {@link BuildableRDFSProperty}) for generating resource class add-methods
 * for this property.
 */
@Partial
public abstract class AdderSupport extends PropertyBuildingSupport implements BuildableRDFSProperty {

    @Override
    MethodSpec buildSignature(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException {
        if(getRanges() != null) {
            // Get the most specific class describing all of the properties range classes:
            ClassName paramType = getRangeJavaPoetClassName(config);

            // Generate Javadoc if a rdfs:comment literal is available:
            CodeBlock.Builder javaDoc = CodeBlock.builder();
            CharSequence preferredComment = getPreferredRDFSComment(config);
            if (preferredComment != null) {
                javaDoc.add(preferredComment.toString());
            }
            javaDoc.add("\n@param value The element to be added.");

            // Add a throws declaration if the value space is constrained:
            BuildableRDFSClazz range = findSingleRangeClazz();
            addJavaDocExceptionInfo(javaDoc, range, config);

            return MethodNameBuilder.forObjectRepository(getObjectConnection())
                    .getJavaPoetMethodSpec("add", this, config, false)
                    .toBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(paramType, "value")
                    .addJavadoc(javaDoc.build())
                    .returns(void.class)
                    .build();

        } else {
            return null;
        }
    }

    @Override
    public MethodSpec buildAdder(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException {
        // Add the abstract modifier to the signature, because there is no implementation in the interface:
        return buildSignature(domainClazz, config)
                .toBuilder()
                .addModifiers(Modifier.ABSTRACT)
                .build();
    }
}
