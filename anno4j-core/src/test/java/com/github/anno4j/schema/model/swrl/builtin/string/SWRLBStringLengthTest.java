package com.github.anno4j.schema.model.swrl.builtin.string;

import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.swrl.Variable;
import com.github.anno4j.schema.model.swrl.builtin.AbstractSWRLBuiltinTest;
import com.github.anno4j.schema.model.swrl.engine.Bindings;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test for SWRL built-in implementation {@link SWRLBStringLength}.
 */
public class SWRLBStringLengthTest extends AbstractSWRLBuiltinTest {

    @Test
    public void testSPARQLFilterExpression() throws Exception {
        Variable x = createVariable();

        SWRLBStringLength builtin = instantiate(SWRLB.STRING_LENGTH, x, "Hello");
        assertEquals("?" + x.getVariableName() + " = strlen(\"hello\")", builtin.asSPARQLFilterExpression().toLowerCase());
    }

    @Test
    public void testEvaluate() throws Exception {
        assertTrue(instantiate(SWRLB.STRING_LENGTH, 3, "abc").evaluate(new Bindings()));
        assertTrue(instantiate(SWRLB.STRING_LENGTH, 0, "").evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.STRING_LENGTH, 42, "Hello").evaluate(new Bindings()));
    }
}