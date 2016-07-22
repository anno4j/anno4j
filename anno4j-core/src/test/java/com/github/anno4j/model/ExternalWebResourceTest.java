package com.github.anno4j.model;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.impl.targets.SpecificResource;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.repository.RepositoryException;

import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * Test suite for the ExternalWebResource interface and classes inheriting it.
 */
public class ExternalWebResourceTest {

    private Anno4j anno4j;

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
    }

    @Test
    public void testLanguages() throws RepositoryException, IllegalAccessException, InstantiationException {
        Annotation annotation = this.anno4j.createObject(Annotation.class);

        SpecificResource specificResource = this.anno4j.createObject(SpecificResource.class);
        annotation.addTarget(specificResource);

        Annotation result = this.anno4j.findByID(Annotation.class, annotation.getResourceAsString());

        assertEquals(0, ((SpecificResource)result.getTargets().toArray()[0]).getLanguages().size());

        specificResource.addLanguage("de");

        result = this.anno4j.findByID(Annotation.class, annotation.getResourceAsString());

        assertEquals(1, ((SpecificResource)result.getTargets().toArray()[0]).getLanguages().size());

        HashSet<String> languages = new HashSet<>();
        languages.add("de");
        languages.add("en");

        specificResource.setLanguages(languages);

        result = this.anno4j.findByID(Annotation.class, annotation.getResourceAsString());

        assertEquals(2, ((SpecificResource)result.getTargets().toArray()[0]).getLanguages().size());
    }

    @Test
    public void testTextDirection() throws RepositoryException, IllegalAccessException, InstantiationException {
        Annotation annotation = this.anno4j.createObject(Annotation.class);

        SpecificResource specificResource = this.anno4j.createObject(SpecificResource.class);
        annotation.addTarget(specificResource);

        ResourceObject textDirection = TextDirectionFactory.getLeftToRight(this.anno4j);
        specificResource.setTextDirection(textDirection);

        Annotation result = this.anno4j.findByID(Annotation.class, annotation.getResourceAsString());

        assertEquals(textDirection.getResourceAsString(), ((SpecificResource)result.getTargets().toArray()[0]).getTextDirection().getResourceAsString());
    }
}