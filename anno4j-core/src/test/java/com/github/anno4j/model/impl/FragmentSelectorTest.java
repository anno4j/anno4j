package com.github.anno4j.model.impl;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.selector.FragmentSelector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.repository.object.ObjectConnection;

import static org.junit.Assert.assertEquals;

public class FragmentSelectorTest {
    private Anno4j anno4j;
    private ObjectConnection connection;

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
        this.connection = this.anno4j.getObjectRepository().getConnection();
    }

    @After
    public void tearDown() throws Exception {
        connection.close();
    }

    @Test
    public void createSpatialFragment() throws Exception {
        FragmentSelector fragmentSelector = anno4j.createObject(FragmentSelector.class);
        fragmentSelector.setSpatialFragment(160, 120, 320, 240);

        assertEquals("#xywh=160,120,320,240", fragmentSelector.getValue());
        assertEquals(null, fragmentSelector.getSpatialFormat());
        assertEquals((Integer) 160, fragmentSelector.getX());
        assertEquals((Integer) 120, fragmentSelector.getY());
        assertEquals((Integer) 320, fragmentSelector.getWidth());
        assertEquals((Integer) 240, fragmentSelector.getHeight());
    }

    @Test
    public void createTemporalFragment() throws Exception {
        FragmentSelector fragmentSelector = anno4j.createObject(FragmentSelector.class);
        fragmentSelector.setTemporalFragment(null, 121.5);

        assertEquals("#t=npt:,121.5", fragmentSelector.getValue());
        assertEquals("npt:", fragmentSelector.getTemporalFormat());
        assertEquals(null, fragmentSelector.getStart());
        assertEquals((Double) 121.5, fragmentSelector.getEnd());
    }

    @Test
    public void createSpatialAndTemporalFragment() throws Exception {
        FragmentSelector fragmentSelector = anno4j.createObject(FragmentSelector.class);
        fragmentSelector.setSpatialFragment(160, 120, 320, 240);
        fragmentSelector.setTemporalFragment(80.0, 121.5);

        assertEquals("#xywh=160,120,320,240&t=npt:80.0,121.5", fragmentSelector.getValue());
        assertEquals(null, fragmentSelector.getSpatialFormat());
        assertEquals((Integer) 160, fragmentSelector.getX());
        assertEquals((Integer) 120, fragmentSelector.getY());
        assertEquals((Integer) 320, fragmentSelector.getWidth());
        assertEquals((Integer) 240, fragmentSelector.getHeight());
        assertEquals("npt:", fragmentSelector.getTemporalFormat());
        assertEquals((Double) 80.0, fragmentSelector.getStart());
        assertEquals((Double) 121.5, fragmentSelector.getEnd());
    }

    @Test
    public void deleteTemporalFragment() throws Exception {
        FragmentSelector fragmentSelector = anno4j.createObject(FragmentSelector.class);
        fragmentSelector.setValue("#xywh=160,120,320,240&t=npt:80.0,121.5");
        fragmentSelector.setTemporalFragment(null, null);

        assertEquals("#xywh=160,120,320,240", fragmentSelector.getValue());
        assertEquals(null, fragmentSelector.getSpatialFormat());
        assertEquals((Integer) 160, fragmentSelector.getX());
        assertEquals((Integer) 120, fragmentSelector.getY());
        assertEquals((Integer) 320, fragmentSelector.getWidth());
        assertEquals((Integer) 240, fragmentSelector.getHeight());
        assertEquals(null, fragmentSelector.getTemporalFormat());
        assertEquals(null, fragmentSelector.getStart());
        assertEquals(null, fragmentSelector.getEnd());
    }

    @Test
    public void deleteSpatialFragment() throws Exception {
        FragmentSelector fragmentSelector = anno4j.createObject(FragmentSelector.class);
        fragmentSelector.setValue("#xywh=160,120,320,240&t=npt:80.0,121.5");
        fragmentSelector.setSpatialFragment(null, null, null, null);

        assertEquals("#t=npt:80.0,121.5", fragmentSelector.getValue());
        assertEquals(null, fragmentSelector.getSpatialFormat());
        assertEquals(null, fragmentSelector.getX());
        assertEquals(null, fragmentSelector.getY());
        assertEquals(null, fragmentSelector.getWidth());
        assertEquals(null, fragmentSelector.getHeight());
        assertEquals("npt:", fragmentSelector.getTemporalFormat());
        assertEquals((Double) 80.0, fragmentSelector.getStart());
        assertEquals((Double) 121.5, fragmentSelector.getEnd());
    }

    @Test
    public void testXYWH() throws Exception {
        FragmentSelector fragmentSelector = anno4j.createObject(FragmentSelector.class);
        fragmentSelector.setValue("#xywh=160,120,320,240");

        assertEquals(null, fragmentSelector.getSpatialFormat());
        assertEquals((Integer) 160, fragmentSelector.getX());
        assertEquals((Integer) 120, fragmentSelector.getY());
        assertEquals((Integer) 320, fragmentSelector.getWidth());
        assertEquals((Integer) 240, fragmentSelector.getHeight());
    }

    @Test
    public void testXYWHAndStartAndEnd() throws Exception {
        FragmentSelector fragmentSelector = anno4j.createObject(FragmentSelector.class);
        fragmentSelector.setValue("#xywh=160,120,320,240&t=npt:10,20");

        assertEquals(null, fragmentSelector.getSpatialFormat());
        assertEquals((Integer) 160, fragmentSelector.getX());
        assertEquals((Integer) 120, fragmentSelector.getY());
        assertEquals((Integer) 320, fragmentSelector.getWidth());
        assertEquals((Integer) 240, fragmentSelector.getHeight());

        assertEquals("npt:", fragmentSelector.getTemporalFormat());
        assertEquals((Double) 10.0 , fragmentSelector.getStart());
        assertEquals((Double) 20.0 , fragmentSelector.getEnd());
    }

    @Test
    public void testXYWHWithFormatAndStartAndEnd() throws Exception {
        FragmentSelector fragmentSelector = anno4j.createObject(FragmentSelector.class);
        fragmentSelector.setValue("#xywh=percent:21,22,23,24&t=npt:10,20");

        assertEquals("percent:", fragmentSelector.getSpatialFormat());
        assertEquals((Integer) 21, fragmentSelector.getX());
        assertEquals((Integer) 22, fragmentSelector.getY());
        assertEquals((Integer) 23, fragmentSelector.getWidth());
        assertEquals((Integer) 24, fragmentSelector.getHeight());

        assertEquals("npt:", fragmentSelector.getTemporalFormat());
        assertEquals((Double) 10.0 , fragmentSelector.getStart());
        assertEquals((Double) 20.0 , fragmentSelector.getEnd());
    }

    @Test
    public void testXYWHandStartAndEndMiliseconds() throws Exception {
        FragmentSelector fragmentSelector = anno4j.createObject(FragmentSelector.class);
        fragmentSelector.setValue("#xywh=160,120,320,240&t=npt:10.23,20.232");

        assertEquals(null, fragmentSelector.getSpatialFormat());
        assertEquals((Integer) 160, fragmentSelector.getX());
        assertEquals((Integer) 120, fragmentSelector.getY());
        assertEquals((Integer) 320, fragmentSelector.getWidth());
        assertEquals((Integer) 240, fragmentSelector.getHeight());

        assertEquals("npt:", fragmentSelector.getTemporalFormat());
        assertEquals((Double) 10.23 , fragmentSelector.getStart());
        assertEquals((Double) 20.232 , fragmentSelector.getEnd());
    }

    @Test
    public void testStartandEndMilisecondsAndXYWH() throws Exception {
        FragmentSelector fragmentSelector = anno4j.createObject(FragmentSelector.class);
        fragmentSelector.setValue("#t=npt:10.23,20.232&xywh=160,120,320,240");

        assertEquals(null, fragmentSelector.getSpatialFormat());
        assertEquals((Integer) 160, fragmentSelector.getX());
        assertEquals((Integer) 120, fragmentSelector.getY());
        assertEquals((Integer) 320, fragmentSelector.getWidth());
        assertEquals((Integer) 240, fragmentSelector.getHeight());

        assertEquals("npt:", fragmentSelector.getTemporalFormat());
        assertEquals((Double) 10.23 , fragmentSelector.getStart());
        assertEquals((Double) 20.232 , fragmentSelector.getEnd());
    }

    @Test
    public void testNoStartButEndMiliseconds() throws Exception {
        FragmentSelector fragmentSelector = anno4j.createObject(FragmentSelector.class);
        fragmentSelector.setValue("#t=npt:,121.5");

        assertEquals("npt:", fragmentSelector.getTemporalFormat());
        assertEquals(null, fragmentSelector.getStart());
        assertEquals((Double) 121.5 , fragmentSelector.getEnd());
    }

}
