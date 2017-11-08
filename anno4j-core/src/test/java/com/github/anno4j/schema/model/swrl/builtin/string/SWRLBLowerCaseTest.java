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
 * Test for {@link SWRLBLowerCase}
 */
public class SWRLBLowerCaseTest extends AbstractSWRLBuiltinTest {

    @Test
    public void testSPARQLFilterExpression() throws Exception {
        Variable x = createVariable();

        SWRLBLowerCase builtin = instantiate(SWRLB.LOWERCASE, x, "Hello");
        assertEquals("?" + x.getVariableName() + " = lcase(\"hello\")", builtin.asSPARQLFilterExpression().toLowerCase());
    }

    @Test
    public void testEvaluate() throws Exception {
        assertTrue(instantiate(SWRLB.LOWERCASE, "hello", "HelLo").evaluate(new Bindings()));
        assertTrue(instantiate(SWRLB.LOWERCASE, "hello", "HELLO").evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.LOWERCASE, "HELLO", "HelLo").evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.LOWERCASE, "hello", new Date()).evaluate(new Bindings()));
    }
}