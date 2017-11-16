package com.github.anno4j.model.impl;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.annotations.Iri;
import org.openrdf.model.Resource;

/**
 * Test for {@link ResourceObjectSupport}.
 * Tests for instance checking and casting are implemented in {@link SpecialAnnotationSupport}.
 */
public class ResourceObjectSupportTest {

    /**
     * Subtype of the OADM annotation which provides a testing method.
     */
    @Iri("http://example.de/special_annotation")
    public interface SpecialAnnotation extends Annotation {

        /**
         * Tests {@link ResourceObjectSupport#isInstance(Resource)} and {@link ResourceObjectSupport#isInstance(Class)}.
         * Is implemented in {@link SpecialAnnotationSupport} because these tests must be run inside a support class.
         */
        void testTypeChecking() throws Exception;

        /**
         * Tests {@link ResourceObjectSupport#cast(Class)}.
         * Is implemented in {@link SpecialAnnotationSupport} because these tests must be run inside a support class.
         */
        void testCasting() throws Exception;
    }

    /**
     * Subtype of the testing annotation for testing down-casts.
     */
    @Iri("http://example.de/very_special_annotation")
    public interface VerySpecialAnnotation extends SpecialAnnotation {
    }

    /**
     * The resource object used for testing.
     */
    private VerySpecialAnnotation annotation;

    @Before
    public void setUp() throws Exception {
        Anno4j anno4j = new Anno4j();
        annotation = anno4j.createObject(VerySpecialAnnotation.class);
    }

    /**
     * Tests instance checking and casting.
     */
    @Test
    public void testTypeChecking() throws Exception {
        annotation.testTypeChecking();
    }

    /**
     * Tests instance checking and casting.
     */
    @Test
    public void testCasting() throws Exception {
        annotation.testCasting();
    }
}