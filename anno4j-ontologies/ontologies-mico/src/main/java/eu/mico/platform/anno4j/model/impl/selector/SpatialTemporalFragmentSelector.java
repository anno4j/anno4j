package eu.mico.platform.anno4j.model.impl.selector;

import com.github.anno4j.model.impl.selector.FragmentSelector;
import com.github.anno4j.model.impl.selector.FragmentSpecification;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Spatial and temporal fragment selector which is conform to W3C Media Fragments specification
 */
public class SpatialTemporalFragmentSelector extends FragmentSelector {

    private Pattern pattern = Pattern.compile("#xywh=(\\d+),(\\d+),(\\d+),(\\d+)&t=npt:(\\d+.\\d+),(\\d+.\\d+)");

    /**
     * Default constructor
     */
    public SpatialTemporalFragmentSelector() {
        this.setConformsToFragmentSpecification(FragmentSpecification.W3C_MEDIA_FRAGMENTS);
    }

    /**
     * Constructor
     * @param x x-coordinate of the fragment as absolute pixel
     * @param y y-coordinate of the fragment as absolute pixel
     * @param width width of the fragment box
     * @param height height of the fragment box
     * @param start start of the temporal fragment in miliseconds
     * @param end end of the temporal fragment in miliseconds
     */
    public SpatialTemporalFragmentSelector(int x, int y, int width, int height, int start, int end) {
        this.setConformsToFragmentSpecification(FragmentSpecification.W3C_MEDIA_FRAGMENTS);
        this.setSpatialTemporalFragment(x, y, width, height, start, end);
    }

    /**
     * Sets the temporal and spatial fragments of the selector
     * @param x x-coordinate of the fragment as absolute pixel
     * @param y y-coordinate of the fragment as absolute pixel
     * @param width width of the fragment box
     * @param height height of the fragment box
     * @param start start of the temporal fragment in miliseconds
     * @param end end of the temporal fragment in miliseconds
     */
    public void setSpatialTemporalFragment(int x, int y, int width, int height, int start, int end) {
        this.setValue("#xywh="+x+","+y+","+width+","+height+"&t=npt:"+convertToTimeString(start)+","+convertToTimeString(end));
    }

    /**
     * Extracts the x-coordinate from the string representation of the fragment
     *
     * @return The x coordinate of the fragment
     */
    public int getX() {
        Matcher m = pattern.matcher(this.getValue());
        if (m.matches()) {
            return Integer.parseInt(m.group(1));
        }
        return -1;
    }

    /**
     * Extracts the y-coordinate from the string representation of the fragment
     *
     * @return The y coordinate of the fragment
     */
    public int getY() {
        Matcher m = pattern.matcher(this.getValue());
        if (m.matches()) {
            return Integer.parseInt(m.group(2));
        }
        return -1;
    }

    /**
     * Extracts the width from the string representation of the fragment
     *
     * @return The width of the fragment
     */
    public int getWidth() {
        Matcher m = pattern.matcher(this.getValue());
        if (m.matches()) {
            return Integer.parseInt(m.group(3));
        }
        return -1;
    }

    /**
     * Extracts the height value from the string representation of the fragment
     *
     * @return The height of the fragment
     */
    public int getHeight() {
        Matcher m = pattern.matcher(this.getValue());
        if (m.matches()) {
            return Integer.parseInt(m.group(4));
        }
        return -1;
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

    /**
     * Converts miliseconds to a media fragment compatible string representation (e.g. 4556 ms -> 4.556)
     * @param miliseconds
     * @return timestamp as compatible media fragment string representation
     */
    private String convertToTimeString(int miliseconds) {
        return String.format(Locale.US, "%.3f", (float)miliseconds/1000f);
    }
}
