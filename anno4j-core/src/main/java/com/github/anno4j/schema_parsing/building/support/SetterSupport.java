package com.github.anno4j.schema_parsing.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.model.BuildableRDFSProperty;
import com.squareup.javapoet.*;
import org.openrdf.repository.RepositoryException;

import javax.lang.model.element.Modifier;

/**
 * Support class belonging to {@link BuildableRDFSProperty} that adds functionality
 * to generate a JavaPoet specification for a setter method of this property.
 */
@Partial
public abstract class SetterSupport extends SetterBuildingSupport implements BuildableRDFSProperty {

    /**
     * Generates the signature of a setter method for this property.
     * The signature is in the following format:<br>
     * <code>public void set%Property-Name%(Set<%Range%>)</code>
     * Note that no annotations are added to the signature.
     * JavaDoc is added to the signature if possible.
     *
     * @param domainClazz The class in which context to generate a setter.
     * @param config Configuration of the generation process, e.g. which
     *               language to use for the JavaDoc.
     * @return Returns the JavaPoet specification of the signature.
     */
    @Override
    MethodSpec buildSignature(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException {
        // Get the setters signature without parameter:
        MethodSpec.Builder setter = buildParameterlessSetterSignature(domainClazz, config);

        // Different parameter types are generated for cardinality one and for higher cardinality:
        boolean singleValueParameter = hasSingleValueParameter(domainClazz);

        TypeName paramType = getParameterType(config, !singleValueParameter);

        if(!singleValueParameter) {
            // Use Set type parameter for higher cardinalities:
            ClassName set = ClassName.get("java.util", "Set");
            paramType = ParameterizedTypeName.get(set, paramType);
        }

        return setter.addParameter(paramType, "values")
                     .build();
    }

    @Override
    public MethodSpec buildSetter(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException {
        // Get the signature of a setter for this property:
        MethodSpec signature = buildSignature(domainClazz, config);

        if (signature != null) {
            // Add an abstract modifier to the signature:
            return signature.toBuilder()
                    .addModifiers(Modifier.ABSTRACT)
                    .build();

        } else {
            return null;
        }
    }
}
