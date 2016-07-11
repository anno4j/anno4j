package com.github.anno4j.model;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObjectSupport;
import com.github.anno4j.model.namespaces.DCTERMS;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.exceptions.ObjectPersistException;

import java.util.concurrent.TimeUnit;

/**
 * Support class for the CreationProvenance interface.
 */
@Partial
public abstract class CreationProvenanceSupport extends ResourceObjectSupport implements CreationProvenance {

    @Iri(DCTERMS.MODIFIED)
    String modified;

    @Iri(DCTERMS.CREATED)
    String created;

    @Override
    /**
     * {@inheritDoc}
     */
    public void setCreated(String created) {
        if (created == null || this.testTimeString(created)) {
            this.created = created;
        } else {
            throw new ObjectPersistException("Incorrect timestamp format supported. The timestamp needs to be conform to the ISO 8601 specification.");
        }
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public String getCreated() {
        return this.created;
    }

    ;

    @Override
    /**
     * {@inheritDoc}
     */
    public void setModified(String modification) {
        if (modification == null || this.testTimeString(modification)) {
            this.modified = modification;
        } else {
            throw new ObjectPersistException("Incorrect timestamp format supported. The timestamp needs to be conform to the ISO 8601 specification.");
        }
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public String getModified() {
        return this.modified;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public void setModified(int year, int month, int day, int hours, int minutes, int seconds, String timezoneID) {
        this.setModified(createTimeString(year, month, day, hours, minutes, seconds, timezoneID));
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public void setCreated(int year, int month, int day, int hours, int minutes, int seconds, String timezoneID) {
        this.setCreated(createTimeString(year, month, day, hours, minutes, seconds, timezoneID));
    }

    /**
     * Creates a timestamp conform to the ISO 8601 specification, supported its single information bits.
     *
     * @param year       The year for the timestamp.
     * @param month      The month for the timestamp.
     * @param day        The day for the timestamp.
     * @param hours      The hours for the timestamp.
     * @param minutes    The minutes for the timestamp.
     * @param seconds    The seconds for the timestamp.
     * @param timezoneID The timezone for the timestamp.
     * @return A textual representation of the timestamp defined by the supported parameters.
     */
    String createTimeString(int year, int month, int day, int hours, int minutes, int seconds, String timezoneID) {
        StringBuilder builder = new StringBuilder();
        builder.append(Integer.toString(year)).append("-").
                append(Integer.toString(month)).append("-").
                append(Integer.toString(day)).append("T");

        if (hours < 10) {
            builder.append(0);
        }
        builder.append(Integer.toString(hours));

        builder.append(":");

        if (minutes < 10) {
            builder.append(0);
        }
        builder.append(Integer.toString(minutes));

        builder.append(":");

        if (seconds < 10) {
            builder.append(0);
        }
        builder.append(Integer.toString(seconds));

        // Timezone
        builder.append(createTimezoneString(timezoneID));

        return builder.toString();
    }

    /**
     * Tests if the supported String is a correctly formatted time String, following ISO 8601.
     *
     * @param time The textual representation of time to test.
     * @throws IllegalArgumentException If the format is not aligned with the ISO 8601 specification.
     */
    boolean testTimeString(String time) throws IllegalArgumentException {
        DateTimeFormatter format = ISODateTimeFormat.dateTimeNoMillis().withZoneUTC();

        try {
            format.parseDateTime(time);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Taking a timezone ID, this method returns the offset in hours.
     *
     * @param timezoneID The ID of the timezone to support the hours offset for.
     * @return A textual representation of the hours offset for the supported timezone.
     */
    String createTimezoneString(String timezoneID) {
        StringBuilder builder = new StringBuilder();

        DateTimeZone timeZone = DateTimeZone.forID(timezoneID);
        long offsetInMilliseconds = timeZone.toTimeZone().getRawOffset();
        long offsetHours = TimeUnit.MILLISECONDS.toHours(offsetInMilliseconds);

        if (offsetHours == 0) {
            builder.append("Z");
        } else {
            if (offsetHours < 0) {
                // Negative
                builder.append("-");

                if (offsetHours < -9) {
                    builder.append(Math.abs(offsetHours));
                } else {
                    builder.append(0).append(Math.abs(offsetHours));
                }
            } else {
                // Positive
                if (offsetHours > 9) {
                    builder.append(offsetHours);
                } else {
                    builder.append(0).append(offsetHours);
                }
            }
            builder.append(":00");
        }

        return builder.toString();
    }
}
