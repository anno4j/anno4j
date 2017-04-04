package com.github.anno4j.schema_parsing.building;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.RDFS;
import com.github.anno4j.schema_parsing.model.ExtendedRDFSClazz;
import com.github.anno4j.schema_parsing.model.ExtendedRDFSProperty;
import com.github.anno4j.schema_parsing.model.ExtendedRDFSPropertySupport;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;

import javax.lang.model.element.Modifier;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test for the building capabilities of {@link ExtendedRDFSProperty}/{@link ExtendedRDFSPropertySupport}
 * for generating JavaPoet {@link MethodSpec}.
 */
public class PropertySpecTest {

    private static RDFSModelBuilder modelBuilder;

    private static OntGenerationConfig generationConfig;

    /**
     * Returns a {@link ExtendedRDFSProperty} instance from {@link #modelBuilder}
     * with the specified URI.
     * @param uri The URI to get the property object for.
     * @return The property object or null if no property with the given URI is in the model.
     */
    private static ExtendedRDFSProperty getPropertyFromModel(String uri) {
        for(ExtendedRDFSProperty property : modelBuilder.getProperties()) {
            if(property.getResourceAsString().equals(uri)) {
                return property;
            }
        }
        return null;
    }

    @Before
    public void setUp() throws Exception {
        generationConfig = new OntGenerationConfig();
        List<String> javaDocLangPreference = Arrays.asList("de", "en", OntGenerationConfig.UNTYPED_LITERAL);
        List<String> identifierLangPreference = Arrays.asList("en", "de", OntGenerationConfig.UNTYPED_LITERAL);
        generationConfig.setIdentifierLanguagePreference(identifierLangPreference);
        generationConfig.setJavaDocLanguagePreference(javaDocLangPreference);

        // Create a RDFS model builder instance:
        VehicleOntologyLoader ontologyLoader = new VehicleOntologyLoader();
        modelBuilder = ontologyLoader.getVehicleOntologyModelBuilder();

        // Build the ontology model:
        modelBuilder.build();
    }

    @Test
    public void testGetter() throws Exception {
        ExtendedRDFSProperty loadCap = getPropertyFromModel("http://example.de/ont#load_capacity");
        assertNotNull(loadCap);

        MethodSpec loadCapSpec = loadCap.buildGetter(generationConfig);

        // Test signature:
        assertEquals("getMaximumLoadCapacities", loadCapSpec.name);
        assertTrue(loadCapSpec.modifiers.contains(Modifier.PUBLIC));
        ClassName setClass = ClassName.get("java.util", "Set");
        assertEquals(ParameterizedTypeName.get(setClass, ClassName.get(Float.class)), loadCapSpec.returnType);

        // Test annotation:
        assertEquals(0, loadCapSpec.annotations.size()); // @Iri annotation was moved to private field. Setters must not have an annotation.
    }

    @Test
    public void testSetter() throws Exception {
        ExtendedRDFSProperty loadCap = getPropertyFromModel("http://example.de/ont#load_capacity");
        assertNotNull(loadCap);

        MethodSpec loadCapSpec = loadCap.buildSetter(generationConfig);

        // Test signature:
        assertEquals("setMaximumLoadCapacities", loadCapSpec.name);
        assertTrue(loadCapSpec.modifiers.contains(Modifier.PUBLIC));
        assertEquals(1, loadCapSpec.parameters.size());
        ClassName setClass = ClassName.get("java.util", "Set");
        assertEquals(ParameterizedTypeName.get(setClass, ClassName.get(Float.class)), loadCapSpec.parameters.get(0).type);

        // Test JavaDoc:
        assertNotNull(loadCapSpec.javadoc);
        assertTrue(loadCapSpec.javadoc.toString().startsWith("Ladung in Tonnen"));

        // Test annotation:
        assertEquals(0, loadCapSpec.annotations.size()); // @Iri annotation was moved to private field. Setters must not have an annotation.
    }

