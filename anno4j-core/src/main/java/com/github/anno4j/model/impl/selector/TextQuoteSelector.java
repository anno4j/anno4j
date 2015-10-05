package com.github.anno4j.model.impl.selector;

import com.github.anno4j.model.Selector;
import com.github.anno4j.model.namespaces.OADM;
import org.openrdf.annotations.Iri;

/**
 * Conforms to http://www.w3.org/ns/oa#TextQuoteSelector
 *
 * A Selector that describes a textual segment by means of quoting it, plus passages before or after it.
 *
 * For example, if the document were "abcdefghijklmnopqrstuvwxyz", one could select "efg" by a oa:prefix of "abcd", the quotation of oa:exact "efg" and a oa:suffix of "hijk".
 *
 * The text MUST be normalized before recording.
 *
 * Each TextQuoteSelector MUST have exactly 1 oa:exact property.
 *
 * Each TextQuoteSelector SHOULD have exactly 1 oa:prefix property, and MUST NOT have more than 1.
 *
 * Each TextQuoteSelector SHOULD have exactly 1 oa:suffix property, and MUST NOT have more than 1.
 */
@Iri(OADM.SELECTOR_TEXT_QUOTE)
public interface TextQuoteSelector extends Selector {
    /**
     * Sets new Refers to http:www.w3.orgnsoa#prefix
     * A snippet of text that occurs immediately before the text which is being selected.
     * Each TextQuoteSelector should have exactly 1 oa:prefix property, and must not have more than 1..
     *
     * @param prefix New value of Refers to http:www.w3.orgnsoa#prefix
     *               A snippet of text that occurs immediately before the text which is being selected.
     *               Each TextQuoteSelector should have exactly 1 oa:prefix property, and must not have more than 1..
     */
    @Iri(OADM.TEXT_PREFIX)
    void setPrefix(String prefix);

    /**
     * Sets new Refers to http:www.w3.orgnsoa#exact
     * A copy of the text which is being selected, after normalization.
     * Each TextQuoteSelector must have exactly 1 oa:exact property..
     *
     * @param exact New value of Refers to http:www.w3.orgnsoa#exact
     *              A copy of the text which is being selected, after normalization.
     *              Each TextQuoteSelector must have exactly 1 oa:exact property..
     */
    @Iri(OADM.EXACT)
    void setExact(String exact);

    /**
     * Gets Refers to http:www.w3.orgnsoa#exact
     * A copy of the text which is being selected, after normalization.
     * Each TextQuoteSelector must have exactly 1 oa:exact property..
     *
     * @return Value of Refers to http:www.w3.orgnsoa#exact
     * A copy of the text which is being selected, after normalization.
     * Each TextQuoteSelector must have exactly 1 oa:exact property..
     */
    @Iri(OADM.EXACT)
    public String getExact();

    /**
     * Sets new Refers to http:www.w3.orgnsoa#suffix
     * The snippet of text that occurs immediately after the text which is being selected.
     * Each TextQuoteSelector should have exactly 1 oa:suffix property, and must not have more than 1..
     *
     * @param suffix New value of Refers to http:www.w3.orgnsoa#suffix
     *               The snippet of text that occurs immediately after the text which is being selected.
     *               Each TextQuoteSelector should have exactly 1 oa:suffix property, and must not have more than 1..
     */
    @Iri(OADM.SUFFIX)
    public void setSuffix(String suffix);

    /**
     * Gets Refers to http:www.w3.orgnsoa#suffix
     * The snippet of text that occurs immediately after the text which is being selected.
     * Each TextQuoteSelector should have exactly 1 oa:suffix property, and must not have more than 1..
     *
     * @return Value of Refers to http:www.w3.orgnsoa#suffix
     * The snippet of text that occurs immediately after the text which is being selected.
     * Each TextQuoteSelector should have exactly 1 oa:suffix property, and must not have more than 1..
     */
    @Iri(OADM.SUFFIX)
    public String getSuffix();

    /**
     * Gets Refers to http:www.w3.orgnsoa#prefix
     * A snippet of text that occurs immediately before the text which is being selected.
     * Each TextQuoteSelector should have exactly 1 oa:prefix property, and must not have more than 1..
     *
     * @return Value of Refers to http:www.w3.orgnsoa#prefix
     * A snippet of text that occurs immediately before the text which is being selected.
     * Each TextQuoteSelector should have exactly 1 oa:prefix property, and must not have more than 1..
     */
    @Iri(OADM.TEXT_PREFIX)
    public String getPrefix();
}
