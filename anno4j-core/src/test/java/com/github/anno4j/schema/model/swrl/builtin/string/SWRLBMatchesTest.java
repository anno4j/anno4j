package com.github.anno4j.schema.model.swrl.builtin.string;

import com.github.anno4j.model.namespaces.SWRLB;
import com.github.anno4j.schema.model.swrl.Variable;
import com.github.anno4j.schema.model.swrl.builtin.AbstractSWRLBuiltinTest;
import com.github.anno4j.schema.model.swrl.engine.Bindings;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Test for {@link SWRLBMatches}.
 */
public class SWRLBMatchesTest extends AbstractSWRLBuiltinTest {

    @Test
    public void testSPARQLFilterExpression() throws Exception {
        Variable x = createVariable();

        SWRLBMatches builtin = instantiate(SWRLB.MATCHES, x, "Hello");
        assertEquals("regex(?" + x.getVariableName() + ", \"hello\")", builtin.asSPARQLFilterExpression().toLowerCase());
    }

    @Test
    public void testEvaluate() throws Exception {
        assertTrue(instantiate(SWRLB.MATCHES, "hello", "he(l+)o").evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.MATCHES, "hello", "he(l?)o").evaluate(new Bindings()));
        assertFalse(instantiate(SWRLB.MATCHES, "hello", "he(l?o").evaluate(new Bindings())); // Invalid Regex sytax
        assertFalse(instantiate(SWRLB.MATCHES, "hello", new Date()).evaluate(new Bindings()));
    }
}