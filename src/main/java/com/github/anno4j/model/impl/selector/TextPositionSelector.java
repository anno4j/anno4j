package com.github.anno4j.model.impl.selector;

import com.github.anno4j.model.Selector;
import com.github.anno4j.model.ontologies.OADM;
import org.openrdf.annotations.Iri;

/**
 * Conforms to http://www.w3.org/ns/oa#TextPositionSelector
 *
 * An oa:Selector which describes a range of text based on its start and end positions.
 *
 * The text MUST be normalized before counting characters. For a Selector that works from the bitstream rather than the rendered characters, see oa:DataPositionSelector.
 *
 * Each oa:TextPositionSelector MUST have exactly 1 oa:start property.
 *
 * Each oa:TextPositionSelector MUST have exactly 1 oa:end property.
 */
@Iri(OADM.SELECTOR_TEXT_POSITION)
public class TextPositionSelector extends Selector {

    @Iri(OADM.START) private long start;
    @Iri(OADM.END)   private long end;

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }
}
