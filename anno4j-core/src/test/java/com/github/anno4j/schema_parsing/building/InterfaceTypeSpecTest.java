package com.github.anno4j.schema_parsing.building;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.namespaces.XSD;
import com.github.anno4j.schema_parsing.model.BuildableRDFSClazz;
import com.github.anno4j.schema_parsing.model.BuildableRDFSClazzSupport;
import com.squareup.javapoet.*;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryException;

import javax.lang.model.element.Modifier;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Test for the building capabilities of {@link BuildableRDFSClazz}/{@link BuildableRDFSClazzSupport}
 * for generating JavaPoet {@link TypeSpec}.
 */
public class InterfaceTypeSpecTest extends TypeSpecTest {

    /**
     * The configuration object used for building the TypeSpec.
     */
    private static OntGenerationConfig generationConfig;

    private static OWLJavaFileGenerator modelBuilder;

    /**
     * Returns a {@link BuildableRDFSClazz} instance from {@link #modelBuilder}
     * with the specified URI.
     * @param uri The URI to get the class object for.
     * @return The class object or null if no class with the given URI is in the model.
     */
    private static BuildableRDFSClazz getClazzFromModel(String uri) throws RepositoryException {
        Collection<BuildableRDFSClazz> clazzes = modelBuilder.getClazzes();
        for (BuildableRDFSClazz clazz : clazzes) {
            if(clazz.getResourceAsString().equals(uri)) {
                return clazz;
            }
        }
        return null;
    }

    @Before
    public void setUp() throws Exception {
        generationConfig = new OntGenerationConfig();
        List<String> identifierLangPreference = Arrays.asList("en", "de", OntGenerationConfig.UNTYPED_LITERAL);
        List<String> javaDocLangPreference = Arrays.asList("de", "en", OntGenerationConfig.UNTYPED_LITERAL);
        generationConfig.setIdentifierLanguagePreference(identifierLangPreference);
        generationConfig.setJavaDocLanguagePreference(javaDocLangPreference);

        modelBuilder = new OWLJavaFileGenerator();

        // Create a RDFS model builder instance:
        VehicleOntologyLoader.addVehicleOntology(modelBuilder);

        // Build the ontology model:
        modelBuilder.build();
    }

    @Test
    public void testBaseClass() throws Exception {
        BuildableRDFSClazz vehicle = getClazzFromModel("http://example.de/ont#Vehicle");
        assertNotNull(vehicle);

        TypeSpec vehicleSpec = vehicle.buildTypeSpec(generationConfig);

        // @Iri annotation:
        assertEquals(1, vehicleSpec.annotations.size());
        AnnotationSpec annotation = vehicleSpec.annotations.iterator().next();
        assertEquals("org.openrdf.annotations.Iri", annotation.type.toString());
        assertEquals(1, annotation.members.size());
        assertEquals("\"http://example.de/ont#Vehicle\"", annotation.members.get("value").get(0).toString());

        // Resource objects are public interfaces:
        assertTrue(vehicleSpec.modifiers.contains(Modifier.PUBLIC));
        assertEquals(TypeSpec.Kind.INTERFACE, vehicleSpec.kind);

        // Name should be english:
        assertEquals("Vehicle", vehicleSpec.name);

        // JavaDoc should be english (german is missing):
        assertNotNull(vehicleSpec.javadoc);
        assertTrue(vehicleSpec.javadoc.toString().startsWith("A mobile machine that"));

        // Vehicles have the seat_num, name and official_name properties
        // and thus a getter, setter, setter with vararg, adder, addAll, remover and removerAll for it:
        assertEquals(3*7, vehicleSpec.methodSpecs.size());
        Set<String> methodNames = getMethodNames(vehicleSpec);
        assertTrue(methodNames.contains("getNumberOfSeats"));
        assertTrue(methodNames.contains("setNumberOfSeats"));
        assertTrue(methodNames.contains("addNumberOfSeats"));
        assertTrue(methodNames.contains("addAllNumberOfSeats"));
        assertTrue(methodNames.contains("removeNumberOfSeats"));

        // Vehicle is the topmost class in the vehicle ontology inheritance tree.
        // Its superinterface must be ResourceObject:
        assertEquals(ClassName.get(Object.class), vehicleSpec.superclass);
        assertEquals(1, vehicleSpec.superinterfaces.size());
        TypeName superInterface = vehicleSpec.superinterfaces.get(0);
        assertEquals("com.github.anno4j.model.impl.ResourceObject", superInterface.toString());
    }

