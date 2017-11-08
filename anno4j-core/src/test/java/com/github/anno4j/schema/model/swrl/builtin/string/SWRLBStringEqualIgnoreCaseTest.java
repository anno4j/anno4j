package com.github.anno4j.schema.model.swrl.builtin.string;

import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.swrl.Variable;
import com.github.anno4j.schema.model.swrl.builtin.AbstractSWRLBuiltinTest;
import com.github.anno4j.schema.model.swrl.engine.Bindings;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for SWRL built-in implementation {@link SWRLBStringEqualIgnoreCase}.
 */
public class SWRLBStringEqualIgnoreCaseTest extends AbstractSWRLBuiltinTest {

    @Test
    public void testSPARQLFilterExpression() throws Exception {
        Variable x = createVariable();

        SWRLBStringEqualIgnoreCase builtin = instantiate(SWRLB.STRING_EQUAL_IGNORE_CASE, "Hello World", x);
        assertEquals("lcase(\"hello world\") = lcase(?" + x.getVariableName() + ")", builtin.asSPARQLFilterExpression().toLowerCase());
    }

    @Test
    public void testEvaluate() throws Exception {
        assertTrue(instantiate(SWRLB.STRING_EQUAL_IGNORE_CASE, "Hello World", "HELLO WORLD").evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.STRING_EQUAL_IGNORE_CASE, "Hello", "World").evaluate(new Bindings()));
    }
}