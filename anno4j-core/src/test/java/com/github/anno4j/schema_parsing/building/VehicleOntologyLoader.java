package com.github.anno4j.schema_parsing.building;

import com.github.anno4j.Anno4j;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

/**
 * Utility class for loading the vehicle ontology.
 */
public class VehicleOntologyLoader {

    /**
     * Creates a model builder with the vehicle ontology information added to it.
     * The model does not get built and thus {@link RDFSModelBuilder#build()} must
     * be called afterwards to build it.
     * @param anno4j The Anno4j instance to persist ontology information to.
     * @return The model builder instance created.
     * @throws FileNotFoundException If the vehicle ontology file is not found.
     */
    public RDFSModelBuilder getVehicleOntologyModelBuilder(Anno4j anno4j) throws FileNotFoundException {
        // Create a RDFS model builder instance:
        RDFSModelBuilder modelBuilder = new RDFSModelBuilder(anno4j);

        // Get the vehicle test ontology:
        ClassLoader classLoader = getClass().getClassLoader();
        URL vehicleOntUrl = classLoader.getResource("vehicle.rdf.xml");
        if(vehicleOntUrl == null) {
            throw new FileNotFoundException("The vehicle ontology file was not found.");
        }

        File ontologyFile = new File(vehicleOntUrl.getFile());

        // Add the RDF data to the builder:
        modelBuilder.addRDF(ontologyFile.getAbsolutePath());

        return modelBuilder;
    }

    /**
     * Creates a model builder with the vehicle ontology information added to it.
     * The model does not get built and thus {@link RDFSModelBuilder#build()} must
     * be called afterwards to build it.
     * The model builder uses an newly created Anno4j instance with in-memory store.
     * @return The model builder instance created.
     * @throws FileNotFoundException If the vehicle ontology file is not found.
     * @throws RepositoryConfigException If an error occurs on configuring the repository
     * of the newly created Anno4j instance.
     * @throws RepositoryException If an error occurs on creating the repository
     * of the Anno4j instance.
     */
    public RDFSModelBuilder getVehicleOntologyModelBuilder() throws FileNotFoundException, RepositoryConfigException, RepositoryException {
        Anno4j anno4j = new Anno4j();
        return getVehicleOntologyModelBuilder(anno4j);
    }
}
