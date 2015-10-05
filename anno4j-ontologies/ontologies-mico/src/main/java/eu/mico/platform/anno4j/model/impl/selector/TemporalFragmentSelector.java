package eu.mico.platform.anno4j.model.impl.selector;

import com.github.anno4j.model.impl.selector.FragmentSelector;
import com.github.anno4j.model.impl.selector.enums.FragmentSpecification;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Temporal fragment selector which is conform to W3C Media Fragments specification
 */
public class TemporalFragmentSelector extends FragmentSelector {

    private Pattern pattern = Pattern.compile("t=npt:(\\d+.\\d+),(\\d+.\\d+)");

    /**
     * Default constructor
     */
    public TemporalFragmentSelector() {
        this.setConformsToFragmentSpecification(FragmentSpecification.W3C_MEDIA_FRAGMENTS);
    }

    /**
     * Constructor
     * @param start start of the temporal fragment in miliseconds
     * @param end end of the temporal fragment in miliseconds
     */
    public TemporalFragmentSelector(int start, int end) {
        this.setConformsToFragmentSpecification(FragmentSpecification.W3C_MEDIA_FRAGMENTS);
        this.setTemporalFragment(start, end);
    }

    /**
     * Sets the temporal fragments of the selector

     * @param start start of the temporal fragment in miliseconds
     * @param end end of the temporal fragment in miliseconds
     */
    public void setTemporalFragment( int start, int end) {
        this.setValue("t=npt:" + convertToTimeString(start) + "," + convertToTimeString(end));
    }

    /**
     * Converts miliseconds to a media fragment compatible string representation (e.g. 4556 ms -> 4.556)
     * @param miliseconds
     * @return timestamp as compatible media fragment string representation
     */
    private String convertToTimeString(int miliseconds) {
        return String.format(Locale.US, "%.3f", (float)miliseconds/1000f);
    }

    /**
     * Extracts the start time value from the string representation of the fragment
     *
     * @return The start time of the fragment as miliseconds
     */
    public int getStart() {
        Matcher m = pattern.matcher(this.getValue());
        if (m.matches()) {
            return (int) Float.parseFloat(m.group(5))*1000;
        }
        return -1;
    }

    /**
     * Extracts the end time value from the string representation of the fragment
     *
     * @return The end time of the fragment as miliseconds
     */
    public int getEnd() {
        Matcher m = pattern.matcher(this.getValue());
        if (m.matches()) {
            return (int) Float.parseFloat(m.group(6))*1000;
        }
        return -1;
    }

}
