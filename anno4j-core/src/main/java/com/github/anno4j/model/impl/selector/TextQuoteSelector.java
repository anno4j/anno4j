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
public class TextQuoteSelector extends Selector {

    /**
     * Refers to http://www.w3.org/ns/oa#exact
     * A copy of the text which is being selected, after normalization.
     * Each TextQuoteSelector must have exactly 1 oa:exact property.
     */
    @Iri(OADM.EXACT)  private String exact;

    /**
     * Refers to http://www.w3.org/ns/oa#prefix
     * A snippet of text that occurs immediately before the text which is being selected.
     * Each TextQuoteSelector should have exactly 1 oa:prefix property, and must not have more than 1.
     */
    @Iri(OADM.PREFIX) private String prefix;

    /**
     * Refers to http://www.w3.org/ns/oa#suffix
     * The snippet of text that occurs immediately after the text which is being selected.
     * Each TextQuoteSelector should have exactly 1 oa:suffix property, and must not have more than 1.
     */
    @Iri(OADM.SUFFIX) private String suffix;

    /**
     * Standard constructor.
     */
    public TextQuoteSelector() {};

    /**
     * Constructor also setting the exact, prefix, and suffix variables.
     * @param exact     Specifies a copy of the text that is to be selected.
     * @param prefix    Snippet of the text that occurs immediately before the text that is to be selected.
     * @param suffix    Snippet of the text that occurs immediately after the text that is to be selected.
     */
    public TextQuoteSelector(String exact, String prefix, String suffix) {
        this.exact = exact;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    /**
     * Sets new Refers to http:www.w3.orgnsoa#prefix
     * A snippet of text that occurs immediately before the text which is being selected.
     * Each TextQuoteSelector should have exactly 1 oa:prefix property, and must not have more than 1..
     *
     * @param prefix New value of Refers to http:www.w3.orgnsoa#prefix
     *               A snippet of text that occurs immediately before the text which is being selected.
     *               Each TextQuoteSelector should have exactly 1 oa:prefix property, and must not have more than 1..
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Sets new Refers to http:www.w3.orgnsoa#exact
     * A copy of the text which is being selected, after normalization.
     * Each TextQuoteSelector must have exactly 1 oa:exact property..
     *
     * @param exact New value of Refers to http:www.w3.orgnsoa#exact
     *              A copy of the text which is being selected, after normalization.
     *              Each TextQuoteSelector must have exactly 1 oa:exact property..
     */
    public void setExact(String exact) {
        this.exact = exact;
    }

    /**
     * Gets Refers to http:www.w3.orgnsoa#exact
     * A copy of the text which is being selected, after normalization.
     * Each TextQuoteSelector must have exactly 1 oa:exact property..
     *
     * @return Value of Refers to http:www.w3.orgnsoa#exact
     * A copy of the text which is being selected, after normalization.
     * Each TextQuoteSelector must have exactly 1 oa:exact property..
     */
    public String getExact() {
        return exact;
    }

    /**
     * Sets new Refers to http:www.w3.orgnsoa#suffix
     * The snippet of text that occurs immediately after the text which is being selected.
     * Each TextQuoteSelector should have exactly 1 oa:suffix property, and must not have more than 1..
     *
     * @param suffix New value of Refers to http:www.w3.orgnsoa#suffix
     *               The snippet of text that occurs immediately after the text which is being selected.
     *               Each TextQuoteSelector should have exactly 1 oa:suffix property, and must not have more than 1..
     */
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    /**
     * Gets Refers to http:www.w3.orgnsoa#suffix
     * The snippet of text that occurs immediately after the text which is being selected.
     * Each TextQuoteSelector should have exactly 1 oa:suffix property, and must not have more than 1..
     *
     * @return Value of Refers to http:www.w3.orgnsoa#suffix
     * The snippet of text that occurs immediately after the text which is being selected.
     * Each TextQuoteSelector should have exactly 1 oa:suffix property, and must not have more than 1..
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * Gets Refers to http:www.w3.orgnsoa#prefix
     * A snippet of text that occurs immediately before the text which is being selected.
     * Each TextQuoteSelector should have exactly 1 oa:prefix property, and must not have more than 1..
     *
     * @return Value of Refers to http:www.w3.orgnsoa#prefix
     * A snippet of text that occurs immediately before the text which is being selected.
     * Each TextQuoteSelector should have exactly 1 oa:prefix property, and must not have more than 1..
     */
    public String getPrefix() {
        return prefix;
    }

    @Override
    public String toString() {
        return "TextQuoteSelector{" +
                "resource='" + this.getResource() + "'" +
                ", exact='" + exact + '\'' +
                ", prefix='" + prefix + '\'' +
                ", suffix='" + suffix + '\'' +
                "}'";
    }
}
