package com.github.anno4j.schema_parsing.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.RDFS;
import com.github.anno4j.model.namespaces.XSD;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.mapping.DatatypeMapper;
import com.github.anno4j.schema_parsing.mapping.IllegalMappingException;
import com.github.anno4j.schema_parsing.model.ExtendedRDFSClazz;
import com.github.anno4j.schema_parsing.model.ExtendedRDFSProperty;
import com.github.anno4j.schema.model.rdfs.RDFSClazzSupport;
import com.github.anno4j.schema_parsing.naming.ClassNameBuilder;
import com.github.anno4j.schema_parsing.naming.IdentifierBuilder;
import com.squareup.javapoet.ClassName;
import org.openrdf.repository.object.LangString;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Support class for {@link ExtendedRDFSClazz} implementing basic methods
 * for JavaPoet class generation for this class.
 */
@Partial
public abstract class ClazzBuildingSupport extends RDFSClazzSupport implements ExtendedRDFSClazz {

    @Override
    public String getJavaPackageName() {
        try {
            return ClassNameBuilder.builder(getResourceAsString()).packageName();
        } catch (URISyntaxException e) {
            return "";
        }
    }

    /**
     * Checks if the given type is a Anno4j supported Java type. These are:
     * <ul>
     *  <li>Java primitive corresponding classes ({@link Integer}, {@link Double}, ...)</li>
     *  <li>{@link CharSequence}</li>
     *  <li>{@link String}</li>
     *  <li>{@link org.openrdf.repository.object.LangString}</li>
     * </ul>
     * @param type The type to check.
     * @return Returns true iff the type is allowed.
     */
    private static boolean isSupportedJavaType(Class<?> type) {
        Collection<Class<?>> supportedTypes = Arrays.asList(Boolean.class,
                Byte.class,
                Character.class,
                Double.class,
                Float.class,
                Integer.class,
                Long.class,
                Short.class,
                CharSequence.class,
                String.class,
                LangString.class);
        return supportedTypes.contains(type);
    }

