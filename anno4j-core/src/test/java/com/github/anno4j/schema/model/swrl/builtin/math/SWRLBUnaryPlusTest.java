package com.github.anno4j.schema.model.swrl.builtin.math;

import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.swrl.Variable;
import com.github.anno4j.schema.model.swrl.builtin.AbstractSWRLBuiltinTest;
import com.github.anno4j.schema.model.swrl.engine.Bindings;
import com.github.anno4j.schema.model.swrl.engine.SolutionSet;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for SWRL built-in implementation {@link SWRLBUnaryPlus}.
 */
public class SWRLBUnaryPlusTest extends AbstractSWRLBuiltinTest {

    @Test
    public void testSolve() throws Exception {
        Variable x = createVariable();

        // Test: -5

        // Computed variable at argument position 1:
        SWRLBUnaryPlus builtin = instantiate(SWRLB.UNARY_PLUS, x, -5L);

        SolutionSet solutions = builtin.solve(new Bindings());
        assertEquals(1, solutions.size());
        Bindings bindings = solutions.iterator().next();
        assertEquals(-5.0, bindings.get(x));

        // Computed variable at argument position 2:
        builtin = instantiate(SWRLB.UNARY_PLUS, -5, x);

        solutions = builtin.solve(new Bindings());
        assertEquals(1, solutions.size());
        bindings = solutions.iterator().next();
        assertEquals(-5.0, bindings.get(x));
    }

    @Test
    public void testEvaluate() throws Exception {
        assertTrue(instantiate(SWRLB.UNARY_PLUS, -2.0, -2).evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.UNARY_PLUS, 2.0, -2).evaluate(new Bindings()));
    }
}