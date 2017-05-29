package com.github.anno4j.schema_parsing.naming;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link MethodNameBuilder}.
 */
public class MethodNameBuilderTest {

    @Test
    public void testJavaPoetMethodSpec() throws Exception {
        // Test singular:
        assertEquals("getAge", MethodNameBuilder.builder("http://example.org/#age")
                                                            .getJavaPoetMethodSpec("get", false)
                                                            .name);

        // Test plural:
        assertEquals("getAges", MethodNameBuilder.builder("http://example.org/#age")
                                                        .getJavaPoetMethodSpec("get", true)
                                                        .name);

        // Test building with RDFS label:
        assertEquals("getAlter", MethodNameBuilder.builder("http://example.org/#age")
                                                            .withRDFSLabel("Alter")
                                                            .getJavaPoetMethodSpec("get", false)
                                                            .name);

        // Test resource for which no name can be extracted:
        assertTrue(MethodNameBuilder.builder("http://example.org/#")
                .getJavaPoetMethodSpec("foo", false)
                .name.startsWith("fooUnnamed"));
    }
}