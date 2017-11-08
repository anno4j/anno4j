package com.github.anno4j.schema.model.swrl.builtin.comparison;

import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.swrl.Variable;
import com.github.anno4j.schema.model.swrl.builtin.AbstractSWRLBuiltinTest;
import com.github.anno4j.schema.model.swrl.engine.Bindings;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test for SWRL built-in implementation {@link SWRLBLessThan}.
 */
public class SWRLBLessThanTest extends AbstractSWRLBuiltinTest {

    @Test
    public void testSPARQLFilterExpression() throws Exception {
        Variable x = createVariable();

        SWRLBLessThan builtin = instantiate(SWRLB.LESS_THAN, x, 5.0);
        assertEquals("?" + x.getVariableName() + " < 5.0", builtin.asSPARQLFilterExpression());
    }

    @Test
    public void testEvaluate() throws Exception {
        assertFalse(instantiate(SWRLB.LESS_THAN, 70L, 5.0).evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.LESS_THAN, 5L, 5.0).evaluate(new Bindings()));
        assertTrue(instantiate(SWRLB.LESS_THAN, 3L, 5.0).evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.LESS_THAN, new Date(), 5.0).evaluate(new Bindings()));
    }
}