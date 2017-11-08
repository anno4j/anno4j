package com.github.anno4j.schema.model.swrl.builtin.math;

import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.swrl.Variable;
import com.github.anno4j.schema.model.swrl.builtin.AbstractSWRLBuiltinTest;
import com.github.anno4j.schema.model.swrl.engine.Bindings;
import com.github.anno4j.schema.model.swrl.engine.SolutionSet;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for SWRL built-in implementation {@link SWRLBAbs}.
 */
public class SWRLBAbsTest extends AbstractSWRLBuiltinTest {

    @Test
    public void testSolve() throws Exception {
        Variable x = createVariable();


        // Computed variable at argument position 1:
        SWRLBAbs builtin = instantiate(SWRLB.ABS, x, -12L);

        SolutionSet solutions = builtin.solve(new Bindings());
        assertEquals(1, solutions.size());
        Bindings bindings = solutions.iterator().next();
        assertEquals(12.0, bindings.get(x));

        // Computed variable at argument position 2:
        builtin = instantiate(SWRLB.ABS, 4.0, x);

        solutions = builtin.solve(new Bindings());
        assertEquals(2, solutions.size());
    }

    @Test
    public void testSPARQLFilterExpression() throws Exception {
        Variable x = createVariable();

        SWRLBAbs builtin = instantiate(SWRLB.ABS, 5.0, x);
        assertEquals("5.0 = abs(?" + x.getVariableName() + ")", builtin.asSPARQLFilterExpression().toLowerCase());
    }

    @Test
    public void testEvaluate() throws Exception {
        assertTrue(instantiate(SWRLB.ABS, 5L, -5.0).evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.ABS, -5L, 5.0).evaluate(new Bindings()));
    }
}