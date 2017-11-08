package com.github.anno4j.schema.model.swrl.builtin.string;

import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.swrl.Variable;
import com.github.anno4j.schema.model.swrl.builtin.AbstractSWRLBuiltinTest;
import com.github.anno4j.schema.model.swrl.engine.Bindings;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Test for {@link SWRLBContains}.
 */
public class SWRLBContainsTest extends AbstractSWRLBuiltinTest {

    @Test
    public void testSPARQLFilterExpression() throws Exception {
        Variable x = createVariable();

        SWRLBContains builtin = instantiate(SWRLB.CONTAINS, x, "Hello");
        assertEquals("contains(?" + x.getVariableName() + ", \"hello\")", builtin.asSPARQLFilterExpression().toLowerCase());
    }

    @Test
    public void testEvaluate() throws Exception {
        assertTrue(instantiate(SWRLB.CONTAINS, "hello", "llo").evaluate(new Bindings()));
        assertTrue(instantiate(SWRLB.CONTAINS, "hello", "ll").evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.CONTAINS, "hello", "LL").evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.CONTAINS, "hello", new Date()).evaluate(new Bindings()));
    }
}