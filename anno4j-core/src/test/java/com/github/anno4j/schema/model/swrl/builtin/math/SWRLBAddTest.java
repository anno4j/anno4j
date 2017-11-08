package com.github.anno4j.schema.model.swrl.builtin.math;

import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.swrl.Variable;
import com.github.anno4j.schema.model.swrl.builtin.AbstractSWRLBuiltinTest;
import com.github.anno4j.schema.model.swrl.engine.Bindings;
import com.github.anno4j.schema.model.swrl.engine.SolutionSet;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for SWRL built-in implementation {@link SWRLBAdd}.
 */
public class SWRLBAddTest extends AbstractSWRLBuiltinTest {

    @Test
    public void testSolve() throws Exception {
        Variable x = createVariable();

        // Test: 15 = 5 + 10

        // Computed variable at argument position 1:
        SWRLBAdd builtin = instantiate(SWRLB.ADD, x, 5L, 10.0);

        SolutionSet solutions = builtin.solve(new Bindings());
        assertEquals(1, solutions.size());
        Bindings bindings = solutions.iterator().next();
        assertEquals(15.0, bindings.get(x));

        // Computed variable at argument position 2:
        builtin = instantiate(SWRLB.ADD, 15.0, x, 10.0);

        solutions = builtin.solve(new Bindings());
        assertEquals(1, solutions.size());
        bindings = solutions.iterator().next();
        assertEquals(5.0, bindings.get(x));

        // Computed variable at argument position 3:
        builtin = instantiate(SWRLB.ADD, 15.0, 5L, x);

        solutions = builtin.solve(new Bindings());
        assertEquals(1, solutions.size());
        bindings = solutions.iterator().next();
        assertEquals(10.0, bindings.get(x));
    }

    @Test
    public void testEvaluate() throws Exception {
        assertTrue(instantiate(SWRLB.ADD, 3L, 5.0, -2).evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.ADD, 3L, 5.0, 2).evaluate(new Bindings()));
    }
}