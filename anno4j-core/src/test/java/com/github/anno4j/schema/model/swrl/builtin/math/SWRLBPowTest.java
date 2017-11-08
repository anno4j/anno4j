package com.github.anno4j.schema.model.swrl.builtin.math;

import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.swrl.Variable;
import com.github.anno4j.schema.model.swrl.builtin.AbstractSWRLBuiltinTest;
import com.github.anno4j.schema.model.swrl.engine.Bindings;
import com.github.anno4j.schema.model.swrl.engine.SolutionSet;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for SWRL built-in implementation {@link SWRLBPow}.
 */
public class SWRLBPowTest extends AbstractSWRLBuiltinTest {
    @Test
    public void testSolve() throws Exception {
        Variable x = createVariable();

        // Test: 32 = 2 ^ 5

        // Computed variable at argument position 1:
        SWRLBPow builtin = instantiate(SWRLB.POW, x, 2L, 5.0);

        SolutionSet solutions = builtin.solve(new Bindings());
        assertEquals(1, solutions.size());
        Bindings bindings = solutions.iterator().next();
        assertEquals(32.0, bindings.get(x));

        // Computed variable at argument position 2:
        builtin = instantiate(SWRLB.POW, 32.0, x, 5.0);

        solutions = builtin.solve(new Bindings());
        assertEquals(1, solutions.size());
        bindings = solutions.iterator().next();
        assertEquals(2.0, bindings.get(x));

        // Computed variable at argument position 3:
        builtin = instantiate(SWRLB.POW, 32.0, 2L, x);

        solutions = builtin.solve(new Bindings());
        assertEquals(1, solutions.size());
        bindings = solutions.iterator().next();
        assertEquals(5.0, bindings.get(x));
    }

    @Test
    public void testEvaluate() throws Exception {
        assertTrue(instantiate(SWRLB.POW, 625L, 25.0, 2L).evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.POW, 626L, 25.0, 2L).evaluate(new Bindings()));
    }

}