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
 * Test for SWRL built-in implementation {@link SWRLBLessThanOrEqual}.
 */
public class SWRLBLessThanOrEqualTest extends AbstractSWRLBuiltinTest {

    @Test
    public void testSPARQLFilterExpression() throws Exception {
        Variable x = createVariable();

        SWRLBLessThanOrEqual builtin = instantiate(SWRLB.LESS_THAN_OR_EQUAL, x, 5.0);
        assertEquals("?" + x.getVariableName() + " <= 5.0", builtin.asSPARQLFilterExpression());
    }

    @Test
    public void testEvaluate() throws Exception {
        assertFalse(instantiate(SWRLB.LESS_THAN_OR_EQUAL, 70L, 5.0).evaluate(new Bindings()));
        assertTrue(instantiate(SWRLB.LESS_THAN_OR_EQUAL, 5L, 5.0).evaluate(new Bindings()));
        assertTrue(instantiate(SWRLB.LESS_THAN_OR_EQUAL, 3L, 5.0).evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.LESS_THAN_OR_EQUAL, new Date(), 5.0).evaluate(new Bindings()));
    }

}