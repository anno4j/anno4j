package com.github.anno4j.schema.model.swrl.builtin.math;

import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.swrl.Variable;
import com.github.anno4j.schema.model.swrl.builtin.AbstractSWRLBuiltinTest;
import com.github.anno4j.schema.model.swrl.engine.Bindings;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for SWRL built-in implementation {@link SWRLBFloor}.
 */
public class SWRLBFloorTest extends AbstractSWRLBuiltinTest {

    @Test
    public void testSPARQLFilterExpression() throws Exception {
        Variable x = createVariable();

        SWRLBFloor builtin = instantiate(SWRLB.FLOOR, 5.0, x);
        assertEquals("5.0 = floor(?" + x.getVariableName() + ")", builtin.asSPARQLFilterExpression().toLowerCase());
    }

    @Test
    public void testEvaluate() throws Exception {
        assertTrue(instantiate(SWRLB.FLOOR, 13L, 13.37).evaluate(new Bindings()));
        assertTrue(instantiate(SWRLB.FLOOR, -14L, -13.37).evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.FLOOR, 5L, 1.234).evaluate(new Bindings()));
    }
}