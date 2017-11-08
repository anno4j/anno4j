package com.github.anno4j.schema.model.swrl.builtin.math;

import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.swrl.builtin.AbstractSWRLBuiltinTest;
import com.github.anno4j.schema.model.swrl.engine.Bindings;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test for SWRL built-in implementation {@link SWRLBMod}.
 */
public class SWRLBModTest extends AbstractSWRLBuiltinTest {

    @Test
    public void testEvaluate() throws Exception {
        assertTrue(instantiate(SWRLB.MOD, 1L, 15.0, 2).evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.MOD, 0L, 15.0, 2).evaluate(new Bindings()));
    }
}