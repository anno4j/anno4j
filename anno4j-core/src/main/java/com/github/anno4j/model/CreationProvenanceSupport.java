package com.github.anno4j.model;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.impl.ResourceObjectSupport;
import com.github.anno4j.model.namespaces.DCTERMS;
import com.github.anno4j.util.TimeHelper;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.exceptions.ObjectPersistException;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

/**
 * Support class for the CreationProvenance interface.
 */
@Partial
public abstract class CreationProvenanceSupport extends ResourceObjectSupport implements CreationProvenance {

    /**
     * Refers to http://purl.org/dc/terms/modified.
     */
    @Iri(DCTERMS.MODIFIED)
    private String modified;

    /**
     * Refers to http://purl.org/dc/terms/created.
     */
    @Iri(DCTERMS.CREATED)
    private String created;

    @Override
    /**
     * {@inheritDoc}
     */
    public void setCreated(String created) {
        if (created == null || TimeHelper.testTimeString(created)) {
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
        if (modification == null || TimeHelper.testTimeString(modification)) {
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
        this.setModified(TimeHelper.createTimeString(year, month, day, hours, minutes, seconds, timezoneID));
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public void setCreated(int year, int month, int day, int hours, int minutes, int seconds, String timezoneID) {
        this.setCreated(TimeHelper.createTimeString(year, month, day, hours, minutes, seconds, timezoneID));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addRight(ResourceObject right) {
        HashSet<ResourceObject> rights = new HashSet<>();

        if(this.getRights() != null) {
            rights.addAll(this.getRights());
        }

        rights.add(right);
        this.setRights(rights);
    }

}
