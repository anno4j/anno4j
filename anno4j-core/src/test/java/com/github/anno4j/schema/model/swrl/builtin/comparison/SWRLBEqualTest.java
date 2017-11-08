package com.github.anno4j.schema.model.swrl.builtin.comparison;

import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.swrl.Variable;
import com.github.anno4j.schema.model.swrl.builtin.AbstractSWRLBuiltinTest;
import com.github.anno4j.schema.model.swrl.engine.Bindings;
import com.github.anno4j.schema.model.swrl.engine.SolutionSet;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test for SWRL built-in implementation {@link SWRLBEqual}.
 */
public class SWRLBEqualTest extends AbstractSWRLBuiltinTest {

    @Test
    public void testSolve() throws Exception {
        Variable x = createVariable();

        // Computed variable at argument position 1:
        SWRLBEqual builtin = instantiate(SWRLB.EQUAL, x, 5);

        SolutionSet solutions = builtin.solve(new Bindings());
        assertEquals(1, solutions.size());
        Bindings bindings = solutions.iterator().next();
        assertEquals(5, bindings.get(x));

        // Computed variable at argument position 2:
        builtin = instantiate(SWRLB.EQUAL, 5, x);

        solutions = builtin.solve(new Bindings());
        assertEquals(1, solutions.size());
        bindings = solutions.iterator().next();
        assertEquals(5, bindings.get(x));
    }

    @Test
    public void testSPARQLFilterExpression() throws Exception {
        Variable x = createVariable();

        SWRLBEqual builtin = instantiate(SWRLB.EQUAL, x, 5.0);
        assertEquals("?" + x.getVariableName() + " = 5.0", builtin.asSPARQLFilterExpression());
    }

    @Test
    public void testEvaluate() throws Exception {
        assertTrue(instantiate(SWRLB.EQUAL, 5L, 5.0).evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.EQUAL, 42, 5.0).evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.EQUAL, new Date(), 5.0).evaluate(new Bindings()));
    }
}