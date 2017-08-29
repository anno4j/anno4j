package com.github.anno4j.schema_parsing.building;

import com.github.anno4j.Anno4j;
import com.github.anno4j.schema_parsing.model.BuildableRDFSClazz;
import com.github.anno4j.schema_parsing.model.BuildableRDFSProperty;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Tests the generation of methods from OWL schema.
 */
public class OWLPropertySpecTest {

    /**
     * The Anno4j instance that will receive the schema information.
     */
    private Anno4j anno4j;

    /**
     * Defines how Java code is generated from the OWL ontology.
     */
    private OntGenerationConfig generationConfig;

    @Before
    public void setUp() throws Exception {
        // Define the generation configuration:
        generationConfig = new OntGenerationConfig();
        List<String> javaDocLangPreference = Arrays.asList("de", "en", OntGenerationConfig.UNTYPED_LITERAL);
        List<String> identifierLangPreference = Arrays.asList("en", "de", OntGenerationConfig.UNTYPED_LITERAL);
        generationConfig.setIdentifierLanguagePreference(identifierLangPreference);
        generationConfig.setJavaDocLanguagePreference(javaDocLangPreference);

        // Create the model builder and feed it with the restaurant OWL ontology:
        anno4j = new Anno4j();
        OWLJavaFileGenerator modelBuilder = new OWLJavaFileGenerator(anno4j);
        ClassLoader classLoader = getClass().getClassLoader();
        URL ontUrl = classLoader.getResource("restaurant_owl.ttl");
        File ontologyFile = new File(ontUrl.getFile());
        modelBuilder.addRDF(new FileInputStream(ontologyFile), "http://example.de/ont#", "TURTLE");

        // Transfer the extensive model to the anno4j object:
        modelBuilder.build();
    }

    @Test
    public void testGetter() throws Exception {
        // Get the properties as a buildable and the class for which to build:
        BuildableRDFSClazz restaurant = anno4j.findByID(BuildableRDFSClazz.class, "http://example.de/ont#Restaurant");
        BuildableRDFSClazz dish = anno4j.findByID(BuildableRDFSClazz.class, "http://example.de/ont#Dish");
        BuildableRDFSProperty nameProperty = anno4j.findByID(BuildableRDFSProperty.class, "http://example.de/ont#name");
        BuildableRDFSProperty servesProperty = anno4j.findByID(BuildableRDFSProperty.class, "http://example.de/ont#serves");

        // Build the getter method signature:
        MethodSpec nameGetter = nameProperty.buildGetter(restaurant, generationConfig);

        assertEquals(3, nameGetter.annotations.size());
        assertEquals(0, nameGetter.parameters.size());
        assertEquals("getHasName", nameGetter.name);

        // The cardinality of the property is one, so there should be a single value return type:
        assertEquals(ClassName.get(CharSequence.class), nameGetter.returnType);

        // The same for the ex:serves property:
        MethodSpec servesGetter = servesProperty.buildGetter(restaurant, generationConfig);

        assertEquals(2, servesGetter.annotations.size());
        assertEquals(0, servesGetter.parameters.size());
        assertEquals("getServes", servesGetter.name);
        ClassName setType = ClassName.get(Set.class);
        assertEquals(ParameterizedTypeName.get(setType, dish.getJavaPoetClassName(generationConfig)), servesGetter.returnType);
    }

    @Test
    public void testSetter() throws Exception {
        // Get the properties as a buildable and the class for which to build:
        BuildableRDFSClazz restaurant = anno4j.findByID(BuildableRDFSClazz.class, "http://example.de/ont#Restaurant");
        BuildableRDFSClazz dish = anno4j.findByID(BuildableRDFSClazz.class, "http://example.de/ont#Dish");
        BuildableRDFSProperty nameProperty = anno4j.findByID(BuildableRDFSProperty.class, "http://example.de/ont#name");

        // Test normal setter:
        MethodSpec nameSetter = nameProperty.buildSetter(restaurant, generationConfig);
        assertEquals(1, nameSetter.annotations.size());
        assertEquals(1, nameSetter.parameters.size());
        assertEquals(ClassName.get(CharSequence.class), nameSetter.parameters.get(0).type); // Cardinality is one, so single value parameter
        assertFalse(nameSetter.varargs);
        assertEquals("setHasName", nameSetter.name); // Cardinality is one, so name is singular

        // Test vararg setter:
        MethodSpec nameSetterVarArg = nameProperty.buildVarArgSetter(restaurant, generationConfig);
        assertEquals(0, nameSetterVarArg.annotations.size());
        assertEquals(1, nameSetterVarArg.parameters.size());
        assertEquals(ArrayTypeName.get(CharSequence[].class), nameSetterVarArg.parameters.get(0).type);
        assertTrue(nameSetterVarArg.varargs);
        assertEquals("setHasName", nameSetterVarArg.name); // Cardinality is one, so name is singular

        BuildableRDFSProperty servesProperty = anno4j.findByID(BuildableRDFSProperty.class, "http://example.de/ont#serves");

        // Test normal setter:
        MethodSpec servesSetter = servesProperty.buildSetter(restaurant, generationConfig);
        assertEquals(1, servesSetter.annotations.size());
        assertEquals(1, servesSetter.parameters.size());
        assertEquals(ParameterizedTypeName.get(ClassName.get(Set.class),dish.getJavaPoetClassName(generationConfig)), servesSetter.parameters.get(0).type);
        assertFalse(servesSetter.varargs);
        assertEquals("setServes", servesSetter.name);

        // Test vararg setter:
        MethodSpec servesSetterVarArg = servesProperty.buildVarArgSetter(restaurant, generationConfig);
        assertEquals(0, servesSetterVarArg.annotations.size());
        assertEquals(1, servesSetterVarArg.parameters.size());
        assertEquals(ArrayTypeName.of(dish.getJavaPoetClassName(generationConfig)), servesSetterVarArg.parameters.get(0).type);
        assertTrue(servesSetterVarArg.varargs);
        assertEquals("setServes", servesSetterVarArg.name);
    }
}
