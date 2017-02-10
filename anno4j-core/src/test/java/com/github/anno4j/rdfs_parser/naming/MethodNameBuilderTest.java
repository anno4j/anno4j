package com.github.anno4j.rdfs_parser.naming;

import com.github.anno4j.model.namespaces.FOAF;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for {@link MethodNameBuilder}.
 */
public class MethodNameBuilderTest {
    @Test
    public void getterSpec() throws Exception {
        assertEquals("getName", MethodNameBuilder.builder("http://example.com/ont#name")
                                                            .getterSpec()
                                                            .name);

        assertEquals("getName", MethodNameBuilder.builder("http://example.com/name")
                                                            .getterSpec()
                                                            .name);
    }

    @Test
    public void setterSpec() throws Exception {
        assertEquals("setName", MethodNameBuilder.builder("http://example.com/ont#name")
                .setterSpec()
                .name);

        assertEquals("setName", MethodNameBuilder.builder("http://example.com/name")
                .setterSpec()
                .name);
    }

    @Test
    public void adderSpec() throws Exception {
        assertEquals("addName", MethodNameBuilder.builder("http://example.com/ont#name")
                .adderSpec()
                .name);

        assertEquals("addName", MethodNameBuilder.builder("http://example.com/name")
                .adderSpec()
                .name);
    }
}