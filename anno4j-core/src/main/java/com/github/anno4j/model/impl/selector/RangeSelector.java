package com.github.anno4j.model.impl.selector;

import com.github.anno4j.model.Selector;
import com.github.anno4j.model.namespaces.OADM;
import org.openrdf.annotations.Iri;

/**
 * Refers to http://www.w3.org/ns/oa#RangeSelector.
 *
 * A Range Selector can be used to identify the beginning and the end of the selection by using other Selectors.
 * The selection consists of everything from the beginning of the starting selector through to the beginning of the
 * ending selector, but not including it.
 */
@Iri(OADM.RANGE_SELECTOR)
public interface RangeSelector extends Selector {

    /**
     * Sets the http://www.w3.org/ns/oa#hasStartSelector relationship.
     *
     * The relationship between a RangeSelector and the Selector that describes the start position of the range.
     *
     * @param startSelector The Selector to set as start selector.
     */
    @Iri(OADM.HAS_START_SELECTOR)
    void setStartSelector(Selector startSelector);

    /**
     * Gets the http://www.w3.org/ns/oa#hasStartSelector relationship.
     *
     * The relationship between a RangeSelector and the Selector that describes the start position of the range.
     *
     * @return  The Selector currently defined as start selector.
     */
    @Iri(OADM.HAS_START_SELECTOR)
    Selector getStartSelector();

    /**
     * Sets the http://www.w3.org/ns/oa#hasEndSelector relationship.
     *
     * The relationship between a RangeSelector and the Selector that describes the end position of the range.
     *
     * @param endSelector   The Selector to set as end selector.
     */
    @Iri(OADM.HAS_END_SELECTOR)
    void setEndSelector(Selector endSelector);

    /**
     * Gets the http://www.w3.org/ns/oa#hasEndSelector relationship.
     *
     * The relationship between a RangeSelector and the Selector that describes the end position of the range.
     *
     * @return  The Selector currently defined as end selector.
     */
    @Iri(OADM.HAS_END_SELECTOR)
    Selector getEndSelector();
}