    @Test
    public void testAdder() throws Exception {
        ExtendedRDFSProperty loadCap = getPropertyFromModel("http://example.de/ont#load_capacity");
        assertNotNull(loadCap);

        MethodSpec loadCapSpec = loadCap.buildAdder(generationConfig);

        // Test signature:
        assertEquals("addMaximumLoadCapacity", loadCapSpec.name);
        assertTrue(loadCapSpec.modifiers.contains(Modifier.PUBLIC));
        assertEquals(1, loadCapSpec.parameters.size());
        assertEquals(ClassName.get(Float.class), loadCapSpec.parameters.get(0).type);

        // Test JavaDoc:
        assertNotNull(loadCapSpec.javadoc);
        assertTrue(loadCapSpec.javadoc.toString().startsWith("Ladung in Tonnen"));

        // Test annotation:
        assertEquals(0, loadCapSpec.annotations.size());
    }

    @Test
    public void testAdderAll() throws Exception {
        ExtendedRDFSProperty loadCap = getPropertyFromModel("http://example.de/ont#load_capacity");
        assertNotNull(loadCap);

        MethodSpec loadCapSpec = loadCap.buildAdderAll(generationConfig);

        // Test signature:
        assertEquals("addAllMaximumLoadCapacities", loadCapSpec.name);
        assertTrue(loadCapSpec.modifiers.contains(Modifier.PUBLIC));
        assertEquals(1, loadCapSpec.parameters.size());
        ClassName setClass = ClassName.get("java.util", "Set");
        assertEquals(ParameterizedTypeName.get(setClass, ClassName.get(Float.class)), loadCapSpec.parameters.get(0).type);

        // Test JavaDoc:
        assertNotNull(loadCapSpec.javadoc);
        assertTrue(loadCapSpec.javadoc.toString().startsWith("Ladung in Tonnen"));

        // Test annotation:
        assertEquals(0, loadCapSpec.annotations.size());
    }

    @Test
    public void testRemover() throws Exception {
        ExtendedRDFSProperty loadCap = getPropertyFromModel("http://example.de/ont#load_capacity");
        assertNotNull(loadCap);

        MethodSpec loadCapSpec = loadCap.buildRemover(generationConfig);

        // Test signature:
        assertEquals("removeMaximumLoadCapacity", loadCapSpec.name);
        assertTrue(loadCapSpec.modifiers.contains(Modifier.PUBLIC));
        assertEquals(1, loadCapSpec.parameters.size());
        assertEquals(ClassName.get(Float.class), loadCapSpec.parameters.get(0).type);

        // Test JavaDoc:
        assertNotNull(loadCapSpec.javadoc);
        assertTrue(loadCapSpec.javadoc.toString().startsWith("Ladung in Tonnen"));

        // Test annotation:
        assertEquals(0, loadCapSpec.annotations.size());
    }

    @Test
    public void testRemoverAll() throws Exception {
        ExtendedRDFSProperty loadCap = getPropertyFromModel("http://example.de/ont#load_capacity");
        assertNotNull(loadCap);

        MethodSpec loadCapSpec = loadCap.buildRemoverAll(generationConfig);

        // Test signature:
        assertEquals("removeAllMaximumLoadCapacities", loadCapSpec.name);
        assertTrue(loadCapSpec.modifiers.contains(Modifier.PUBLIC));
        assertEquals(1, loadCapSpec.parameters.size());
        ClassName setClass = ClassName.get("java.util", "Set");
        assertEquals(ParameterizedTypeName.get(setClass, ClassName.get(Float.class)), loadCapSpec.parameters.get(0).type);

        // Test JavaDoc:
        assertNotNull(loadCapSpec.javadoc);
        assertTrue(loadCapSpec.javadoc.toString().startsWith("Ladung in Tonnen"));

        // Test annotation:
        assertEquals(0, loadCapSpec.annotations.size());
    }

    @Test
    public void testUnspecifiedDomain() throws Exception {
        Anno4j anno4j = new Anno4j();
        ExtendedRDFSProperty property = anno4j.createObject(ExtendedRDFSProperty.class);

        ExtendedRDFSClazz rdfsClazz = anno4j.createObject(ExtendedRDFSClazz.class, (Resource) new URIImpl(RDFS.CLAZZ));
        property.addRangeClazz(rdfsClazz);

        ClassName range = property.getRangeJavaPoetClassName(generationConfig);
        assertEquals(ClassName.get(ResourceObject.class), range);
    }
}