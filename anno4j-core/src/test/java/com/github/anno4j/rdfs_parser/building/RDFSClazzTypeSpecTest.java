package com.github.anno4j.rdfs_parser.building;

import com.github.anno4j.rdfs_parser.model.ExtendedRDFSClazz;
import com.github.anno4j.rdfs_parser.model.ExtendedRDFSClazzSupport;
import com.squareup.javapoet.*;
import org.junit.Before;
import org.junit.Test;

import javax.lang.model.element.Modifier;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Test for the building capabilities of {@link ExtendedRDFSClazz}/{@link ExtendedRDFSClazzSupport}
 * for generating JavaPoet {@link TypeSpec}.
 */
public class RDFSClazzTypeSpecTest {

    private static RDFSModelBuilder modelBuilder;

    private static OntGenerationConfig generationConfig;

    /**
     * Returns a {@link ExtendedRDFSClazz} instance from {@link #modelBuilder}
     * with the specified URI.
     * @param uri The URI to get the class object for.
     * @return The class object or null if no class with the given URI is in the model.
     */
    private static ExtendedRDFSClazz getClazzFromModel(String uri) {
        Collection<ExtendedRDFSClazz> clazzes = modelBuilder.getRDFSClazzes();
        for (ExtendedRDFSClazz clazz : clazzes) {
            if(clazz.getResourceAsString().equals(uri)) {
                return clazz;
            }
        }
        return null;
    }

    /**
     * Returns the fully qualified names of all superinterfaces of the given type.
     * @param typeSpec The type spec to get the superinterfaces for.
     * @return The names of the types superinterfaces.
     */
    private static Set<String> getSuperinterfaceNames(TypeSpec typeSpec) {
        Set<String> superInterfaceNames = new HashSet<>();
        for (TypeName superInterface : typeSpec.superinterfaces) {
            superInterfaceNames.add(superInterface.toString());
        }
        return superInterfaceNames;
    }

    /**
     * Returns the names of all methods of the given type.
     * @param typeSpec The type to get methods for.
     * @return The method names.
     */
    private static Set<String> getMethodNames(TypeSpec typeSpec) {
        Set<String> methodNames = new HashSet<>();
        for (MethodSpec methodSpec : typeSpec.methodSpecs) {
            methodNames.add(methodSpec.name);
        }
        return methodNames;
    }

    @Before
    public void setUp() throws Exception {
        generationConfig = new OntGenerationConfig();
        List<String> identifierLangPreference = Arrays.asList("en", "de", OntGenerationConfig.UNTYPED_LITERAL);
        List<String> javaDocLangPreference = Arrays.asList("de", "en", OntGenerationConfig.UNTYPED_LITERAL);
        generationConfig.setIdentifierLanguagePreference(identifierLangPreference);
        generationConfig.setJavaDocLanguagePreference(javaDocLangPreference);

        // Create a RDFS model builder instance:
        VehicleOntologyLoader ontologyLoader = new VehicleOntologyLoader();
        modelBuilder = ontologyLoader.getVehicleOntologyModelBuilder();

        // Build the ontology model:
        modelBuilder.build();
    }

    @Test
    public void testBaseClass() throws Exception {
        ExtendedRDFSClazz vehicle = getClazzFromModel("http://example.de/ont#Vehicle");
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

        // Vehicles have the seat_num property and thus a getter and setter for it:
        assertEquals(2, vehicleSpec.methodSpecs.size());
        Set<String> methodNames = getMethodNames(vehicleSpec);
        assertTrue(methodNames.contains("getNumberOfSeats"));
        assertTrue(methodNames.contains("setNumberOfSeats"));

        // Vehicle is the topmost class in the vehicle ontology inheritance tree.
        // Its superinterface must be ResourceObject:
        assertEquals(ClassName.get(Object.class), vehicleSpec.superclass);
        assertEquals(1, vehicleSpec.superinterfaces.size());
        TypeName superInterface = vehicleSpec.superinterfaces.get(0);
        assertEquals("com.github.anno4j.model.impl.ResourceObject", superInterface.toString());
    }

    @Test
    public void testSubClass() throws Exception {
        ExtendedRDFSClazz truck = getClazzFromModel("http://example.de/ont#Truck");
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

        // Trucks have the load_capacity property and thus a getter and setter for it:
        assertEquals(2, truckSpec.methodSpecs.size());
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
        ExtendedRDFSClazz camper = getClazzFromModel("http://example.de/ont#Camper");
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
}