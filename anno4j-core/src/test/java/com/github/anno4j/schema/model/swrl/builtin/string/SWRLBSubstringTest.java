package com.github.anno4j.schema.model.swrl.builtin.string;

import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.swrl.Variable;
import com.github.anno4j.schema.model.swrl.builtin.AbstractSWRLBuiltinTest;
import com.github.anno4j.schema.model.swrl.engine.Bindings;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for SWRL built-in implementation {@link SWRLBStringConcat}.
 */
public class SWRLBSubstringTest extends AbstractSWRLBuiltinTest {

    @Test
    public void testSPARQLFilterExpression() throws Exception {
        Variable x = createVariable();

        SWRLBSubstring builtin = instantiate(SWRLB.SUBSTRING, x, "Hello", 3);
        assertEquals("?" + x.getVariableName() + " = substr(\"hello\", 3)", builtin.asSPARQLFilterExpression().toLowerCase());
    }

    @Test
    public void testEvaluate() throws Exception {
        assertTrue(instantiate(SWRLB.SUBSTRING, "llo", "Hello", 2).evaluate(new Bindings()));
        assertTrue(instantiate(SWRLB.SUBSTRING, "World", "Hello World", 6).evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.SUBSTRING, "", "Hello", 5).evaluate(new Bindings()));

        assertTrue(instantiate(SWRLB.SUBSTRING, "l", "Hello", 2, 1).evaluate(new Bindings()));
        assertTrue(instantiate(SWRLB.SUBSTRING, "Hello", "Hello World", 0, 5).evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.SUBSTRING, "", "Hello", 3, 6).evaluate(new Bindings()));
    }
}