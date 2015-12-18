package eu.mico.platform.anno4j.model;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObjectSupport;

import java.util.HashSet;

/**
 * This class implements the Item interface and adds additional functionality.
 */
@Partial
public abstract class ItemSupport extends ResourceObjectSupport implements Item {

    @Override
    /**
     * {@inheritDoc}
     */
    public void addPart(Part part) {
        if(this.getParts() == null) {
            this.setParts(new HashSet<Part>());
        }

        this.getParts().add(part);
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public void setSerializedAt(int year, int month, int day, int hours, int minutes, int seconds) {

        StringBuilder builder = new StringBuilder();
        builder.append(Integer.toString(year)).append("-").
                append(Integer.toString(month)).append("-").
                append(Integer.toString(day)).append("T");

        if(hours < 10) {
            builder.append(0);
        }
        builder.append(Integer.toString(hours));

        builder.append(":");

        if(minutes < 10) {
            builder.append(0);
        }
        builder.append(Integer.toString(minutes));

        builder.append(":");

        if(seconds < 10) {
            builder.append(0);
        }
        builder.append(Integer.toString(seconds));

        builder.append("Z");

        this.setSerializedAt(builder.toString());
    }
}
