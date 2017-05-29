package com.github.anno4j.model.impl.state;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.impl.ResourceObjectSupport;
import com.github.anno4j.model.namespaces.OADM;
import com.github.anno4j.util.TimeHelper;
import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.exceptions.ObjectPersistException;

import java.util.HashSet;
import java.util.Set;

/**
 * Support class for the TimeState interface.
 */
@Partial
public abstract class TimeStateSupport extends ResourceObjectSupport implements TimeState {

    @Iri(OADM.SOURCE_DATE_START)
    private String sourceDateStart;

    @Iri(OADM.SOURCE_DATE_END)
    private String sourceDateEnd;

    @Override
    /**
     * {@inheritDoc}
     */
    public void setSourceDateStart(String sourceDateStart) {
        if (sourceDateStart == null || TimeHelper.testTimeString(sourceDateStart)) {
            this.sourceDateStart = sourceDateStart;
        } else {
            throw new ObjectPersistException("Incorrect timestamp format supported. The timestamp needs to be conform to the ISO 8601 specification.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSourceDateStart() { return this.sourceDateStart; }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSourceDateEnd(String sourceDateEnd) {
        if (sourceDateEnd == null || TimeHelper.testTimeString(sourceDateEnd)) {
            this.sourceDateEnd = sourceDateEnd;
        } else {
            throw new ObjectPersistException("Incorrect timestamp format supported. The timestamp needs to be conform to the ISO 8601 specification.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSourceDateEnd() { return this.sourceDateEnd; }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSourceDate(String sourceDate) {
        HashSet<String> sourceDates = new HashSet<>();

        Set<String> current = this.getSourceDates();

        if(current != null) {
            sourceDates.addAll(current);
        }

        sourceDates.add(sourceDate);
        this.setSourceDates(sourceDates);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCachedSource(ResourceObject cachedSource) {
        HashSet<ResourceObject> cachedSources = new HashSet<>();

        Set<ResourceObject> current = this.getCachedSources();

        if(current != null) {
            cachedSources.addAll(current);
        }

        cachedSources.add(cachedSource);
        this.setCachedSources(cachedSources);
    }
}
