package com.github.anno4j.schema_parsing.naming;

import com.github.anno4j.Anno4j;
import com.github.anno4j.schema.model.rdfs.RDFSProperty;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.object.LangString;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link MethodNameBuilder}.
 */
public class MethodNameBuilderTest {

    private Anno4j anno4j;

    private OntGenerationConfig config;

    @Before
    public void setUp() throws Exception {
        anno4j = new Anno4j();
        config = new OntGenerationConfig();
        config.setIdentifierLanguagePreference(new String[]{"de", OntGenerationConfig.UNTYPED_LITERAL});
    }

    @Test
    public void testJavaPoetMethodSpec() throws Exception {
        MethodNameBuilder builder = MethodNameBuilder.forObjectRepository(anno4j.getObjectRepository());

        RDFSProperty age = anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl("http://example.org/#age"));

        // Test singular:
        assertEquals("getAge", builder.getJavaPoetMethodSpec("get", age, config, false).name);

        // Test plural:
        assertEquals("getAges", builder.getJavaPoetMethodSpec("get", age, config, true).name);

        // Test building with RDFS label:
        age.setLabels(Sets.<CharSequence>newHashSet(new LangString("Alter", "de")));
        assertEquals("getAlter", builder.getJavaPoetMethodSpec("get", age, config, false).name);

        // Test resource for which no name can be extracted:
        boolean exceptionThrown = false;
        try {
            RDFSProperty noName = anno4j.createObject(RDFSProperty.class, (Resource) new URIImpl("http://example.org/#"));
            MethodNameBuilder.forObjectRepository(anno4j.getObjectRepository())
                    .getJavaPoetMethodSpec("foo", noName, config, false);
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }
}