    @Override
    public ClassName getJavaPoetClassName(OntGenerationConfig config) {
        if (getResourceAsString().equals(RDFS.CLAZZ) || getResourceAsString().equals(RDFS.RESOURCE)) {
            return ClassName.get(ResourceObject.class);

        }

        // Capture XSD datatypes:
        if (getResourceAsString().startsWith(XSD.NS)) {
            switch (getResourceAsString()) {
                case XSD.STRING:
                    return ClassName.get(CharSequence.class);
                case XSD.BOOLEAN:
                    return ClassName.get(Boolean.class);
                case XSD.DECIMAL:
                    return ClassName.get(Double.class);
                case XSD.FLOAT:
                    return ClassName.get(Float.class);
                case XSD.DOUBLE:
                    return ClassName.get(Double.class);
                case XSD.HEX_BINARY:
                    return ClassName.get(String.class);
                case XSD.BASE64_BINARY:
                    return ClassName.get(String.class);
                case XSD.ANY_URI:
                    return ClassName.get(CharSequence.class);
                case XSD.NORMALIZED_STRING:
                    return ClassName.get(CharSequence.class);
                case XSD.TOKEN:
                    return ClassName.get(CharSequence.class);
                case XSD.LANGUAGE:
                    return ClassName.get(String.class);
                case XSD.INTEGER:
                    return ClassName.get(Integer.class);
                case XSD.NON_POSITIVE_INTEGER:
                    return ClassName.get(Integer.class);
                case XSD.NEGATIVE_INTEGER:
                    return ClassName.get(Integer.class);
                case XSD.LONG:
                    return ClassName.get(Long.class);
                case XSD.INT:
                    return ClassName.get(Integer.class);
                case XSD.SHORT:
                    return ClassName.get(Short.class);
                case XSD.BYTE:
                    return ClassName.get(Byte.class);
                case XSD.NON_NEGATIVE_INTEGER:
                    return ClassName.get(Integer.class);
                case XSD.UNSIGNED_LONG:
                    return ClassName.get(Long.class);
                case XSD.UNSIGNED_INT:
                    return ClassName.get(Integer.class);
                case XSD.UNSIGNED_SHORT:
                    return ClassName.get(Short.class);
                case XSD.UNSIGNED_BYTE:
                    return ClassName.get(Byte.class);
                case XSD.POSITIVE_INTEGER:
                    return ClassName.get(Integer.class);
                default:
                    return ClassName.get(CharSequence.class);
            }
        }

        /**
         * If this class is an unknown literal, return char sequence:
         */
        if(isLiteral()) {

            // Check if there is a datatype mapper for this datatype:
            for(DatatypeMapper mapper : config.getDatatypeMappers()) {

                Class<?> javaType = mapper.mapType(this);
                if(javaType != null) {
                    // Validate that the mapping is a Anno4j supported type:
                    if(isSupportedJavaType(javaType)) {
                        return ClassName.get(javaType);
                    } else {
                        throw new IllegalMappingException(javaType.getName() + " is not a Java type supported by Anno4j.");
                    }
                }
            }
            // If no mapper was found, return CharSequence as a general type:
            return ClassName.get(CharSequence.class);
        }

        // First try to find a name for this class:
        try {
            ClassNameBuilder nameBuilder = ClassNameBuilder.builder(getResourceAsString());

            // Identifier building is enhanced by rdfs:label literals:
            if (getLabels() != null && !getLabels().isEmpty()) {
                CharSequence preferredLabel = getPreferredRDFSLabel(config);
                if (preferredLabel != null) {
                    nameBuilder = nameBuilder.withRDFSLabel(preferredLabel.toString());
                }
            }

            // Add information about incoming and outgoing properties:
            for (ExtendedRDFSProperty incomingProp : getIncomingProperties()) {
                nameBuilder = nameBuilder.withIncomingProperty(incomingProp);
            }
            for (ExtendedRDFSProperty outgoingProp : getOutgoingProperties()) {
                nameBuilder = nameBuilder.withOutgoingProperty(outgoingProp);
            }

            return nameBuilder.className();

        } catch (IdentifierBuilder.NameBuildingException | URISyntaxException e1) {
            // On error, try to derive at least a package name from the resource:
            String packageName;
            try {
                packageName = ClassNameBuilder.builder(getResourceAsString())
                        .packageName();
            } catch (URISyntaxException e2) {
                packageName = ""; // Fall back to default package
            }
            // Generate a unique identifier from the resources id:
            return ClassName.get(packageName, "UnnamedClass" + getResourceAsString().hashCode());
        }
    }

    /**
     * Returns the rdfs:label which is preferred for identifier names
     * according to the configuration object provided.
     *
     * @param config The configuration object.
     * @return The preferred rdfs:label literal.
     */
    private CharSequence getPreferredRDFSLabel(OntGenerationConfig config) {
        // Get the label in the preferred language:
        CharSequence preferredLabel = null;
        for (CharSequence label : getLabels()) {
            if (config.isPreferredForIdentifiers(label, preferredLabel)) {
                preferredLabel = label;
            }
        }

        return preferredLabel;
    }

    /**
     * Returns the rdfs:comment which is preferred for JavaDoc
     * according to the configuration object provided.
     *
     * @param config The configuration object.
     * @return The preferred rdfs:comment literal.
     */
    CharSequence getPreferredRDFSComment(OntGenerationConfig config) {
        CharSequence preferredComment = null;
        for (CharSequence comment : getComments()) {
            if (config.isPreferredForJavaDoc(comment, preferredComment)) {
                preferredComment = comment;
            }
        }

        return preferredComment;
    }

    @Override
    public boolean hasPropertyTransitive(ExtendedRDFSProperty property) {
        // If the class has the property, immediately return:
        if (getOutgoingProperties().contains(property)) {
            return true;
        }

        // Recursively search in superclasses:
        for (ExtendedRDFSClazz superClazz : getSuperclazzes()) {
            if (superClazz.hasPropertyTransitive(property)) {
                return true;
            }
        }

        // The property was neither found in this class or any superclass:
        return false;
    }
}