    @Test
    public void testSubClass() throws Exception {
        BuildableRDFSClazz truck = getClazzFromModel("http://example.de/ont#Truck");
        assertNotNull(truck);

        TypeSpec truckSpec = truck.buildTypeSpec(generationConfig);

        // @Iri annotation:
        assertEquals(1, truckSpec.annotations.size());
        AnnotationSpec annotation = truckSpec.annotations.iterator().next();
        assertEquals("org.openrdf.annotations.Iri", annotation.type.toString());
        assertEquals(1, annotation.members.size());
        assertEquals("\"http://example.de/ont#Truck\"", annotation.members.get("value").get(0).toString());

        // Resource objects are public interfaces:
        assertTrue(truckSpec.modifiers.contains(Modifier.PUBLIC));
        assertEquals(TypeSpec.Kind.INTERFACE, truckSpec.kind);

        // Name should be english:
        assertEquals("Truck", truckSpec.name);

        // JavaDoc should be german:
        assertNotNull(truckSpec.javadoc);
        assertTrue(truckSpec.javadoc.toString().startsWith("Ein LKW ist ein"));

        // Trucks have the load_capacity property and thus a getter, setter, setter with vararg, adder, addAll, remover, removerAll for it:
        assertEquals(7, truckSpec.methodSpecs.size());
        Set<String> methodNames = getMethodNames(truckSpec);
        assertTrue(methodNames.contains("getMaximumLoadCapacities"));
        assertTrue(methodNames.contains("setMaximumLoadCapacities"));

        // The single superclass of a truck is Vehicle:
        assertEquals(ClassName.get(Object.class), truckSpec.superclass);
        assertEquals(1, truckSpec.superinterfaces.size());
        TypeName superInterface = truckSpec.superinterfaces.get(0);
        assertEquals("de.example.Vehicle", superInterface.toString());
    }

    @Test
    public void testMultipleInheritance() throws Exception {
        BuildableRDFSClazz camper = getClazzFromModel("http://example.de/ont#Camper");
        assertNotNull(camper);

        TypeSpec camperSpec = camper.buildTypeSpec(generationConfig);

        // @Iri annotation:
        assertEquals(1, camperSpec.annotations.size());
        AnnotationSpec annotation = camperSpec.annotations.iterator().next();
        assertEquals("org.openrdf.annotations.Iri", annotation.type.toString());
        assertEquals(1, annotation.members.size());
        assertEquals("\"http://example.de/ont#Camper\"", annotation.members.get("value").get(0).toString());

        // Resource objects are public interfaces:
        assertTrue(camperSpec.modifiers.contains(Modifier.PUBLIC));
        assertEquals(TypeSpec.Kind.INTERFACE, camperSpec.kind);

        // Name should be generated from URI:
        assertEquals("Camper", camperSpec.name);

        // JavaDoc should be non existing (only the generation note):
        assertEquals("Generated class for http://example.de/ont#Camper", camperSpec.javadoc.toString());

        // Campers have no properties on their own:
        assertEquals(0, camperSpec.methodSpecs.size());

        // Camper has two super interfaces:
        assertEquals(ClassName.get(Object.class), camperSpec.superclass);
        assertEquals(2, camperSpec.superinterfaces.size());
        Set<String> superInterfaces = getSuperinterfaceNames(camperSpec);
        assertTrue(superInterfaces.contains("de.example.Home"));
        assertTrue(superInterfaces.contains("de.example.Vehicle"));
    }

