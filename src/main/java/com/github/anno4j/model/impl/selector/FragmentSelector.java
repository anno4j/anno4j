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

    @Iri(RDF.VALUE)           private String value;
    @Iri(DCTERMS.CONFORMS_TO) private String conformsTo;

    public FragmentSelector() {};

    public FragmentSelector(String value, String conformsTo) {
        this.value = value;
        this.conformsTo = conformsTo;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getConformsTo() {
        return conformsTo;
    }

    public void setConformsTo(String conformsTo) {
        this.conformsTo = conformsTo;
    }

    public void setConformsTo(FragmentSpecification fragmentSpecification) {
        this.conformsTo = fragmentSpecification.toString();
    }
}
