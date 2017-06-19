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
 * Support class (of {@link BuildableRDFSProperty}) for generating resource class removeAll-methods
 * for this property.
 */
@Partial
public abstract class RemoverAllSupport extends PropertyBuildingSupport implements BuildableRDFSProperty {

    @Override
    MethodSpec buildSignature(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException {
        if(getRanges() != null) {
            // Get the most specific class describing all of the properties range classes:
            BuildableRDFSClazz range = findSingleRangeClazz();
            ClassName set = ClassName.get("java.util", "Set");

            // Get the type of parameter type elements:
            TypeName setType = range.getJavaPoetClassName(config);

            // For convenience the parameter type for strings should be a wildcard, i.e. Set<? extends CharSequence>:
            if(setType.equals(ClassName.get(CharSequence.class))) {
                setType = WildcardTypeName.subtypeOf(setType);
            }

            TypeName paramType = ParameterizedTypeName.get(set, setType);

            // Generate Javadoc if a rdfs:comment literal is available:
            CodeBlock.Builder javaDoc = CodeBlock.builder();
            CharSequence preferredComment = getPreferredRDFSComment(config);
            if (preferredComment != null) {
                javaDoc.add(preferredComment.toString());
            }
            javaDoc.add("\n@param values The elements to be removed.");
            javaDoc.add("\n@return Returns true if any value was removed.");

            return MethodNameBuilder.forObjectRepository(getObjectConnection())
                    .getJavaPoetMethodSpec("removeAll", this, config, true)
                    .toBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(paramType, "values")
                    .addJavadoc(javaDoc.build())
                    .returns(boolean.class)
                    .build();

        } else {
            return null;
        }
    }

    @Override
    public MethodSpec buildRemoverAll(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException {
        // Add the abstract modifier to the signature, because there is no implementation in the interface:
        return buildSignature(domainClazz, config)
                .toBuilder()
                .addModifiers(Modifier.ABSTRACT)
                .build();
    }
}
