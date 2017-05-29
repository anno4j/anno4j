package com.github.anno4j.schema_parsing.building;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

/**
 * Utility class for loading the vehicle ontology.
 */
public class VehicleOntologyLoader {

    /**
     * Adds the ontology information from the vehicle ontology to the given model builder.
     * @param modelBuilder The model builder to which the ontology information should be added.
     * @throws FileNotFoundException Thrown if the ontology file was not found.
     */
    public static void addVehicleOntology(OntologyModelBuilder modelBuilder) throws FileNotFoundException {
        // Get the vehicle test ontology:
        ClassLoader classLoader = modelBuilder.getClass().getClassLoader();
        URL vehicleOntUrl = classLoader.getResource("vehicle.rdf.xml");
        if(vehicleOntUrl == null) {
            throw new FileNotFoundException("The vehicle ontology file was not found.");
        }

        File ontologyFile = new File(vehicleOntUrl.getFile());

        // Add the RDF data to the builder:
        modelBuilder.addRDF(ontologyFile.getAbsolutePath());
    }
}
