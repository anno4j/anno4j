package com.github.anno4j.schema_parsing.building;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.schema_parsing.model.ExtendedRDFSClazz;
import com.github.anno4j.schema_parsing.model.ExtendedRDFSProperty;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Test for the {@link RDFSModelBuilder} class.
 * See {@link RDFSModelBuilderValidationTest} for testing the
 * model builders validation.
 */
public class RDFSModelBuilderTest {

    private static Anno4j anno4j;

    private static RDFSModelBuilder modelBuilder;

    private static ExtendedRDFSClazz getClazzFromModel(String uri) {
        Collection<ExtendedRDFSClazz> clazzes = modelBuilder.getClazzes();
        for (ExtendedRDFSClazz clazz : clazzes) {
            if(clazz.getResourceAsString().equals(uri)) {
                return clazz;
            }
        }
        return null;
    }

    private static Set<String> getResourcesAsStrings(Collection<? extends ResourceObject> objects) {
        Set<String> resourceStrings = new HashSet<>();
        for(ResourceObject object : objects) {
            resourceStrings.add(object.getResourceAsString());
        }
        return resourceStrings;
    }

    @Before
    public void setUp() throws Exception {
        anno4j = new Anno4j();

        VehicleOntologyLoader vehicleOntologyLoader = new VehicleOntologyLoader();
        modelBuilder = vehicleOntologyLoader.getVehicleOntologyModelBuilder(anno4j);

        // Build the model:
        modelBuilder.build();
    }

    @Test
    public void testClazzProperties() throws Exception {
        Collection<ExtendedRDFSClazz> clazzes = modelBuilder.getClazzes();

        // Model must contain the classes from the ontology plus RDFS build-in classes:
        assertTrue(clazzes.size() >= 5);

        ExtendedRDFSClazz vehicle = getClazzFromModel("http://example.de/ont#Vehicle");
        assertNotNull(vehicle);
        assertEquals(3, vehicle.getOutgoingProperties().size());
        Set<ExtendedRDFSProperty> vehicleOutProps = vehicle.getOutgoingProperties();
        assertTrue(getResourcesAsStrings(vehicleOutProps).contains("http://example.de/ont#seat_num"));

        ExtendedRDFSClazz car = getClazzFromModel("http://example.de/ont#Car");
        assertNotNull(car);
        assertEquals(1, car.getIncomingProperties().size());
        Set<ExtendedRDFSProperty> carInProps = car.getIncomingProperties();
        assertEquals("http://example.de/ont#parking_for", carInProps.iterator().next().getResourceAsString());

        ExtendedRDFSClazz truck = getClazzFromModel("http://example.de/ont#Truck");
        assertNotNull(truck);
        Set<String> truckInProps = getResourcesAsStrings(truck.getIncomingProperties());
        Set<String> truckOutProps = getResourcesAsStrings(truck.getOutgoingProperties());
        assertEquals(1, truckInProps.size());
        assertTrue(truckInProps.contains("http://example.de/ont#parking_for"));
        assertEquals(4, truckOutProps.size());
        assertTrue(truckOutProps.contains("http://example.de/ont#load_capacity"));
        assertTrue(truckOutProps.contains("http://example.de/ont#seat_num"));

        ExtendedRDFSClazz camper = getClazzFromModel("http://example.de/ont#Camper");
        assertNotNull(camper);
        assertEquals(4, camper.getOutgoingProperties().size());
        assertEquals(0, camper.getIncomingProperties().size());
    }

    @Test
    public void testInheritance() throws Exception {
        ExtendedRDFSClazz vehicle = getClazzFromModel("http://example.de/ont#Vehicle");
        assertNotNull(vehicle);
        assertEquals(1, vehicle.getSuperclazzes().size());

        ExtendedRDFSClazz car = getClazzFromModel("http://example.de/ont#Car");
        assertNotNull(car);
        assertEquals(1, car.getSuperclazzes().size());
        ExtendedRDFSClazz carSuper = car.getSuperclazzes().iterator().next();
        assertEquals(vehicle, carSuper);

        ExtendedRDFSClazz truck = getClazzFromModel("http://example.de/ont#Truck");
        assertNotNull(truck);
        assertEquals(1, truck.getSuperclazzes().size());
        ExtendedRDFSClazz truckSuper = truck.getSuperclazzes().iterator().next();
        assertEquals(vehicle, truckSuper);
    }

    @Test
    public void testMultipleInheritance() throws Exception {
        ExtendedRDFSClazz camper = getClazzFromModel("http://example.de/ont#Camper");
        assertNotNull(camper);
        Set<String> camperSuper = getResourcesAsStrings(camper.getSuperclazzes());
        assertEquals(2, camperSuper.size());
        assertTrue(camperSuper.contains("http://example.de/ont#Vehicle"));
        assertTrue(camperSuper.contains("http://example.de/ont#Home"));
    }

    @Test
    public void testAnno4jPersistence() throws Exception {
        List<ExtendedRDFSClazz> carClazzes = anno4j.createQueryService()
                                        .addPrefix("ex", "http://example.de/ont#")
                                        .addCriteria(".", "http://example.de/ont#Car")
                                        .execute(ExtendedRDFSClazz.class);
        assertEquals(1, carClazzes.size());
    }

    @Test
    public void testDomainRooting() throws Exception {
        // The ex:name property has no domain explicitly specified.
        // So its domain is inferred as rdfs:Class, but this should be shifted
        // to the root classes.
        ExtendedRDFSClazz vehicle = getClazzFromModel("http://example.de/ont#Vehicle");
        ExtendedRDFSClazz home = getClazzFromModel("http://example.de/ont#Home");
        assertNotNull(vehicle);
        assertNotNull(home);

        Collection<String> vehicleProps = getResourcesAsStrings(vehicle.getOutgoingProperties());
        Collection<String> homeProps = getResourcesAsStrings(home.getOutgoingProperties());
        assertTrue(vehicleProps.contains("http://example.de/ont#name"));
        assertTrue(homeProps.contains("http://example.de/ont#name"));
    }

    @Test
    public void testCyclicEquivalence() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        URL cyclicOntUrl = classLoader.getResource("cyclic_equivalence.ttl");

        RDFSModelBuilder modelBuilder = new RDFSModelBuilder();
        modelBuilder.addRDF(new FileInputStream(cyclicOntUrl.getFile()), "http://example.de/ont#", "TURTLE");

        modelBuilder.build();

        Collection<String> ontClazzes = new HashSet<>();
        for (String clazz : getResourcesAsStrings(modelBuilder.getClazzes())) {
            if(clazz.startsWith("http://example.de/ont#")) {
                ontClazzes.add(clazz);
            }
        }
        assertEquals(3, ontClazzes.size());

        // Find the class that was picked as a representative of the equivalent classes:
        ExtendedRDFSClazz equivalentClass = null;
        for (ExtendedRDFSClazz clazz : modelBuilder.getClazzes()) {
            if(clazz.getResourceAsString().equals("http://example.de/ont#B") || clazz.getResourceAsString().equals("http://example.de/ont#C")) {
                equivalentClass = clazz;
            }
        }
        assertNotNull(equivalentClass);
        assertEquals(1, equivalentClass.getOutgoingProperties().size());
        ExtendedRDFSProperty property = equivalentClass.getOutgoingProperties().iterator().next();
        assertEquals("http://example.de/ont#foo", property.getResourceAsString());
        assertEquals(1, property.getRanges().size());
        assertEquals(equivalentClass.getResourceAsString(), property.getRanges().iterator().next().getResourceAsString());
    }
}