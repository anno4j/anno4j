package com.github.anno4j.rdfs_parser.building.support;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.rdfs_parser.building.OntGenerationConfig;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSClazz;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSProperty;
import com.github.anno4j.rdfs_parser.model.RDFSClazz;
import com.github.anno4j.rdfs_parser.model.RDFSPropertySupport;
import com.github.anno4j.rdfs_parser.util.LowestCommonSuperclass;
import com.github.anno4j.rdfs_parser.validation.Validator;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;

import java.util.Collection;
import java.util.HashSet;

/**
 * Support class providing utility functionality for generating
 * methods for the property.
 */
@Partial
public abstract class PropertyBuildingSupport extends RDFSPropertySupport implements ExtendedRDFSProperty {

    /**
     * Generates the signature of a method for this property.
     * The signature is in the following format:<br>
     *     <code>public void set%Property-Name%(Set<%Range%>)</code>
     * Note that no annotations are added to the signature.
     * JavaDoc is added to the signature if possible.
     * @param config Configuration of the generation process, e.g. which
     *               language to use for the JavaDoc.
     * @return Returns the JavaPoet specification of the signature.
     */
    abstract MethodSpec buildSignature(OntGenerationConfig config);

    /**
     * Returns the rdfs:label which is preferred for identifier names
     * according to the configuration object provided.
     * @param config The configuration object.
     * @return The preferred rdfs:label literal.
     */
    CharSequence getPreferredRDFSLabel(OntGenerationConfig config) {
        // Get the label in the preferred language:
        CharSequence preferredLabel = null;
        for (CharSequence label : getLabels()) {
            if(config.isPreferredForIdentifiers(label, preferredLabel)) {
                preferredLabel = label;
            }
        }

        return preferredLabel;
    }

    /**
     * Returns the rdfs:comment which is preferred for JavaDoc
     * according to the configuration object provided.
     * @param config The configuration object.
     * @return The preferred rdfs:comment literal.
     */
    CharSequence getPreferredRDFSComment(OntGenerationConfig config) {
        CharSequence preferredComment = null;
        for (CharSequence comment : getComments()) {
            if(config.isPreferredForJavaDoc(comment, preferredComment)) {
                preferredComment = comment;
            }
        }

        return preferredComment;
    }

    /**
     * Returns the most specific common superclass of all classes
     * defined as the range of this property.
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
     * @param javaDoc The JavaDoc builder to add to.
     * @param datatype The datatype of the symbol checked.
     * @param config The config containing the {@link com.github.anno4j.rdfs_parser.validation.ValidatorChain}.
     */
    void addJavaDocExceptionInfo(CodeBlock.Builder javaDoc, ExtendedRDFSClazz datatype, OntGenerationConfig config) {
        StringBuilder valueSpaceDefinitions = new StringBuilder();
        for (Validator validator : config.getValidators()) {
            if(validator.isValueSpaceConstrained(datatype)) {
                valueSpaceDefinitions.append("<li>")
                        .append(validator.getValueSpaceDefinition(datatype))
                        .append("</li>")
                        .append(System.lineSeparator());
            }
        }
        if(valueSpaceDefinitions.length() > 0) {
            javaDoc.add("\n@throws IllegalArgumentException If <code>value</code> is not in the value space. <ol>"
                    + valueSpaceDefinitions.toString() + "</ol>");
        }
    }
}
