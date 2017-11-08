package com.github.anno4j.schema.model.swrl.builtin.math;

import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.swrl.Variable;
import com.github.anno4j.schema.model.swrl.builtin.AbstractSWRLBuiltinTest;
import com.github.anno4j.schema.model.swrl.engine.Bindings;
import com.github.anno4j.schema.model.swrl.engine.SolutionSet;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for SWRL built-in implementation {@link SWRLBDivide}.
 */
public class SWRLBDivideTest extends AbstractSWRLBuiltinTest {

    @Test
    public void testSolve() throws Exception {
        Variable x = createVariable();

        // Test: 4 = 12 / 3

        // Computed variable at argument position 1:
        SWRLBDivide builtin = instantiate(SWRLB.DIVIDE, x, 12L, 3.0);

        SolutionSet solutions = builtin.solve(new Bindings());
        assertEquals(1, solutions.size());
        Bindings bindings = solutions.iterator().next();
        assertEquals(4.0, bindings.get(x));

        // Computed variable at argument position 2:
        builtin = instantiate(SWRLB.DIVIDE, 4.0, x, 3.0);

        solutions = builtin.solve(new Bindings());
        assertEquals(1, solutions.size());
        bindings = solutions.iterator().next();
        assertEquals(12.0, bindings.get(x));

        // Computed variable at argument position 3:
        builtin = instantiate(SWRLB.DIVIDE, 4.0, 12L, x);

        solutions = builtin.solve(new Bindings());
        assertEquals(1, solutions.size());
        bindings = solutions.iterator().next();
        assertEquals(3.0, bindings.get(x));

        // Special case: division by zero
        builtin = instantiate(SWRLB.DIVIDE, x, 12L, 0);

        solutions = builtin.solve(new Bindings());
        assertEquals(0, solutions.size());
    }

    @Test
    public void testEvaluate() throws Exception {
        assertTrue(instantiate(SWRLB.DIVIDE, 5L, 25.0, 5).evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.DIVIDE, 5L, 25.0, 4).evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.DIVIDE, 5L, 25.0, 0).evaluate(new Bindings()));
    }
}