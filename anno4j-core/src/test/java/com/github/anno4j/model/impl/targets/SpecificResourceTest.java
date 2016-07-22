package com.github.anno4j.model.impl.targets;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.State;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.impl.state.HttpRequestState;
import com.github.anno4j.model.impl.state.TimeState;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.repository.RepositoryException;

import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * Test suite for the SpecificResource interface.
 */
public class SpecificResourceTest {

    private Anno4j anno4j;

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
    }

    @Test
    public void testStyleClass() throws RepositoryException, IllegalAccessException, InstantiationException {
        Annotation annotation = this.anno4j.createObject(Annotation.class);

        SpecificResource specificResource = this.anno4j.createObject(SpecificResource.class);

        annotation.addTarget(specificResource);

        Annotation result = this.anno4j.findByID(Annotation.class, annotation.getResourceAsString());
        assertEquals(0, ((SpecificResource) result.getTargets().iterator().next()).getStyleClasses().size());

        specificResource.addStyleClass("red");

        result = this.anno4j.findByID(Annotation.class, annotation.getResourceAsString());
        assertEquals(1, ((SpecificResource) result.getTargets().iterator().next()).getStyleClasses().size());

        HashSet<String> styleClasses = new HashSet<>();
        styleClasses.add("green");
        styleClasses.add("blue");
        specificResource.setStyleClasses(styleClasses);

        result = this.anno4j.findByID(Annotation.class, annotation.getResourceAsString());
        assertEquals(2, ((SpecificResource) result.getTargets().iterator().next()).getStyleClasses().size());
    }

    @Test
    public void testStates() throws RepositoryException, IllegalAccessException, InstantiationException {
        SpecificResource specificResource = this.anno4j.createObject(SpecificResource.class);

        SpecificResource result = this.anno4j.findByID(SpecificResource.class, specificResource.getResourceAsString());

        assertEquals(0, result.getStates().size());

        specificResource.addState(this.anno4j.createObject(TimeState.class));

        result = this.anno4j.findByID(SpecificResource.class, specificResource.getResourceAsString());

        assertEquals(1, result.getStates().size());

        HashSet<State> states = new HashSet<>();
        states.add(this.anno4j.createObject(HttpRequestState.class));
        states.add(this.anno4j.createObject(HttpRequestState.class));

        specificResource.setStates(states);

        result = this.anno4j.findByID(SpecificResource.class, specificResource.getResourceAsString());

        assertEquals(2, result.getStates().size());
    }

    @Test
    public void testRenderedVia() throws RepositoryException, IllegalAccessException, InstantiationException {
        SpecificResource specificResource = this.anno4j.createObject(SpecificResource.class);

        SpecificResource result = this.anno4j.findByID(SpecificResource.class, specificResource.getResourceAsString());

        assertEquals(0, result.getRenderedVia().size());

        specificResource.addRenderedVia(this.anno4j.createObject(ResourceObject.class));

        result = this.anno4j.findByID(SpecificResource.class, specificResource.getResourceAsString());

        assertEquals(1, result.getRenderedVia().size());

        HashSet<ResourceObject> rendered = new HashSet<>();
        rendered.add(this.anno4j.createObject(ResourceObject.class));
        rendered.add(this.anno4j.createObject(ResourceObject.class));

        specificResource.setRenderedVia(rendered);

        result = this.anno4j.findByID(SpecificResource.class, specificResource.getResourceAsString());

        assertEquals(2, result.getRenderedVia().size());
    }

}