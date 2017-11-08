package com.github.anno4j.schema.model.swrl.builtin.string;

import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.swrl.Variable;
import com.github.anno4j.schema.model.swrl.builtin.AbstractSWRLBuiltinTest;
import com.github.anno4j.schema.model.swrl.engine.Bindings;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Test for {@link SWRLBContainsIgnoreCase}.
 */
public class SWRLBContainsIgnoreCaseTest extends AbstractSWRLBuiltinTest {

    @Test
    public void testSPARQLFilterExpression() throws Exception {
        Variable x = createVariable();

        SWRLBContainsIgnoreCase builtin = instantiate(SWRLB.CONTAINS_IGNORE_CASE, x, "Hello");
        assertEquals("contains(lcase(?" + x.getVariableName() + "), lcase(\"hello\"))", builtin.asSPARQLFilterExpression().toLowerCase());
    }

    @Test
    public void testEvaluate() throws Exception {
        assertTrue(instantiate(SWRLB.CONTAINS_IGNORE_CASE, "heLlO", "lLo").evaluate(new Bindings()));
        assertTrue(instantiate(SWRLB.CONTAINS_IGNORE_CASE, "hello", "ll").evaluate(new Bindings()));
        assertTrue(instantiate(SWRLB.CONTAINS_IGNORE_CASE, "hello", "LL").evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.CONTAINS_IGNORE_CASE, "hello", "world").evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.CONTAINS_IGNORE_CASE, "hello", new Date()).evaluate(new Bindings()));
    }
}