package com.github.anno4j.schema_parsing.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.OWL;
import com.github.anno4j.model.namespaces.RDF;
import com.github.anno4j.model.namespaces.RDFS;
import com.github.anno4j.model.namespaces.XSD;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.github.anno4j.schema.model.rdfs.RDFSProperty;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.model.BuildableRDFSClazz;
import com.github.anno4j.schema_parsing.model.BuildableRDFSProperty;
import com.github.anno4j.schema_parsing.naming.IdentifierBuilder;
import com.github.anno4j.schema_parsing.util.LowestCommonSuperclass;
import com.github.anno4j.schema_parsing.validation.Validator;
import com.squareup.javapoet.*;
import org.openrdf.annotations.Iri;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

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
public abstract class PropertyBuildingSupport extends PropertySchemaAnnotationSupport implements BuildableRDFSProperty {

    /**
     * Generates the signature of a method for this property.
     * The signature is in the following format:<br>
     * <code>public void set%Property-Name%(Set<%Range%>)</code>
     * Note that no annotations are added to the signature.
     * JavaDoc is added to the signature if possible.
     *
     * @param domainClazz The class for which the signature should be generated.
     * @param config Configuration of the generation process, e.g. which
     *               language to use for the JavaDoc.
     * @return Returns the JavaPoet specification of the signature.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    abstract MethodSpec buildSignature(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException;

    /**
     * {@inheritDoc}
     */
    @Override
    public ClassName getDomainJavaPoetClassName(OntGenerationConfig config) throws RepositoryException {
        BuildableRDFSClazz domainClazz = findSingleDomainClazz();
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
    public ClassName getRangeJavaPoetClassName(OntGenerationConfig config) throws RepositoryException {
        BuildableRDFSClazz rangeClazz = findSingleRangeClazz();
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
    public FieldSpec buildAnnotatedField(RDFSClazz domainClazz, OntGenerationConfig config) throws RepositoryException {
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
                .addAnnotations(buildSchemaAnnotations(domainClazz, config))
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
     * defined as the range of this property.
     *
     * @return The most specific common superclass.
     */
    protected BuildableRDFSClazz findSingleRangeClazz() throws RepositoryException {
        // Get the current property of RDF property type:
        RDFSProperty property;
        try {
            property = getObjectConnection().findObject(RDFSProperty.class, getResource());
        } catch (QueryEvaluationException e) {
            throw new RepositoryException(e);
        }

        Collection<BuildableRDFSClazz> ranges = new HashSet<>();
        for (RDFSClazz range : property.getRanges()) {
            ranges.add(asBuildableClazz(range));
        }

        // If the range is undefined, set it to everything:
        if(ranges.isEmpty()) {
            try {
                ranges.add(getObjectConnection().findObject(BuildableRDFSClazz.class, new URIImpl(OWL.THING)));
            } catch (QueryEvaluationException e) {
                throw new RepositoryException(e);
            }
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
    void addJavaDocExceptionInfo(CodeBlock.Builder javaDoc, BuildableRDFSClazz datatype, OntGenerationConfig config) {
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

    /**
     * Returns the given resource in {@link BuildableRDFSProperty} type.
     * @param property The property resource which should be converted.
     * @return The property in the {@link BuildableRDFSProperty} type or null if there is no such property
     * in the repository.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
     BuildableRDFSProperty asBuildableProperty(RDFSProperty property) throws RepositoryException {
        try {
            return getObjectConnection().findObject(BuildableRDFSProperty.class, property.getResource());
        } catch (QueryEvaluationException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * Returns the given resource in {@link BuildableRDFSClazz} type.
     * @param clazz The class resource which should be converted.
     * @return The class in the {@link BuildableRDFSClazz} type or null if there is no such class
     * in the repository.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    BuildableRDFSClazz asBuildableClazz(RDFSClazz clazz) throws RepositoryException {
        try {
            return getObjectConnection().findObject(BuildableRDFSClazz.class, clazz.getResource());
        } catch (QueryEvaluationException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * Returns whether the given resource is from one of the following vocabularies:
     * <ul>
     *     <li>{@link RDF}</li>
     *     <li>{@link RDFS}</li>
     *     <li>{@link OWL}</li>
     *     <li>{@link XSD}</li>
     * </ul>
     * @param resource The resource to check.
     * @return Returns true iff the resource is from one of the above vocabularies.
     */
    static boolean isFromSpecialVocabulary(ResourceObject resource) {
        return resource.getResourceAsString().startsWith(RDF.NS)
                || resource.getResourceAsString().startsWith(RDFS.NS)
                || resource.getResourceAsString().startsWith(OWL.NS)
                || resource.getResourceAsString().startsWith(XSD.NS);
    }
}
