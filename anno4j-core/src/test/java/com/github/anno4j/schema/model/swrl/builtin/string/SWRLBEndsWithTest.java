package com.github.anno4j.schema.model.swrl.builtin.string;

import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.swrl.Variable;
import com.github.anno4j.schema.model.swrl.builtin.AbstractSWRLBuiltinTest;
import com.github.anno4j.schema.model.swrl.engine.Bindings;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Test for {@link SWRLBEndsWith}.
 */
public class SWRLBEndsWithTest extends AbstractSWRLBuiltinTest {

    @Test
    public void testSPARQLFilterExpression() throws Exception {
        Variable x = createVariable();

        SWRLBEndsWith builtin = instantiate(SWRLB.ENDS_WITH, x, "hello");
        assertEquals("strends(?" + x.getVariableName() + ", \"hello\")", builtin.asSPARQLFilterExpression().toLowerCase());
    }

    @Test
    public void testEvaluate() throws Exception {
        assertTrue(instantiate(SWRLB.ENDS_WITH, "hello world", "world").evaluate(new Bindings()));
        assertTrue(instantiate(SWRLB.ENDS_WITH, "hello world", "hello world").evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.ENDS_WITH, "hello world", "World").evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.ENDS_WITH, "hello world", "hello").evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.ENDS_WITH, "hello", new Date()).evaluate(new Bindings()));
    }
}