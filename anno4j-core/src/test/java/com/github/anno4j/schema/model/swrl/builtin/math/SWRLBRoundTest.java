package com.github.anno4j.schema.model.swrl.builtin.math;

import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.swrl.Variable;
import com.github.anno4j.schema.model.swrl.builtin.AbstractSWRLBuiltinTest;
import com.github.anno4j.schema.model.swrl.engine.Bindings;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test for SWRL built-in implementation {@link SWRLBRound}.
 */
public class SWRLBRoundTest extends AbstractSWRLBuiltinTest {

    @Test
    public void testSPARQLFilterExpression() throws Exception {
        Variable x = createVariable();

        SWRLBRound builtin = instantiate(SWRLB.ROUND, 5.0, x);
        assertEquals("5.0 = round(?" + x.getVariableName() + ")", builtin.asSPARQLFilterExpression().toLowerCase());
    }

    @Test
    public void testEvaluate() throws Exception {
        assertTrue(instantiate(SWRLB.ROUND, 13L, 13.37).evaluate(new Bindings()));
        assertTrue(instantiate(SWRLB.ROUND, -13L, -13.37).evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.ROUND, 5L, 1.234).evaluate(new Bindings()));
    }
}