package com.github.anno4j.model.impl.selector;

import com.github.anno4j.model.Selector;
import com.github.anno4j.model.namespaces.OADM;
import com.github.anno4j.model.namespaces.RDF;
import org.openrdf.annotations.Iri;

/**
 * Refers to http://www.w3.org/ns/oa#XPathSelector
 *
 * An XPathSelector is used to select elements and content within a resource that supports the Document Object Model
 * via a specified XPath value.
 */
@Iri(OADM.XPATH_SELECTOR)
public interface XPathSelector extends Selector {

    /**
     * Set the http://www.w3.org/1999/02/22-rdf-syntax-ns#value property.
     *
     * The xpath to the selected segment.
     * There must be exactly 1 value associated with an XPath Selector.
     *
     * @param value The value to set for the http://www.w3.org/1999/02/22-rdf-syntax-ns#value property.
     */
    @Iri(RDF.VALUE)
    void setValue(String value);

    /**
     * Gets the http://www.w3.org/1999/02/22-rdf-syntax-ns#value property.
     *
     * The xpath to the selected segment.
     * There must be exactly 1 value associated with an XPath Selector.
     *
     * @return  The current value of the http://www.w3.org/1999/02/22-rdf-syntax-ns#value property.
     */
    @Iri(RDF.VALUE)
    String getValue();
}
