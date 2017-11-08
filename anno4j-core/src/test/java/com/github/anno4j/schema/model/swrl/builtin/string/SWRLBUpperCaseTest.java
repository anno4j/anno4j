package com.github.anno4j.schema.model.swrl.builtin.string;

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
 * Test for {@link SWRLBUpperCase}.
 */
public class SWRLBUpperCaseTest extends AbstractSWRLBuiltinTest {

    @Test
    public void testSPARQLFilterExpression() throws Exception {
        Variable x = createVariable();

        SWRLBUpperCase builtin = instantiate(SWRLB.UPPERCASE, x, "Hello");
        assertEquals("?" + x.getVariableName() + " = ucase(\"hello\")", builtin.asSPARQLFilterExpression().toLowerCase());
    }

    @Test
    public void testEvaluate() throws Exception {
        assertTrue(instantiate(SWRLB.UPPERCASE, "HELLO", "HelLo").evaluate(new Bindings()));
        assertTrue(instantiate(SWRLB.UPPERCASE, "HELLO", "HELLO").evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.UPPERCASE, "hello", "HelLo").evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.UPPERCASE, "hello", new Date()).evaluate(new Bindings()));
    }
}