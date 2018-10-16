package com.github.anno4j.model.impl;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.AnnotationSupport;
import com.github.anno4j.model.CreationProvenance;
import com.github.anno4j.model.impl.agent.Person;
import com.github.anno4j.model.namespaces.OADM;
import org.openrdf.model.impl.URIImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Partial class used for {@link ResourceObjectSupportTest} tests that must be run inside a support class.
 */
@Partial
public abstract class SpecialAnnotationSupport extends AnnotationSupport implements ResourceObjectSupportTest.SpecialAnnotation {

    @Override
    public void testTypeChecking() throws Exception {
        assertTrue(isInstance(Annotation.class));
        assertTrue(isInstance(new URIImpl(OADM.ANNOTATION)));
        assertTrue(isInstance(CreationProvenance.class));
        assertTrue(isInstance(ResourceObject.class));
        assertFalse(isInstance(Person.class));
    }

    @Override
    public void testCasting() throws Exception {
        // Test up-cast:
        Annotation annotation = cast(Annotation.class);
        assertEquals(getResourceAsString(), annotation.getResourceAsString());
        CreationProvenance provenance = cast(CreationProvenance.class);
        assertEquals(getResourceAsString(), provenance.getResourceAsString());
        ResourceObject resourceObject = cast(ResourceObject.class);
        assertEquals(getResourceAsString(), resourceObject.getResourceAsString());

        // Test downcast:
        ResourceObjectSupportTest.VerySpecialAnnotation verySpecialAnnotation = cast(ResourceObjectSupportTest.VerySpecialAnnotation.class);
        assertEquals(getResourceAsString(), verySpecialAnnotation.getResourceAsString());

        // Test wrong type cast:
        boolean exceptionThrown = false;
        try {
            cast(Person.class);
        } catch (ClassCastException ignored) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }
}
