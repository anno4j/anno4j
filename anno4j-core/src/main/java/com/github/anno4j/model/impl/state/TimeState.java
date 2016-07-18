package com.github.anno4j.model.impl.state;

import com.github.anno4j.model.State;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.OADM;
import org.openrdf.annotations.Iri;

import java.util.Set;

/**
 * Refers to http://www.w3.org/ns/oa#TimeState.
 * A TimeState records the time at which the resource's state is appropriate for the Annotation, typically the time
 * that the Annotation was created and/or a link to a persistent copy of the current version.
 */
@Iri(OADM.TIME_STATE)
public interface TimeState extends State {

    /**
     * Sets the value for the http://www.w3.org/ns/oa#sourceDate relationship.
     *
     *
     *
     * @param sourceDateStart   The value to set for the http://www.w3.org/ns/oa#sourceDate relationship.
     */
    @Iri(OADM.SOURCE_DATE_START)
    void setSourceDateStart(String sourceDateStart);

    /**
     * Gets the value currently set for the http://www.w3.org/ns/oa#sourceDate relationship.
     * @return
     */
    @Iri(OADM.SOURCE_DATE_START)
    String getSourceDateStart();

    @Iri(OADM.SOURCE_DATE_END)
    void setSourceDateEnd(String sourceDateEnd);

    @Iri(OADM.SOURCE_DATE_END)
    String getSourceDateEnd();

    @Iri(OADM.SOURCE_DATE)
    void setSourceDates(Set<String> sourceDates);

    @Iri(OADM.SOURCE_DATE)
    Set<String> getSourceDates();

    void addSourceDate(String sourceDate);

    @Iri(OADM.CACHED_SOURCE)
    void setCachedSources(Set<ResourceObject> cachedSources);

    @Iri(OADM.CACHED_SOURCE)
    Set<ResourceObject> getCachedSources();

    void addCachedSource(ResourceObject cachedSource);
}