    /**
     * Returns the JavaPoet class name for a RDFS class using
     * {@link BuildableRDFSClazz#getJavaPoetClassName(OntGenerationConfig)}.
     * @param anno4j The Anno4j instance used for creating the resource object.
     * @param clazzUri The URI of the class for which a JavaPoet class name should be created.
     * @param config The configuration object to be used for generating the class name.
     * @return The class name for the RDFS class given.
     * @throws RepositoryException Thrown on error during instantiating the resource object with Anno4j.
     * @throws IllegalAccessException Thrown on error during instantiating the resource object with Anno4j.
     * @throws InstantiationException Thrown on error during instantiating the resource object with Anno4j.
     */
    private static ClassName getJavaPoetClassName(Anno4j anno4j, String clazzUri, OntGenerationConfig config) throws RepositoryException, IllegalAccessException, InstantiationException {
        BuildableRDFSClazz clazz = anno4j.createObject(BuildableRDFSClazz.class, (Resource) new URIImpl(clazzUri));
        return clazz.getJavaPoetClassName(config);
    }

    /**
     * Test the mapping from XSD primitive datatypes to Java datatypes.
     * @throws Exception Thrown on error.
     */
    @Test
    public void testXSDPrimitiveMapping() throws Exception {
        Anno4j anno4j = new Anno4j();

        assertEquals(ClassName.get(CharSequence.class), getJavaPoetClassName(anno4j, XSD.STRING, null));
        assertEquals(ClassName.get(Boolean.class), getJavaPoetClassName(anno4j, XSD.BOOLEAN, null));
        assertEquals(ClassName.get(Double.class), getJavaPoetClassName(anno4j, XSD.DECIMAL, null));
        assertEquals(ClassName.get(Float.class), getJavaPoetClassName(anno4j, XSD.FLOAT, null));
        assertEquals(ClassName.get(Double.class), getJavaPoetClassName(anno4j, XSD.DOUBLE, null));
        assertEquals(ClassName.get(String.class), getJavaPoetClassName(anno4j, XSD.HEX_BINARY, null));
        assertEquals(ClassName.get(String.class), getJavaPoetClassName(anno4j, XSD.BASE64_BINARY, null));
        assertEquals(ClassName.get(CharSequence.class), getJavaPoetClassName(anno4j, XSD.ANY_URI, null));
        assertEquals(ClassName.get(CharSequence.class), getJavaPoetClassName(anno4j, XSD.NORMALIZED_STRING, null));
        assertEquals(ClassName.get(CharSequence.class), getJavaPoetClassName(anno4j, XSD.TOKEN, null));
        assertEquals(ClassName.get(String.class), getJavaPoetClassName(anno4j, XSD.LANGUAGE, null));
        assertEquals(ClassName.get(Integer.class), getJavaPoetClassName(anno4j, XSD.INTEGER, null));
        assertEquals(ClassName.get(Integer.class), getJavaPoetClassName(anno4j, XSD.NON_POSITIVE_INTEGER, null));
        assertEquals(ClassName.get(Integer.class), getJavaPoetClassName(anno4j, XSD.NEGATIVE_INTEGER, null));
        assertEquals(ClassName.get(Long.class), getJavaPoetClassName(anno4j, XSD.LONG, null));
        assertEquals(ClassName.get(Integer.class), getJavaPoetClassName(anno4j, XSD.INT, null));
        assertEquals(ClassName.get(Short.class), getJavaPoetClassName(anno4j, XSD.SHORT, null));
        assertEquals(ClassName.get(Byte.class), getJavaPoetClassName(anno4j, XSD.BYTE, null));
        assertEquals(ClassName.get(Integer.class), getJavaPoetClassName(anno4j, XSD.NON_NEGATIVE_INTEGER, null));
        assertEquals(ClassName.get(Long.class), getJavaPoetClassName(anno4j, XSD.UNSIGNED_LONG, null));
        assertEquals(ClassName.get(Integer.class), getJavaPoetClassName(anno4j, XSD.UNSIGNED_INT, null));
        assertEquals(ClassName.get(Short.class), getJavaPoetClassName(anno4j, XSD.UNSIGNED_SHORT, null));
        assertEquals(ClassName.get(Byte.class), getJavaPoetClassName(anno4j, XSD.UNSIGNED_BYTE, null));
        assertEquals(ClassName.get(Integer.class), getJavaPoetClassName(anno4j, XSD.POSITIVE_INTEGER, null));
    }
}