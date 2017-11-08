package com.github.anno4j.schema.model.swrl.builtin.comparison;

import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.swrl.Variable;
import com.github.anno4j.schema.model.swrl.builtin.AbstractSWRLBuiltinTest;
import com.github.anno4j.schema.model.swrl.engine.Bindings;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Test for SWRL built-in implementation {@link SWRLBGreaterThan}.
 */
public class SWRLBGreaterThanTest extends AbstractSWRLBuiltinTest {

    @Test
    public void testSPARQLFilterExpression() throws Exception {
        Variable x = createVariable();

        SWRLBGreaterThan builtin = instantiate(SWRLB.GREATER_THAN, x, 5.0);
        assertEquals("?" + x.getVariableName() + " > 5.0", builtin.asSPARQLFilterExpression());
    }

    @Test
    public void testEvaluate() throws Exception {
        assertTrue(instantiate(SWRLB.GREATER_THAN, 70L, 5.0).evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.GREATER_THAN, 5L, 5.0).evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.GREATER_THAN, 3L, 5.0).evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.GREATER_THAN, new Date(), 5.0).evaluate(new Bindings()));
    }
}