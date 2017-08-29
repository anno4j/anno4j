package com.github.anno4j.model.impl.selector;

import com.github.anno4j.model.SelectorSupport;
import com.github.anno4j.model.impl.selector.enums.FragmentSpecification;
import com.github.anno4j.annotations.Partial;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by schlegel on 05/10/15.
 */
@Partial
public abstract class FragmentSelectorSupport extends SelectorSupport implements FragmentSelector {

    /**
     * Group 1: TemporalFormat e.g. npt
     * Group 2: Start-Value
     * Group 4: End-Value
     */
    private Pattern temporalPattern = Pattern.compile("t=(\\w+:)(\\d+(.\\d+)?)?,(\\d+(.\\d+)?)");

    /**
     * Group 1: SpatialFormat e.g. pixel/percent
     * Group 2: x-Value
     * Group 3: y-Value
     * Group 4: Width-Value
     * Group 5: Height-Value
     */
    private Pattern spatialPattern = Pattern.compile("xywh=(\\w+:)?(\\d+),(\\d+),(\\d+),(\\d+)");

    @Override
    public String getConformsTo() {
        return FragmentSpecification.W3C_MEDIA_FRAGMENTS.toString();
    }

    @Override
    public String getSpatialFormat() {
        if(this.getValue() == null) {
            return null;
        }

        Matcher m = spatialPattern.matcher(this.getValue());
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    @Override
    public Integer getX() {
        if(this.getValue() == null) {
            return null;
        }

        Matcher m = spatialPattern.matcher(this.getValue());
        if (m.find()) {
            return (m.group(2) != null) ? Integer.parseInt(m.group(2)) : null;
        }
        return null;
    }

    @Override
    public Integer getY() {
        if(this.getValue() == null) {
            return null;
        }

        Matcher m = spatialPattern.matcher(this.getValue());
        if (m.find()) {
            return (m.group(3) != null) ? Integer.parseInt(m.group(3)) : null;
        }
        return null;
    }

    @Override
    public Integer getWidth() {
        if(this.getValue() == null) {
            return null;
        }

        Matcher m = spatialPattern.matcher(this.getValue());
        if (m.find()) {
            return (m.group(4) != null) ? Integer.parseInt(m.group(4)) : null;
        }
        return null;
    }

    @Override
    public Integer getHeight() {
        if(this.getValue() == null) {
            return null;
        }

        Matcher m = spatialPattern.matcher(this.getValue());
        if (m.find()) {
            return (m.group(5) != null) ? Integer.parseInt(m.group(5)) : null;
        }
        return null;
    }

    @Override
    public void setSpatialFragment(Integer x, Integer y, Integer width, Integer height) {
        StringBuilder result = new StringBuilder();
        result.append("#");

        Double start = this.getStart();
        Double end = this.getEnd();
        String temporalFormat = this.getTemporalFormat();


        // add spatial information if available
        if(x != null && y != null && width != null && height != null) {
            result.append("xywh=");

            result.append(x).append(",").append(y).append(",").append(width).append(",").append(height);
        }

        // add temporal information if available
        if (end != null) {
            if(x != null && y != null && width != null && height != null) {
                result.append("&");
            }

            result.append("t=npt:");

            if(start != null) {
                result.append(start);
            }

            result.append(",").append(end);
        }

        this.setValue(result.toString());
    }

    @Override
    public String getTemporalFormat() {
        if(this.getValue() == null) {
            return null;
        }

        Matcher m = temporalPattern.matcher(this.getValue());
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    @Override
    public Double getStart() {
        if(this.getValue() == null) {
            return null;
        }

        Matcher m = temporalPattern.matcher(this.getValue());
        if (m.find()) {
            return (m.group(2) != null) ? Double.parseDouble(m.group(2)) : null;
        }
        return null;
    }

    @Override
    public Double getEnd() {
        if(this.getValue() == null) {
            return null;
        }

        Matcher m = temporalPattern.matcher(this.getValue());
        if (m.find()) {
            return (m.group(4) != null) ? Double.parseDouble(m.group(4)) : null;
        }
        return null;
    }

    @Override
    public void setTemporalFragment(Double start, Double end) {
        StringBuilder result = new StringBuilder();
        result.append("#");

        Integer x = this.getX();
        Integer y = this.getY();
        Integer w = this.getWidth();
        Integer h = this.getHeight();
        String spatialFormat = this.getSpatialFormat();

        // add spatial information if available
        if(x != null && y != null && w != null && h != null) {
            result.append("xywh=");

            if(spatialFormat != null) {
                result.append(spatialFormat);
            }

            result.append(x).append(",").append(y).append(",").append(w).append(",").append(h);

        }

        // add temporal information if available
        if (end != null) {

            if(x != null && y != null && w != null && h != null) {
                result.append("&");
            }

            result.append("t=npt:");

            if(start != null) {
                result.append(start);
            }

            result.append(",").append(end);
        }

        this.setValue(result.toString());
    }
}
