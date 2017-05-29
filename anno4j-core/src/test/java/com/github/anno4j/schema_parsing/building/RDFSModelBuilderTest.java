package com.github.anno4j.schema_parsing.building;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.github.anno4j.schema.model.rdfs.RDFSProperty;
import com.github.anno4j.schema_parsing.model.BuildableRDFSClazz;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;

import java.io.FileInputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Test for the {@link OWLJavaFileGenerator} class with regard to RDFS.
 * See {@link ModelBuilderValidationTest} for testing the
 * model builders validation.
 */
public class RDFSModelBuilderTest {

    private static Anno4j anno4j;

    private static OntologyModelBuilder modelBuilder;

    private static BuildableRDFSClazz getClazzFromModel(String uri) throws RepositoryException {
        Collection<BuildableRDFSClazz> clazzes = modelBuilder.getClazzes();
        for (BuildableRDFSClazz clazz : clazzes) {
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

    private static <T extends ResourceObject> Collection<T> filterOwnNamespace(Collection<T> resources) {
        Collection<T> filtered = new HashSet<>();
        for (T resource : resources) {
            if(resource.getResourceAsString().startsWith("http://example.de/ont#")) {
                filtered.add(resource);
            }
        }
        return filtered;
    }

    @Before
    public void setUp() throws Exception {
        anno4j = new Anno4j();

        modelBuilder = new OWLJavaFileGenerator(anno4j);
        VehicleOntologyLoader.addVehicleOntology(modelBuilder);

        // Build the model:
        modelBuilder.build();
    }

    @Test
    public void testClazzProperties() throws Exception {
        Collection<BuildableRDFSClazz> clazzes = modelBuilder.getClazzes();

        // Model must contain the classes from the ontology plus RDFS build-in classes:
        assertTrue(clazzes.size() >= 5);

        BuildableRDFSClazz vehicle = getClazzFromModel("http://example.de/ont#Vehicle");
        assertNotNull(vehicle);

        Collection<RDFSProperty> vehicleOutProps = filterOwnNamespace(vehicle.getOutgoingProperties());
        assertEquals(3, vehicleOutProps.size());
        assertTrue(getResourcesAsStrings(vehicleOutProps).contains("http://example.de/ont#seat_num"));

        BuildableRDFSClazz car = getClazzFromModel("http://example.de/ont#Car");
        assertNotNull(car);

        Collection<RDFSProperty> carInProps = filterOwnNamespace(car.getIncomingProperties());
        assertEquals(1, carInProps.size());
        assertTrue(getResourcesAsStrings(carInProps).contains("http://example.de/ont#parking_for"));

        BuildableRDFSClazz truck = getClazzFromModel("http://example.de/ont#Truck");
        assertNotNull(truck);

        Set<String> truckInProps = getResourcesAsStrings(filterOwnNamespace(truck.getIncomingProperties()));
        Set<String> truckOutProps = getResourcesAsStrings(filterOwnNamespace(truck.getOutgoingProperties()));
        assertEquals(1, truckInProps.size());
        assertTrue(truckInProps.contains("http://example.de/ont#parking_for"));
        assertEquals(4, truckOutProps.size());
        assertTrue(truckOutProps.contains("http://example.de/ont#load_capacity"));
        assertTrue(truckOutProps.contains("http://example.de/ont#seat_num"));

        BuildableRDFSClazz camper = getClazzFromModel("http://example.de/ont#Camper");
        assertNotNull(camper);
        assertEquals(4, filterOwnNamespace(camper.getOutgoingProperties()).size());
        assertEquals(0, filterOwnNamespace(camper.getIncomingProperties()).size());
    }

    @Test
    public void testInheritance() throws Exception {
        BuildableRDFSClazz vehicle = getClazzFromModel("http://example.de/ont#Vehicle");
        assertNotNull(vehicle);
        Collection<RDFSClazz> vehicleSuperClazzes = filterOwnNamespace(vehicle.getSuperclazzes());
        vehicleSuperClazzes.remove(vehicle); // Remove (optional) reflexive relation
        assertEquals(0, vehicleSuperClazzes.size());

        BuildableRDFSClazz car = getClazzFromModel("http://example.de/ont#Car");
        assertNotNull(car);
        Collection<RDFSClazz> carSuperClazzes = filterOwnNamespace(car.getSuperclazzes());
        carSuperClazzes.remove(car);
        assertEquals(1, carSuperClazzes.size());
        assertTrue(filterOwnNamespace(car.getSuperclazzes()).contains(vehicle));

        BuildableRDFSClazz truck = getClazzFromModel("http://example.de/ont#Truck");
        assertNotNull(truck);
        Collection<RDFSClazz> truckSuperClazzes = filterOwnNamespace(truck.getSuperclazzes());
        truckSuperClazzes.remove(truck);
        assertEquals(1, truckSuperClazzes.size());
        assertTrue(filterOwnNamespace(truck.getSuperclazzes()).contains(vehicle));
    }

    @Test
    public void testMultipleInheritance() throws Exception {
        BuildableRDFSClazz camper = getClazzFromModel("http://example.de/ont#Camper");
        assertNotNull(camper);
        Set<String> camperSuper = getResourcesAsStrings(filterOwnNamespace(camper.getSuperclazzes()));
        camperSuper.remove("http://example.de/ont#Camper"); // Remove (optional) reflexive relation
        assertEquals(2, camperSuper.size());
        assertTrue(camperSuper.contains("http://example.de/ont#Vehicle"));
        assertTrue(camperSuper.contains("http://example.de/ont#Home"));
    }

    @Test
    public void testAnno4jPersistence() throws Exception {
        ObjectConnection connection = anno4j.getObjectRepository().getConnection();
        assertTrue(connection.prepareBooleanQuery(QueryLanguage.SPARQL,
                "PREFIX ex: <http://example.de/ont#> " +
                        "ASK {" +
                        "  ex:Car rdfs:subClassOf ex:Vehicle . " +
                        "  ex:parking_for rdfs:domain ex:Home . " +
                        "}"
        ).evaluate());
    }

    @Test
    public void testDomainRooting() throws Exception {
        // The ex:name property has no domain explicitly specified.
        // So its domain is inferred as rdfs:Class, but this should be shifted
        // to the root classes.
        BuildableRDFSClazz vehicle = getClazzFromModel("http://example.de/ont#Vehicle");
        BuildableRDFSClazz home = getClazzFromModel("http://example.de/ont#Home");
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

        OntologyModelBuilder modelBuilder = new OWLJavaFileGenerator();
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
        BuildableRDFSClazz equivalentClass = null;
        for (BuildableRDFSClazz clazz : modelBuilder.getClazzes()) {
            if(clazz.getResourceAsString().equals("http://example.de/ont#B") || clazz.getResourceAsString().equals("http://example.de/ont#C")) {
                equivalentClass = clazz;
            }
        }
        assertNotNull(equivalentClass);
        assertEquals(1, filterOwnNamespace(equivalentClass.getOutgoingProperties()).size());
        RDFSProperty property = filterOwnNamespace(equivalentClass.getOutgoingProperties()).iterator().next();
        assertEquals("http://example.de/ont#foo", property.getResourceAsString());
        assertEquals(1, filterOwnNamespace(property.getRanges()).size());
        assertEquals(equivalentClass.getResourceAsString(), filterOwnNamespace(property.getRanges()).iterator().next().getResourceAsString());
    }
}