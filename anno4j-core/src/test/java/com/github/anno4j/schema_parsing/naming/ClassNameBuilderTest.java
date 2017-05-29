package com.github.anno4j.schema_parsing.naming;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link ClassNameBuilder}.
 */
public class ClassNameBuilderTest {
    @Test
    public void className() throws Exception {
        ClassName cn = ClassNameBuilder.builder("http://example.com/ont#Person")
                .className();
        assertEquals("com.example", cn.packageName());
        assertEquals("Person", cn.simpleName());

        cn = ClassNameBuilder.builder("http://example.com/person")
                .className();
        assertEquals("com.example", cn.packageName());
        assertEquals("Person", cn.simpleName());
    }

    @Test
    public void interfaceSpec() throws Exception {
        TypeSpec typeSpec = ClassNameBuilder.builder("http://example.com/ont#Person")
                .interfaceSpec();
        assertEquals("Person", typeSpec.name);

        typeSpec = ClassNameBuilder.builder("http://example.com/person")
                .interfaceSpec();
        assertEquals("Person", typeSpec.name);
    }

    @Test
    public void testBlankNode() throws Exception {
        ClassNameBuilder builder = ClassNameBuilder.builder("someblanknode");

        assertEquals("Someblanknode", builder.className().simpleName());

        ClassName cn = builder.withIncomingProperty("http://example.com/has_ingredient")
                              .className();
        assertEquals("HasIngredientTarget", cn.simpleName());

        cn = builder.withOutgoingProperty("http://example.com/ingredient")
                      .withOutgoingProperty("http://example.com/amount")
                      .className();

        assertTrue(cn.simpleName().equals("IngredientAmountNode")
                || cn.simpleName().equals("AmountIngredientNode"));
    }
}