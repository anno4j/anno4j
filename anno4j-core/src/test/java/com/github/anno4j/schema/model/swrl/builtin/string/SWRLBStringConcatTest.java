package com.github.anno4j.schema.model.swrl.builtin.string;

import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.swrl.builtin.AbstractSWRLBuiltinTest;
import com.github.anno4j.schema.model.swrl.engine.Bindings;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test for SWRL built-in implementation {@link SWRLBStringConcat}.
 */
public class SWRLBStringConcatTest extends AbstractSWRLBuiltinTest {

    @Test
    public void testEvaluate() throws Exception {
        assertTrue(instantiate(SWRLB.STRING_CONCAT, "Hello World", "Hello ", "World").evaluate(new Bindings()));
        assertTrue(instantiate(SWRLB.STRING_CONCAT, "This is a very long text", "This ", "is ", "a ", "very ", "long ", "text").evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.STRING_CONCAT, "Hello", "Bye", "Bye").evaluate(new Bindings()));
    }
}