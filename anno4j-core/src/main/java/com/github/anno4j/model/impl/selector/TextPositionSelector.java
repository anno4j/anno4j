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
public interface TextPositionSelector extends Selector {
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
    @Iri(OADM.START)
    long getStart();

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
    @Iri(OADM.END)
    void setEnd(long end);

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
    @Iri(OADM.START)
    void setStart(long start);

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
    @Iri(OADM.END)
    long getEnd();
}
