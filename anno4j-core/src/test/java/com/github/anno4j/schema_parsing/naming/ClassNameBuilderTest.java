package com.github.anno4j.schema_parsing.naming;

import com.github.anno4j.Anno4j;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;

import static org.junit.Assert.assertEquals;

/**
 * Test for {@link ClassNameBuilder}.
 */
public class ClassNameBuilderTest {

    private Anno4j anno4j;

    private OntGenerationConfig config;

    private RDFSClazz person;

    @Before
    public void setUp() throws Exception {
        anno4j = new Anno4j();
        config = new OntGenerationConfig();

        person = anno4j.createObject(RDFSClazz.class, (Resource) new URIImpl("http://example.com/my_person"));
    }

    @Test
    public void className() throws Exception {
        ClassName cn = ClassNameBuilder.forObjectRepository(anno4j.getObjectRepository())
                .className(person, config);
        assertEquals("com.example", cn.packageName());
        assertEquals("MyPerson", cn.simpleName());
    }

    @Test
    public void interfaceSpec() throws Exception {
        TypeSpec typeSpec = ClassNameBuilder.forObjectRepository(anno4j.getObjectRepository())
                .interfaceSpec(person, config);
        assertEquals("MyPerson", typeSpec.name);
    }
}