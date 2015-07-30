package com.github.anno4j.model.impl.selector;

import com.github.anno4j.model.Selector;
import com.github.anno4j.model.namespaces.OADM;
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

    /**
     * Refers to http://www.w3.org/ns/oa#start
     *
     * The starting position of the segment of text. The first character in the full text is character position 0, and the character is included within the segment.
     * Each TextPositionSelector must have exactly 1 oa:start property.
     */
    @Iri(OADM.START) private long start;

    /**
     * Refers to http://www.w3.org/ns/oa#end
     *
     * The end position of the segment of text. The last character is not included within the segment.
     * Each TextPositionSelector must have exactly 1 oa:end property.
     */
    @Iri(OADM.END)   private long end;

    /**
     * Standard constructor.
     */
    public TextPositionSelector() {};

    /**
     * Constructor setting the start and end variables.
     *
     * @param start     Specifies the startposition in the text.
     * @param end       Specifies the endposition in the text.
     */
    public TextPositionSelector(long start, long end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Gets Refers to http:www.w3.orgnsoa#start
     * <p/>
     * The starting position of the segment of text. The first character in the full text is character position 0, and the character is included within the segment.
     * Each TextPositionSelector must have exactly 1 oa:start property..
     *
     * @return Value of Refers to http:www.w3.orgnsoa#start
     * <p/>
     * The starting position of the segment of text. The first character in the full text is character position 0, and the character is included within the segment.
     * Each TextPositionSelector must have exactly 1 oa:start property..
     */
    public long getStart() {
        return start;
    }

    /**
     * Sets new Refers to http:www.w3.orgnsoa#end
     * <p/>
     * The end position of the segment of text. The last character is not included within the segment.
     * Each TextPositionSelector must have exactly 1 oa:end property..
     *
     * @param end New value of Refers to http:www.w3.orgnsoa#end
     *            <p/>
     *            The end position of the segment of text. The last character is not included within the segment.
     *            Each TextPositionSelector must have exactly 1 oa:end property..
     */
    public void setEnd(long end) {
        this.end = end;
    }

    /**
     * Sets new Refers to http:www.w3.orgnsoa#start
     * <p/>
     * The starting position of the segment of text. The first character in the full text is character position 0, and the character is included within the segment.
     * Each TextPositionSelector must have exactly 1 oa:start property..
     *
     * @param start New value of Refers to http:www.w3.orgnsoa#start
     *              <p/>
     *              The starting position of the segment of text. The first character in the full text is character position 0, and the character is included within the segment.
     *              Each TextPositionSelector must have exactly 1 oa:start property..
     */
    public void setStart(long start) {
        this.start = start;
    }

    /**
     * Gets Refers to http:www.w3.orgnsoa#end
     * <p/>
     * The end position of the segment of text. The last character is not included within the segment.
     * Each TextPositionSelector must have exactly 1 oa:end property..
     *
     * @return Value of Refers to http:www.w3.orgnsoa#end
     * <p/>
     * The end position of the segment of text. The last character is not included within the segment.
     * Each TextPositionSelector must have exactly 1 oa:end property..
     */
    public long getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return "TextPositionSelector{" +
                "resource='" + this.getResource() + "'" +
                ", start=" + start +
                ", end=" + end +
                "}'";
    }
}
