package com.github.anno4j.model.impl.selector;

import com.github.anno4j.model.Selector;
import com.github.anno4j.model.ontologies.DCTERMS;
import com.github.anno4j.model.ontologies.OADM;
import com.github.anno4j.model.ontologies.RDF;
import org.openrdf.annotations.Iri;

/**
 * Conforms to http://www.w3.org/TR/annotation-model/#svg-selector
 *
 * [subClass of oa:Selector] The class for a Selector which defines a shape using the SVG standard.
 */
@Iri(OADM.SELECTOR_SVG)
public class SvgSelector extends Selector {

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
     * Standard constructor.
     */
    public SvgSelector() {};

    /**
     * Constructor also setting the value field. ConformsTo will be set automatically to be conform to the SVG standard.
     *
     * @param value Textual representation of the SVG vector.
     */
    public SvgSelector(String value) {
        this.value = value;
        this.conformsTo = FragmentSpecification.SVG.toString();
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
        return "SvgSelector{" +
                "resource='" + this.getResource() + "'" +
                ", value='" + value + '\'' +
                ", conformsTo='" + conformsTo + '\'' +
                "}'";
    }
}
