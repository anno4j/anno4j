package com.github.anno4j.model.impl.selector;

import com.github.anno4j.model.Selector;
import com.github.anno4j.model.ontologies.DCTERMS;
import com.github.anno4j.model.ontologies.OADM;
import com.github.anno4j.model.ontologies.RDF;
import org.openrdf.annotations.Iri;

/**
 * Conforms to http://www.w3.org/ns/oa#FragmentSelector
 *
 * A Selector which describes the segment of interest in a representation, through the use of the fragment identifier component of a URI.
 *
 * It is RECOMMENDED to use oa:FragmentSelector as the selector on a Specific Resource rather than annotating the fragment URI directly, in order to improve discoverability of annotation on the Source.
 *
 * The oa:FragmentSelector MUST have exactly 1 rdf:value property, containing the fragment identifier component of a URI that describes the segment of interest in the resource, excluding the initial "#".
 *
 * The Fragment Selector SHOULD have a dcterms:conformsTo relationship with the object being the specification that defines the syntax of the fragment, for instance <http://tools.ietf.org/rfc/rfc3236> for HTML fragments.
 */
@Iri(OADM.SELECTOR_FRAGMENT)
public class FragmentSelector extends Selector {

    /**
     * Refers to http://www.w3.org/TR/rdf-schema/#ch_value
     *
     * rdf:value is an instance of rdf:Property that may be used in describing structured values.
     */
    @Iri(RDF.VALUE)           private String value;

    /**
     * http://dublincore.org/documents/dcmi-terms/#terms-conformsTo
     *
     * An established standard to which the described resource conforms.
     */
    @Iri(DCTERMS.CONFORMS_TO) private String conformsTo;

    /**
     * Basic constructor.
     */
    public FragmentSelector() {};

    /**
     * Constructor setting the value and the conformsTo variables.
     *
     * @param value         Contains the value of the selector.
     * @param conformsTo    Contains string representation of the referenced standard.
     */
    public FragmentSelector(String value, String conformsTo) {
        this.value = value;
        this.conformsTo = conformsTo;
    }

    /**
     * Sets new http:dublincore.orgdocumentsdcmi-terms#terms-conformsTo.
     * <p/>
     * An established standard to which the described resource conforms.
     * @param fragmentSpecification New value of http:dublincore.orgdocumentsdcmi-terms#terms-conformsTo
     */
    public void setConformsToFragmentSpecification(FragmentSpecification fragmentSpecification) {
        this.conformsTo = fragmentSpecification.toString();
    }

    /**
     * Sets new http:dublincore.orgdocumentsdcmi-terms#terms-conformsTo
     * <p/>
     * An established standard to which the described resource conforms..
     *
     * @param conformsTo New value of http:dublincore.orgdocumentsdcmi-terms#terms-conformsTo
     *                   <p/>
     *                   An established standard to which the described resource conforms..
     */
    public void setConformsTo(String conformsTo) {
        this.conformsTo = conformsTo;
    }

    /**
     * Gets Refers to http:www.w3.orgTRrdf-schema#ch_value
     * <p/>
     * rdf:value is an instance of rdf:Property that may be used in describing structured values..
     *
     * @return Value of Refers to http:www.w3.orgTRrdf-schema#ch_value
     * <p/>
     * rdf:value is an instance of rdf:Property that may be used in describing structured values..
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets http:dublincore.orgdocumentsdcmi-terms#terms-conformsTo
     * <p/>
     * An established standard to which the described resource conforms..
     *
     * @return Value of http:dublincore.orgdocumentsdcmi-terms#terms-conformsTo
     * <p/>
     * An established standard to which the described resource conforms..
     */
    public String getConformsTo() {
        return conformsTo;
    }

    /**
     * Sets new Refers to http:www.w3.orgTRrdf-schema#ch_value
     * <p/>
     * rdf:value is an instance of rdf:Property that may be used in describing structured values..
     *
     * @param value New value of Refers to http:www.w3.orgTRrdf-schema#ch_value
     *              <p/>
     *              rdf:value is an instance of rdf:Property that may be used in describing structured values..
     */
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "FragmentSelector{" +
                "resource='" + this.getResource() + "'" +
                ", value='" + value + '\'' +
                ", conformsTo='" + conformsTo + '\'' +
                "}'";
    }
}
