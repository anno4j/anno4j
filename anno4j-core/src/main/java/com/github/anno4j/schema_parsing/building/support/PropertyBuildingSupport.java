package com.github.anno4j.schema_parsing.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.github.anno4j.schema.model.rdfs.RDFSPropertySupport;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.model.*;
import com.github.anno4j.schema_parsing.naming.IdentifierBuilder;
import com.github.anno4j.schema_parsing.util.LowestCommonSuperclass;
import com.github.anno4j.schema_parsing.validation.Validator;
import com.squareup.javapoet.*;
import org.openrdf.annotations.Iri;

import javax.lang.model.element.Modifier;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Support class providing utility functionality for generating
 * methods for the property.
 */
@Partial
public abstract class PropertyBuildingSupport extends RDFSPropertySupport implements ExtendedRDFSProperty {

    /**
     * Generates the signature of a method for this property.
     * The signature is in the following format:<br>
     * <code>public void set%Property-Name%(Set<%Range%>)</code>
     * Note that no annotations are added to the signature.
     * JavaDoc is added to the signature if possible.
     *
     * @param config Configuration of the generation process, e.g. which
     *               language to use for the JavaDoc.
     * @return Returns the JavaPoet specification of the signature.
     */
    abstract MethodSpec buildSignature(OntGenerationConfig config);

    /**
     * {@inheritDoc}
     */
    @Override
    public ClassName getDomainJavaPoetClassName(OntGenerationConfig config) {
        ExtendedRDFSClazz domainClazz = findSingleDomainClazz();
        if (domainClazz != null) {
            return domainClazz.getJavaPoetClassName(config);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClassName getRangeJavaPoetClassName(OntGenerationConfig config) {
        ExtendedRDFSClazz rangeClazz = findSingleRangeClazz();
        if (rangeClazz != null) {
            return rangeClazz.getJavaPoetClassName(config);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IdentifierBuilder getIdentifierBuilder(OntGenerationConfig config) {
        return IdentifierBuilder.builder(getResourceAsString())
                .withRDFSLabel(getPreferredRDFSLabel(config).toString());
    }

    @Override
    public String getLowercaseIdentifier(OntGenerationConfig config, boolean plural) {
        try {
            IdentifierBuilder builder = IdentifierBuilder.builder(getResourceAsString());
            if (plural) {
                return builder.lowercasePluralIdentifier();
            } else {
                return builder.lowercaseIdentifier();
            }

        } catch (IdentifierBuilder.NameBuildingException | URISyntaxException e) {
            return "unnamedProperty" + hashCode();
        }
    }

    @Override
    public String getCapitalizedIdentifier(OntGenerationConfig config, boolean plural) {
        try {
            IdentifierBuilder builder = IdentifierBuilder.builder(getResourceAsString());
            if (plural) {
                return builder.capitalizedPluralIdentifier();
            } else {
                return builder.capitalizedIdentifier();
            }

        } catch (IdentifierBuilder.NameBuildingException | URISyntaxException e) {
            return "UnnamedProperty" + hashCode();
        }
    }

    @Override
    public FieldSpec buildAnnotatedField(OntGenerationConfig config) {
        // Get the type and the name of the field:
        ClassName set = ClassName.get(Set.class);
        TypeName rangeType = ParameterizedTypeName.get(set, getRangeJavaPoetClassName(config));
        String name = getLowercaseIdentifier(config, true);

        // IRI annotation of the field:
        AnnotationSpec iriAnnotation = AnnotationSpec.builder(Iri.class)
                .addMember("value", "$S", getResourceAsString())
                .build();

        // Build the field specification:
        return FieldSpec.builder(rangeType, name, Modifier.PROTECTED)
                .addAnnotation(iriAnnotation)
                .build();
    }


    /**
     * Returns the rdfs:label which is preferred for identifier names
     * according to the configuration object provided.
     *
     * @param config The configuration object.
     * @return The preferred rdfs:label literal.
     */
    CharSequence getPreferredRDFSLabel(OntGenerationConfig config) {
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

    /**
     * Returns the most specific common superclass of all classes
     * defined as the domain of this property.
     *
     * @return The most specific common superclass.
     */
    protected ExtendedRDFSClazz findSingleDomainClazz() {
        Collection<ExtendedRDFSClazz> domains = new HashSet<>();
        for (RDFSClazz range : getDomains()) {
            domains.add((ExtendedRDFSClazz) range);
        }
        return LowestCommonSuperclass.getLowestCommonSuperclass(domains);
    }

    /**
     * Returns the most specific common superclass of all classes
     * defined as the range of this property.
     *
     * @return The most specific common superclass.
     */
    protected ExtendedRDFSClazz findSingleRangeClazz() {
        Collection<ExtendedRDFSClazz> ranges = new HashSet<>();
        for (RDFSClazz range : getRanges()) {
            ranges.add((ExtendedRDFSClazz) range);
        }
        return LowestCommonSuperclass.getLowestCommonSuperclass(ranges);
    }

    /**
     * Adds information to the JavaDoc when {@link IllegalArgumentException} is thrown.
     * Includes the definitions of the value space constraints imposed by the validator chain in <code>config</code>.
     *
     * @param javaDoc  The JavaDoc builder to add to.
     * @param datatype The datatype of the symbol checked.
     * @param config   The config containing the {@link com.github.anno4j.schema_parsing.validation.ValidatorChain}.
     */
    void addJavaDocExceptionInfo(CodeBlock.Builder javaDoc, ExtendedRDFSClazz datatype, OntGenerationConfig config) {
        StringBuilder valueSpaceDefinitions = new StringBuilder();
        for (Validator validator : config.getValidators()) {
            if (validator.isValueSpaceConstrained(datatype)) {
                valueSpaceDefinitions.append("\t<li>")
                        .append(validator.getValueSpaceDefinition(datatype))
                        .append("</li>\n")
                        .append(System.lineSeparator());
            }
        }
        if (valueSpaceDefinitions.length() > 0) {
            javaDoc.add("\n@throws IllegalArgumentException If the element(s) are not in the value space.\n" +
                    "The value space is defined as:<ol>\n" +
                    valueSpaceDefinitions.toString() + "</ol>");
        }
    }
}
