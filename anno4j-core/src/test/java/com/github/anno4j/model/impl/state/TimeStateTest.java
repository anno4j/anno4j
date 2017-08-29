package com.github.anno4j.model.impl.state;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.exceptions.ObjectPersistException;

import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * Test suite for the TimeState interface.
 */
public class TimeStateTest {

    private Anno4j anno4j;

    private final static String GOOD_DATE = "2015-01-28T12:00:00Z";
    private final static String BAD_DATE = "2015-01-28T12:00:00Z-----2";

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
    }

    @Test
    public void testSourceDateStartWithGoodDate() throws RepositoryException, IllegalAccessException, InstantiationException {
        TimeState state = this.anno4j.createObject(TimeState.class);

        state.setSourceDateStart(GOOD_DATE);

        TimeState result = this.anno4j.findByID(TimeState.class, state.getResourceAsString());

        assertEquals(GOOD_DATE, result.getSourceDateStart());
    }

    @Test(expected = ObjectPersistException.class)
    public void testSourceDateWithBadDate() throws RepositoryException, IllegalAccessException, InstantiationException {
        TimeState state = this.anno4j.createObject(TimeState.class);

        state.setSourceDateStart(BAD_DATE);
    }

    @Test
    public void testSourceDateEndWithGoodDate() throws RepositoryException, IllegalAccessException, InstantiationException {
        TimeState state = this.anno4j.createObject(TimeState.class);

        state.setSourceDateEnd(GOOD_DATE);

        TimeState result = this.anno4j.findByID(TimeState.class, state.getResourceAsString());

        assertEquals(GOOD_DATE, result.getSourceDateEnd());
    }

    @Test(expected = ObjectPersistException.class)
    public void testSourceDateEndWithBadDate() throws RepositoryException, IllegalAccessException, InstantiationException {
        TimeState state = this.anno4j.createObject(TimeState.class);

        state.setSourceDateEnd(BAD_DATE);
    }

    @Test
    public void testCachedSources() throws RepositoryException, IllegalAccessException, InstantiationException {
        TimeState state = this.anno4j.createObject(TimeState.class);

        TimeState result = this.anno4j.findByID(TimeState.class, state.getResourceAsString());

        assertEquals(0, result.getCachedSources().size());

        state.addCachedSource(this.anno4j.createObject(ResourceObject.class));

        result = this.anno4j.findByID(TimeState.class, state.getResourceAsString());

        assertEquals(1, result.getCachedSources().size());

        HashSet<ResourceObject> cachedSources = new HashSet<>();
        cachedSources.add(this.anno4j.createObject(ResourceObject.class));
        cachedSources.add(this.anno4j.createObject(ResourceObject.class));

        state.setCachedSources(cachedSources);

        result = this.anno4j.findByID(TimeState.class, state.getResourceAsString());

        assertEquals(2, result.getCachedSources().size());
    }

    @Test
    public void testSourceDates() throws RepositoryException, IllegalAccessException, InstantiationException {
        TimeState state = this.anno4j.createObject(TimeState.class);

        TimeState result = this.anno4j.findByID(TimeState.class, state.getResourceAsString());

        assertEquals(0, result.getSourceDates().size());

        state.addSourceDate("2015-01-28T12:00:00Z");

        result = this.anno4j.findByID(TimeState.class, state.getResourceAsString());

        assertEquals(1, result.getSourceDates().size());

        HashSet<String> sourceDates = new HashSet<>();
        sourceDates.add("2015-01-28T12:00:00Z");
        sourceDates.add("2016-01-28T12:00:00Z");

        state.setSourceDates(sourceDates);

        result = this.anno4j.findByID(TimeState.class, state.getResourceAsString());

        assertEquals(2, result.getSourceDates().size());
    }
}