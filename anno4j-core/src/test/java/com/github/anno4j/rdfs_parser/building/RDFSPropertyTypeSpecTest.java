package com.github.anno4j.rdfs_parser.building;

import com.github.anno4j.rdfs_parser.building.OntGenerationConfig;
import com.github.anno4j.rdfs_parser.building.RDFSModelBuilder;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSClazz;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSProperty;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSPropertySupport;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import org.junit.Before;
import org.junit.Test;

import javax.lang.model.element.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Test for the building capabilities of {@link ExtendedRDFSProperty}/{@link ExtendedRDFSPropertySupport}
 * for generating JavaPoet {@link MethodSpec}.
 */
public class RDFSPropertyTypeSpecTest {

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
        assertEquals(1, loadCapSpec.annotations.size());
        AnnotationSpec annotation = loadCapSpec.annotations.iterator().next();
        assertEquals("org.openrdf.annotations.Iri", annotation.type.toString());
        assertEquals(1, annotation.members.size());
        assertEquals("\"http://example.de/ont#load_capacity\"", annotation.members.get("value").get(0).toString());
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
        assertEquals(1, loadCapSpec.annotations.size());
        AnnotationSpec annotation = loadCapSpec.annotations.iterator().next();
        assertEquals("org.openrdf.annotations.Iri", annotation.type.toString());
        assertEquals(1, annotation.members.size());
        assertEquals("\"http://example.de/ont#load_capacity\"", annotation.members.get("value").get(0).toString());
    }
}