package com.github.anno4j.rdfs_parser.building;

import com.github.anno4j.Anno4j;
import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObjectSupport;
import com.github.anno4j.rdfs_parser.building.support.SupportTypeSpecSupport;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSClazz;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSProperty;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Test for {@link SupportTypeSpecSupport}.
 */
public class SupportTypeSpecTest {

    private static ExtendedRDFSClazz clazz;

    private OntGenerationConfig generationConfig;

    @Before
    public void setUp() throws Exception {
        Anno4j anno4j = new Anno4j();

        clazz = anno4j.createObject(ExtendedRDFSClazz.class);
        clazz.addLabel("MyClazz");

        Set<ExtendedRDFSProperty> props = new HashSet<>();
        ExtendedRDFSProperty foo = anno4j.createObject(ExtendedRDFSProperty.class);
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
    }

}