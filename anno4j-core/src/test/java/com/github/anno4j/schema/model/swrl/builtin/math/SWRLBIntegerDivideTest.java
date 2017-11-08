package com.github.anno4j.schema.model.swrl.builtin.math;

import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.swrl.Variable;
import com.github.anno4j.schema.model.swrl.builtin.AbstractSWRLBuiltinTest;
import com.github.anno4j.schema.model.swrl.engine.Bindings;
import com.github.anno4j.schema.model.swrl.engine.SolutionSet;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for SWRL built-in implementation {@link SWRLBIntegerDivide}.
 */
public class SWRLBIntegerDivideTest extends AbstractSWRLBuiltinTest {

    @Test
    public void testSolve() throws Exception {
        Variable x = createVariable();

        // Test: 5 = 16 `idiv` 3

        // Computed variable at argument position 1:
        SWRLBIntegerDivide builtin = instantiate(SWRLB.INTEGER_DIVIDE, x, 16L, 3.0);

        SolutionSet solutions = builtin.solve(new Bindings());
        assertEquals(1, solutions.size());
        Bindings bindings = solutions.iterator().next();
        assertEquals(5, bindings.get(x));

        // Computed variable at argument position 2:
        builtin = instantiate(SWRLB.INTEGER_DIVIDE, 5.0, x, 3.0);

        solutions = builtin.solve(new Bindings());
        assertEquals(3, solutions.size());

        // Computed variable at argument position 3:
        builtin = instantiate(SWRLB.INTEGER_DIVIDE, 5.0, 16L, x);

        solutions = builtin.solve(new Bindings());
        assertEquals(1, solutions.size());
        bindings = solutions.iterator().next();
        assertEquals(3, bindings.get(x));
    }

    @Test
    public void testEvaluate() throws Exception {
        assertTrue(instantiate(SWRLB.INTEGER_DIVIDE, 5L, 16.0, 3).evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.INTEGER_DIVIDE, 5L, 16.0, 4).evaluate(new Bindings()));
    }
}