package com.github.anno4j.model.impl.selector;

import com.github.anno4j.model.Selector;
import com.github.anno4j.model.namespaces.OADM;
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
@Iri(OADM.DATA_POSITION_SELECTOR)
public interface DataPositionSelector extends Selector {

    /**
     * Sets new Refers to http:www.w3.orgnsoa#end
     * <p/>
     * The end position of the segment of data. The last character is not included within the segment.
     * Each DataPositionSelector must have exactly 1 oa:end property.
     *
     * Refers to http://www.w3.org/ns/oa#end
     *
     * The end position of the segment of data. The last character is not included within the segment.
     * Each DataPositionSelector must have exactly 1 oa:end property.
     *
     * @param end New value of Refers to http:www.w3.orgnsoa#end
     *            <p/>
     *            The end position of the segment of data. The last character is not included within the segment.
     *            Each DataPositionSelector must have exactly 1 oa:end property.
     */
    @Iri(OADM.END)
    void setEnd(long end);

    /**
     * Gets Refers to http:www.w3.orgnsoa#end
     * <p/>
     * The end position of the segment of data. The last character is not included within the segment.
     * Each DataPositionSelector must have exactly 1 oa:end property.
     *
     * Refers to http://www.w3.org/ns/oa#end
     *
     * The end position of the segment of data. The last character is not included within the segment.
     * Each DataPositionSelector must have exactly 1 oa:end property.
     *
     * @return Value of Refers to http:www.w3.orgnsoa#end
     * <p/>
     * The end position of the segment of data. The last character is not included within the segment.
     * Each DataPositionSelector must have exactly 1 oa:end property.
     */
    @Iri(OADM.END)
    long getEnd();

    /**
     * Sets new Refers to http:www.w3.orgnsoa#start
     * <p/>
     * The starting position of the segment of data. The first byte is character position 0.
     * Each DataPositionSelector must have exactly 1 oa:start property.
     *
     * Refers to http://www.w3.org/ns/oa#start
     *
     * The starting position of the segment of data. The first byte is character position 0.
     * Each DataPositionSelector must have exactly 1 oa:start property.
     *
     * @param start New value of Refers to http:www.w3.orgnsoa#start
     *              <p/>
     *              The starting position of the segment of data. The first byte is character position 0.
     *              Each DataPositionSelector must have exactly 1 oa:start property..
     */
    @Iri(OADM.START)
    void setStart(long start);

    /**
     * Gets Refers to http:www.w3.orgnsoa#start
     * <p/>
     * The starting position of the segment of data. The first byte is character position 0.
     * Each DataPositionSelector must have exactly 1 oa:start property.
     *
     * Refers to http://www.w3.org/ns/oa#start
     *
     * The starting position of the segment of data. The first byte is character position 0.
     * Each DataPositionSelector must have exactly 1 oa:start property.
     *
     * @return Value of Refers to http:www.w3.orgnsoa#start
     * <p/>
     * The starting position of the segment of data. The first byte is character position 0.
     * Each DataPositionSelector must have exactly 1 oa:start property.
     */
    @Iri(OADM.START)
    long getStart();
}
