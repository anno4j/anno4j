package com.github.anno4j.schema.model.swrl.builtin.string;

import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.swrl.Variable;
import com.github.anno4j.schema.model.swrl.builtin.AbstractSWRLBuiltinTest;
import com.github.anno4j.schema.model.swrl.engine.Bindings;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Test for {@link SWRLBStartsWith}.
 */
public class SWRLBStartsWithTest extends AbstractSWRLBuiltinTest {

    @Test
    public void testSPARQLFilterExpression() throws Exception {
        Variable x = createVariable();

        SWRLBStartsWith builtin = instantiate(SWRLB.STARTS_WITH, x, "hello");
        assertEquals("strstarts(?" + x.getVariableName() + ", \"hello\")", builtin.asSPARQLFilterExpression().toLowerCase());
    }

    @Test
    public void testEvaluate() throws Exception {
        assertTrue(instantiate(SWRLB.STARTS_WITH, "hello world", "hello").evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.STARTS_WITH, "hello world", "Hello").evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.STARTS_WITH, "hello world", "world").evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.STARTS_WITH, "hello", new Date()).evaluate(new Bindings()));
    }
}