package com.github.anno4j.schema_parsing.building;

import com.github.anno4j.Anno4j;
import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObjectSupport;
import com.github.anno4j.schema_parsing.building.support.SupportTypeSpecSupport;
import com.github.anno4j.schema_parsing.model.ExtendedRDFSClazz;
import com.github.anno4j.schema_parsing.model.ExtendedRDFSProperty;
import com.squareup.javapoet.*;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.annotations.Iri;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link SupportTypeSpecSupport}.
 */
public class SupportTypeSpecTest extends TypeSpecTest {

    private static ExtendedRDFSClazz clazz;

    private OntGenerationConfig generationConfig;

    @Before
    public void setUp() throws Exception {
        Anno4j anno4j = new Anno4j();

        clazz = anno4j.createObject(ExtendedRDFSClazz.class);
        clazz.addLabel("MyClazz");

        Set<ExtendedRDFSProperty> props = new HashSet<>();
        ExtendedRDFSProperty foo = anno4j.createObject(ExtendedRDFSProperty.class, (Resource) new URIImpl("http://example.org/#foo"));
        foo.addLabel("foo");
        foo.addRangeClazz(clazz);
        props.add(foo);

        clazz.setOutgoingProperties(props);

        generationConfig = new OntGenerationConfig();
        List<String> identifierLangPreference = Arrays.asList(OntGenerationConfig.UNTYPED_LITERAL);
        List<String> javaDocLangPreference = Arrays.asList(OntGenerationConfig.UNTYPED_LITERAL);
        generationConfig.setIdentifierLanguagePreference(identifierLangPreference);
        generationConfig.setJavaDocLanguagePreference(javaDocLangPreference);
    }

    @Test
    public void buildSupportTypeSpec() throws Exception {
        TypeSpec typeSpec = clazz.buildSupportTypeSpec(generationConfig);

        // Test @Partial annotation:
        AnnotationSpec partialAnnotation = AnnotationSpec.builder(Partial.class).build();
        assertEquals(1, typeSpec.annotations.size());
        assertEquals(partialAnnotation, typeSpec.annotations.get(0));

        // Test superclass:
        TypeName resourceObjectSupport = ClassName.get(ResourceObjectSupport.class);
        assertEquals(resourceObjectSupport, typeSpec.superclass);

        // Test superinterface:
        ClassName superInterface = clazz.getJavaPoetClassName(generationConfig);
        assertEquals(1, typeSpec.superinterfaces.size());
        assertEquals(superInterface, typeSpec.superinterfaces.get(0));

        // Test methods:
        Set<String> methodNames = getMethodNames(typeSpec);
        assertEquals(6, methodNames.size());
        assertTrue(methodNames.contains("getFoos"));
        assertTrue(methodNames.contains("setFoos"));
        assertTrue(methodNames.contains("addFoo"));
        assertTrue(methodNames.contains("addAllFoos"));
        assertTrue(methodNames.contains("removeFoo"));
        assertTrue(methodNames.contains("removeAllFoos"));

        // Test fields:
        Set<String> fieldNames = getFieldNames(typeSpec);
        assertEquals(1, fieldNames.size());
        assertTrue(fieldNames.contains("foos"));

        AnnotationSpec iriAnnotation = AnnotationSpec.builder(Iri.class)
                                                    .addMember("value", "$S", "http://example.org/#foo")
                                                    .build();
        FieldSpec fooField = typeSpec.fieldSpecs.get(0);
        assertTrue(fooField.annotations.contains(iriAnnotation));
    }

}