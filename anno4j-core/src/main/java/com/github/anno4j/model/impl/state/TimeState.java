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
     * Sets the value for the http://www.w3.org/ns/oa#sourceDateStart relationship.
     *
     * The start timestamp of the interval over which the Source resource should be interpreted as being applicable
     * to the Annotation.
     *
     * @param sourceDateStart   The value to set for the http://www.w3.org/ns/oa#sourceDateStart relationship.
     */
    void setSourceDateStart(String sourceDateStart);

    /**
     * Gets the value currently set for the http://www.w3.org/ns/oa#sourceDateStart relationship.
     *
     * The start timestamp of the interval over which the Source resource should be interpreted as being applicable
     * to the Annotation.
     *
     * @return  The value currently set for the http://www.w3.org/ns/oa#sourceDateStart relationship.
     */
    String getSourceDateStart();

    /**
     * Sets the value for the http://www.w3.org/ns/oa#sourceDateEnd relationship.
     *
     * The end timestamp of the interval over which the Source resource should be interpreted as being applicable
     * to the Annotation.
     *
     * @param sourceDateEnd The value to set for the http://www.w3.org/ns/oa#sourceDateEnd relationship.
     */
    void setSourceDateEnd(String sourceDateEnd);

    /**
     * Gets the value currently set for the http://www.w3.org/ns/oa#sourceDateEnd relationship.
     *
     * The end timestamp of the interval over which the Source resource should be interpreted as being applicable
     * to the Annotation.
     *
     * @return  The value currently defined for the http://www.w3.org/ns/oa#sourceDateEnd relationship.
     */
    String getSourceDateEnd();

    /**
     * Sets the values for the http://www.w3.org/ns/oa#sourceDate relationship.
     *
     * The timestamp at which the Source resource should be interpreted as being applicable to the Annotation.
     *
     * @param sourceDates   The Set of values to set for the http://www.w3.org/ns/oa#sourceDate relationship.
     */
    @Iri(OADM.SOURCE_DATE)
    void setSourceDates(Set<String> sourceDates);

    /**
     * Gets the values currently defined for the http://www.w3.org/ns/oa#sourceDate relationship.
     *
     * The timestamp at which the Source resource should be interpreted as being applicable to the Annotation.
     *
     * @return  The Set of values currently defined for the http://www.w3.org/ns/oa#sourceDate relationship.
     */
    @Iri(OADM.SOURCE_DATE)
    Set<String> getSourceDates();

    /**
     * Adds a single value to the Set currently defined for the http://www.w3.org/ns/oa#sourceDate relationship.
     *
     * @param sourceDate    The value to add to the Set of currently defined http://www.w3.org/ns/oa#sourceDate
     *                      relationships.
     */
    void addSourceDate(String sourceDate);

    /**
     * Sets the values for the http://www.w3.org/ns/oa#cachedSource relationship.
     *
     * A object of the relationship is a copy of the Source resource's representation, appropriate for the Annotation.
     *
     * @param cachedSources The value to set for the http://www.w3.org/ns/oa#cachedSource relationship.
     */
    @Iri(OADM.CACHED_SOURCE)
    void setCachedSources(Set<ResourceObject> cachedSources);

    /**
     * Gets the values currently defined for the http://www.w3.org/ns/oa#cachedSource relationship.
     *
     * A object of the relationship is a copy of the Source resource's representation, appropriate for the Annotation.
     *
     * @return  The set of values currently defined for the http://www.w3.org/ns/oa#cachedSource relationship.
     */
    @Iri(OADM.CACHED_SOURCE)
    Set<ResourceObject> getCachedSources();

    /**
     * Adds a single value to the Set currently defined for the http://www.w3.org/ns/oa#cachedSource relationship.
     *
     * @param cachedSource  The value to add to the http://www.w3.org/ns/oa#cachedSource relationship.
     */
    void addCachedSource(ResourceObject cachedSource);
}
