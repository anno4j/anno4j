package com.github.anno4j.model.impl.selector;

import com.github.anno4j.model.Selector;
import com.github.anno4j.model.namespaces.OADM;
import com.github.anno4j.model.namespaces.RDF;
import org.openrdf.annotations.Iri;

/**
 * Refers to http://www.w3.org/ns/oa#CssSelector.
 *
 * A CssSelector describes a Segment of interest in a representation that conforms to the Document Object Model
 * through the use of the CSS selector specification.
 */
@Iri(OADM.CSS_SELECTOR)
public interface CSSSelector extends Selector {

    /**
     * Set the http://www.w3.org/1999/02/22-rdf-syntax-ns#value property.
     *
     * The CSS selection path to the Segment.
     * There must be exactly 1 value associated with a CSS Selector.
     *
     * @param value The value to set for the http://www.w3.org/1999/02/22-rdf-syntax-ns#value property.
     */
    @Iri(RDF.VALUE)
    void setValue(String value);

    /**
     * Gets the http://www.w3.org/1999/02/22-rdf-syntax-ns#value property.
     *
     * The CSS selection path to the Segment.
     * There must be exactly 1 value associated with a CSS Selector.
     *
     * @return  The current value of the http://www.w3.org/1999/02/22-rdf-syntax-ns#value property.
     */
    @Iri(RDF.VALUE)
    String getValue();
}
