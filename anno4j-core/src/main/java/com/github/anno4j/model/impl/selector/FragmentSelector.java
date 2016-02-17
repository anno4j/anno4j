package com.github.anno4j.model.impl.selector;

import com.github.anno4j.model.Selector;
import com.github.anno4j.model.namespaces.DCTERMS;
import com.github.anno4j.model.namespaces.OADM;
import com.github.anno4j.model.namespaces.RDF;
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
public interface FragmentSelector extends Selector {

    /**
     * Sets new http:dublincore.orgdocumentsdcmi-terms#terms-conformsTo
     * <p/>
     * An established standard to which the described resource conforms..
     *
     * @param conformsTo New value of http:dublincore.orgdocumentsdcmi-terms#terms-conformsTo
     *                   <p/>
     *                   An established standard to which the described resource conforms..
     */
    @Iri(DCTERMS.CONFORMS_TO)
    void setConformsTo(String conformsTo);

    /**
     * Gets http:dublincore.orgdocumentsdcmi-terms#terms-conformsTo
     * <p/>
     * An established standard to which the described resource conforms..
     *
     * @return Value of http:dublincore.orgdocumentsdcmi-terms#terms-conformsTo
     * <p/>
     * An established standard to which the described resource conforms..
     */
    @Iri(DCTERMS.CONFORMS_TO)
    String getConformsTo();

    /**
     * Gets Refers to http:www.w3.orgTRrdf-schema#ch_value
     * <p/>
     * rdf:value is an instance of rdf:Property that may be used in describing structured values..
     *
     * @return Value of Refers to http:www.w3.orgTRrdf-schema#ch_value
     * <p/>
     * rdf:value is an instance of rdf:Property that may be used in describing structured values..
     */
    @Iri(RDF.VALUE)
    String getValue();

    /**
     * Sets new Refers to http:www.w3.orgTRrdf-schema#ch_value
     * <p/>
     * rdf:value is an instance of rdf:Property that may be used in describing structured values..
     *
     * @param value New value of Refers to http:www.w3.orgTRrdf-schema#ch_value
     *              <p/>
     *              rdf:value is an instance of rdf:Property that may be used in describing structured values..
     */
    @Iri(RDF.VALUE)
    void setValue(String value);

    Integer getX();
    Integer getY();
    Integer getWidth();
    Integer getHeight();
    String getSpatialFormat();
    void setSpatialFragment(Integer x, Integer y, Integer width, Integer height);

    Double getStart();
    Double getEnd();
    String getTemporalFormat();
    void setTemporalFragment( Double start, Double end);
}
