package com.github.anno4j.schema_parsing.mapping;

import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.building.RDFSModelBuilder;
import com.github.anno4j.schema_parsing.model.ExtendedRDFSProperty;
import com.github.anno4j.schema_parsing.model.RDFSClazz;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test for custom mapping from RDF datatypes to Java types.
 */
public class DatatypeMappingTest {

    /**
     * A valid datatype mapper, mapping ex:oddInt to Integer.
     */
    private static class ValidOddIntegerMapper implements DatatypeMapper {

        @Override
        public Class<?> mapType(RDFSClazz type) {
            if(type.getResourceAsString().equals("http://example.de/ont#oddInt")) {
                return Integer.class;
            } else {
                return null;
            }
        }
    }

    /**
     * An invalid datatype mapper, mapping ex:oddInt to Thread.
     */
    private static class InvalidOddIntegerMapper implements DatatypeMapper {

        @Override
        public Class<?> mapType(RDFSClazz type) {
            if(type.getResourceAsString().equals("http://example.de/ont#oddInt")) {
                return Thread.class;
            } else {
                return null;
            }
        }
    }

    private static final String ontologyTtl = "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> . " +
            "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . " +
            "@prefix ex: <http://example.de/ont#> . " +
            "ex:A rdf:type rdfs:Class . " +
            "ex:oddInt rdfs:subClassOf rdfs:Literal . " +
            "ex:foo rdfs:domain ex:A . " +
            "ex:foo rdfs:range ex:oddInt . ";

    @Test
    public void testValidMapping() throws Exception {
        RDFSModelBuilder modelBuilder = new RDFSModelBuilder();
        modelBuilder.addRDF(IOUtils.toInputStream(ontologyTtl), "http://example.de/ont#", "TURTLE");
        modelBuilder.build();

        // Find the ex:foo property:
        Collection<ExtendedRDFSProperty> properties = modelBuilder.getProperties();
        ExtendedRDFSProperty foo = null;
        for (ExtendedRDFSProperty property : properties) {
            if (property.getResourceAsString().equals("http://example.de/ont#foo")) {
                foo = property;
            }
        }
        assertNotNull(foo);

        // Build the generation configuration:
        OntGenerationConfig config = new OntGenerationConfig();
        config.setDatatypeMappers(new DatatypeMapper[]{new ValidOddIntegerMapper()});

        // Build an adder for ex:foo and check the type of its parameter:
        MethodSpec adder = foo.buildAdder(config);
        assertEquals(ClassName.get(Integer.class), adder.parameters.get(0).type);
    }

    @Test
    public void testInvalidMapping() throws Exception {
        RDFSModelBuilder modelBuilder = new RDFSModelBuilder();
        modelBuilder.addRDF(IOUtils.toInputStream(ontologyTtl), "http://example.de/ont#", "TURTLE");
        modelBuilder.build();

        // Find the ex:foo property:
        Collection<ExtendedRDFSProperty> properties = modelBuilder.getProperties();
        ExtendedRDFSProperty foo = null;
        for (ExtendedRDFSProperty property : properties) {
            if (property.getResourceAsString().equals("http://example.de/ont#foo")) {
                foo = property;
            }
        }
        assertNotNull(foo);

        // Build the generation configuration:
        OntGenerationConfig config = new OntGenerationConfig();
        config.setDatatypeMappers(new DatatypeMapper[]{new InvalidOddIntegerMapper()});

        // Exception should be thrown:
        boolean exceptionThrown = false;
        try {
            foo.buildAdder(config);
        } catch (IllegalMappingException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }
}