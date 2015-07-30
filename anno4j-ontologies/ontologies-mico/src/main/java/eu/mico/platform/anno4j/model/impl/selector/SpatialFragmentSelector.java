package eu.mico.platform.anno4j.model.impl.selector;

import com.github.anno4j.model.impl.selector.FragmentSelector;
import com.github.anno4j.model.impl.selector.FragmentSpecification;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpatialFragmentSelector extends FragmentSelector {

    private Pattern pattern = Pattern.compile("#xywh=(\\d+),(\\d+),(\\d+),(\\d+)");


    public SpatialFragmentSelector() {
        this.setConformsToFragmentSpecification(FragmentSpecification.W3C_MEDIA_FRAGMENTS);
    }

    public SpatialFragmentSelector(int x, int y, int width, int height) {
        this.setConformsToFragmentSpecification(FragmentSpecification.W3C_MEDIA_FRAGMENTS);
        this.setSpatialFragment(x, y, width, height);
    }

    public void setSpatialFragment(int x, int y, int width, int height) {
        this.setValue("#xywh="+x+","+y+","+width+","+height);
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
}
