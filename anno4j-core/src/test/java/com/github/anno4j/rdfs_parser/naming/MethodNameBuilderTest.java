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

}