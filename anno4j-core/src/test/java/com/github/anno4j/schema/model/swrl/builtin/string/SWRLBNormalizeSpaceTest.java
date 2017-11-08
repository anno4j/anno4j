package com.github.anno4j.schema.model.swrl.builtin.string;

import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.swrl.builtin.AbstractSWRLBuiltinTest;
import com.github.anno4j.schema.model.swrl.engine.Bindings;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test for SWRL built-in implementation {@link SWRLBNormalizeSpace}.
 */
public class SWRLBNormalizeSpaceTest extends AbstractSWRLBuiltinTest {

    @Test
    public void testEvaluate() throws Exception {
        assertTrue(instantiate(SWRLB.NORMALIZE_SPACE, "Hello World", "  Hello     World ").evaluate(new Bindings()));
        assertTrue(instantiate(SWRLB.NORMALIZE_SPACE, "This is a test", "This is a test").evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.NORMALIZE_SPACE, "Hello World    ", "   Hello     World  ").evaluate(new Bindings()));
    }
}