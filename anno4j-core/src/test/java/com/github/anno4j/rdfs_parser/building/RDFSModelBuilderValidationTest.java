package com.github.anno4j.rdfs_parser.building;

import com.github.anno4j.Anno4j;
import com.github.anno4j.rdfs_parser.model.RDFSClazz;
import com.hp.hpl.jena.reasoner.ValidityReport;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Test for {@link RDFSModelBuilder} validation.
 * The model builder should recognize invalid ontologies and
 * in this case not persist information to the underlying Anno4j object.
 */
public class RDFSModelBuilderValidationTest {

    @Test
    public void testCorrectOntology() throws Exception {
        Anno4j anno4j = new Anno4j();

        // Get a model builder with correct ontology:
        VehicleOntologyLoader ontologyLoader = new VehicleOntologyLoader();
        RDFSModelBuilder modelBuilder = ontologyLoader.getVehicleOntologyModelBuilder(anno4j);
        if(modelBuilder == null) {
            fail();
        }

        // Build the model:
        modelBuilder.build();

        // The model should be valid:
        ValidityReport report = modelBuilder.validate();
        assertTrue(report.isValid());

        // Check if model was persisted to Anno4j:
        List<RDFSClazz> cars = anno4j.createQueryService()
                                    .addPrefix("ex", "http://example.de/ont#")
                                    .addCriteria(".[is-a rdfs:Class AND is-a ex:Vehicle]",
                                                    "http://example.de/ont#Car")
                                    .execute(RDFSClazz.class);
        assertEquals(1, cars.size());
    }

    @Test
    public void testInvalidOntology() throws Exception {
        Anno4j anno4j = new Anno4j();

        // Get a model builder with correct ontology:
        VehicleOntologyLoader ontologyLoader = new VehicleOntologyLoader();
        RDFSModelBuilder modelBuilder = ontologyLoader.getVehicleOntologyModelBuilder(anno4j);
        if(modelBuilder == null) {
            fail();
        }

        // Set range of ex:seat_num to something not numeric (violates range constraint):
        modelBuilder.addRDF(IOUtils.toInputStream("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
                "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
                "         xmlns:ns0=\"http://example.de/ont#\">\n" +
                "  <rdf:Description rdf:about=\"http://example.de/ont#VWGolf\">\n" +
                "    <rdf:type rdf:resource=\"http://example.de/ont#Car\"/>\n" +
                "    <ns0:seat_num>Some non-numeric string</ns0:seat_num>\n" +
                "  </rdf:Description>\n" +
                "</rdf:RDF>"), "http://example.de/ont#");

        // Build the model:
        modelBuilder.build();

        // The model should be valid:
        ValidityReport report = modelBuilder.validate();
        assertFalse(report.isValid());

        // Check that the model was not persisted to Anno4j:
        List<RDFSClazz> cars = anno4j.createQueryService()
                .addPrefix("ex", "http://example.de/ont#")
                .addCriteria(".[is-a rdfs:Class AND is-a ex:Vehicle]",
                        "http://example.de/ont#Car")
                .execute(RDFSClazz.class);
        assertEquals(0, cars.size());
    }
}