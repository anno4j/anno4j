package com.github.anno4j.model.impl.selector;

import com.github.anno4j.model.Selector;
import com.github.anno4j.model.ontologies.OADM;
import org.openrdf.annotations.Iri;

/**
 * Conforms to http://www.w3.org/ns/oa#DataPositionSelector
 *
 * A Selector which describes a range of data based on its start and end positions within the byte stream of the representation.
 *
 * Each DataPositionSelector MUST have exactly 1 oa:start property.
 *
 * Each TextPositionSelector MUST have exactly 1 oa:end property.
 *
 * See oa:TextPositionSelector for selection at normalized character level rather than bytestream level.
 */
@Iri(OADM.SELECTOR_DATA_POSITION)
public class DataPositionSelector extends Selector {

    @Iri(OADM.START) private long start;
    @Iri(OADM.END)   private long end;

    public DataPositionSelector() {};

    public DataPositionSelector(long start, long end) {
        this.start = start;
        this.end = end;
    }

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
