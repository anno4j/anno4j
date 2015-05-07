package com.github.anno4j.model.impl.selector;

import com.github.anno4j.model.Selector;
import com.github.anno4j.model.ontologies.OADM;
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

    @Iri(OADM.EXACT)  private String exact;
    @Iri(OADM.PREFIX) private String prefix;
    @Iri(OADM.SUFFIX) private String suffix;

    public String getExact() {
        return exact;
    }

    public void setExact(String exact) {
        this.exact = exact;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
