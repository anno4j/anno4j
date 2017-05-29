package com.github.anno4j.schema_parsing.building;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.namespaces.OWL;
import com.github.anno4j.model.namespaces.RDFS;
import com.github.anno4j.schema.model.owl.OWLClazz;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.github.anno4j.schema_parsing.model.BuildableRDFSClazz;
import com.github.anno4j.schema_parsing.model.BuildableRDFSProperty;
import com.github.anno4j.schema_parsing.model.BuildableRDFSPropertySupport;
import com.google.common.collect.Sets;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryException;

import javax.lang.model.element.Modifier;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test for the building capabilities of {@link BuildableRDFSProperty}/{@link BuildableRDFSPropertySupport}
 * for generating JavaPoet {@link MethodSpec}.
 */
public class RDFSPropertySpecTest {

    private static OntologyModelBuilder modelBuilder;

    private static OntGenerationConfig generationConfig;

    private OWLClazz declaringClass;

    /**
     * Returns a {@link BuildableRDFSProperty} instance from {@link #modelBuilder}
     * with the specified URI.
     * @param uri The URI to get the property object for.
     * @return The property object or null if no property with the given URI is in the model.
     */
    private static BuildableRDFSProperty getPropertyFromModel(String uri) throws RepositoryException {
        for(BuildableRDFSProperty property : modelBuilder.getProperties()) {
            if(property.getResourceAsString().equals(uri)) {
                return property;
            }
        }
        return null;
    }

    @Before
    public void setUp() throws Exception {
        Anno4j anno4j = new Anno4j();

        generationConfig = new OntGenerationConfig();
        List<String> javaDocLangPreference = Arrays.asList("de", "en", OntGenerationConfig.UNTYPED_LITERAL);
        List<String> identifierLangPreference = Arrays.asList("en", "de", OntGenerationConfig.UNTYPED_LITERAL);
        generationConfig.setIdentifierLanguagePreference(identifierLangPreference);
        generationConfig.setJavaDocLanguagePreference(javaDocLangPreference);

        // Create a model builder instance:
        modelBuilder = new OWLJavaFileGenerator(anno4j);
        VehicleOntologyLoader.addVehicleOntology(modelBuilder);

        // Build the ontology model:
        modelBuilder.build();

        declaringClass = anno4j.createObject(OWLClazz.class, (Resource) new URIImpl("http://example.de/resource"));
    }

    @Test
    public void testGetter() throws Exception {
        BuildableRDFSProperty loadCap = getPropertyFromModel("http://example.de/ont#load_capacity");
        assertNotNull(loadCap);

        MethodSpec loadCapSpec = loadCap.buildGetter(declaringClass, generationConfig);

        // Test annotations empty:
        assertEquals(0, loadCapSpec.annotations.size());

        // Test signature:
        assertEquals("getMaximumLoadCapacities", loadCapSpec.name);
        assertTrue(loadCapSpec.modifiers.contains(Modifier.PUBLIC));
        ClassName setClass = ClassName.get("java.util", "Set");
        assertEquals(ParameterizedTypeName.get(setClass, ClassName.get(Float.class)), loadCapSpec.returnType);
    }

    @Test
    public void testSetter() throws Exception {
        BuildableRDFSProperty loadCap = getPropertyFromModel("http://example.de/ont#load_capacity");
        assertNotNull(loadCap);

        MethodSpec loadCapSpec = loadCap.buildSetter(declaringClass, generationConfig);

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
        BuildableRDFSProperty loadCap = getPropertyFromModel("http://example.de/ont#load_capacity");
        assertNotNull(loadCap);

        MethodSpec loadCapSpec = loadCap.buildAdder(declaringClass, generationConfig);

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
        BuildableRDFSProperty loadCap = getPropertyFromModel("http://example.de/ont#load_capacity");
        assertNotNull(loadCap);

        MethodSpec loadCapSpec = loadCap.buildAdderAll(declaringClass, generationConfig);

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
        BuildableRDFSProperty loadCap = getPropertyFromModel("http://example.de/ont#load_capacity");
        assertNotNull(loadCap);

        MethodSpec loadCapSpec = loadCap.buildRemover(declaringClass, generationConfig);

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
        BuildableRDFSProperty loadCap = getPropertyFromModel("http://example.de/ont#load_capacity");
        assertNotNull(loadCap);

        MethodSpec loadCapSpec = loadCap.buildRemoverAll(declaringClass, generationConfig);

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
        BuildableRDFSProperty property = anno4j.createObject(BuildableRDFSProperty.class);

        BuildableRDFSClazz rdfsClazz = anno4j.createObject(BuildableRDFSClazz.class, (Resource) new URIImpl(RDFS.CLAZZ));
        BuildableRDFSClazz owlThing = anno4j.createObject(BuildableRDFSClazz.class, (Resource) new URIImpl(OWL.THING));
        property.setRanges(Sets.<RDFSClazz>newHashSet(rdfsClazz));

        ClassName range = property.getRangeJavaPoetClassName(generationConfig);
        assertEquals(ClassName.get(ResourceObject.class), range);

        property.setRanges(Sets.<RDFSClazz>newHashSet(owlThing));
        range = property.getRangeJavaPoetClassName(generationConfig);
        assertEquals(ClassName.get(ResourceObject.class), range);
    }
}