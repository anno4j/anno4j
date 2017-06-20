package com.github.anno4j.schema_parsing.mapping;

import com.github.anno4j.Anno4j;
import com.github.anno4j.schema.model.rdfs.RDFSClazz;
import com.github.anno4j.schema_parsing.building.OWLJavaFileGenerator;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.building.OntologyModelBuilder;
import com.github.anno4j.schema_parsing.model.BuildableRDFSProperty;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
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
        Anno4j anno4j = new Anno4j();
        OntologyModelBuilder modelBuilder = new OWLJavaFileGenerator(anno4j);
        modelBuilder.addRDF(IOUtils.toInputStream(ontologyTtl), "http://example.de/ont#", "TURTLE");
        modelBuilder.build();

        RDFSClazz declaringClass = anno4j.createObject(RDFSClazz.class);

        // Find the ex:foo property:
        BuildableRDFSProperty foo = anno4j.findByID(BuildableRDFSProperty.class, "http://example.de/ont#foo");

        // Build the generation configuration:
        OntGenerationConfig config = new OntGenerationConfig();
        config.setDatatypeMappers(new ValidOddIntegerMapper());

        // Build an adder for ex:foo and check the type of its parameter:
        MethodSpec adder = foo.buildAdder(declaringClass, config);
        assertEquals(ClassName.get(Integer.class), adder.parameters.get(0).type);
    }

    @Test
    public void testInvalidMapping() throws Exception {
        Anno4j anno4j = new Anno4j();
        OntologyModelBuilder modelBuilder = new OWLJavaFileGenerator(anno4j);
        modelBuilder.addRDF(IOUtils.toInputStream(ontologyTtl), "http://example.de/ont#", "TURTLE");
        modelBuilder.build();

        RDFSClazz declaringClass = anno4j.createObject(RDFSClazz.class);

        // Find the ex:foo property:
        BuildableRDFSProperty foo = anno4j.findByID(BuildableRDFSProperty.class, "http://example.de/ont#foo");

        // Build the generation configuration:
        OntGenerationConfig config = new OntGenerationConfig();
        config.setDatatypeMappers(new InvalidOddIntegerMapper());

        // Exception should be thrown:
        boolean exceptionThrown = false;
        try {
            foo.buildAdder(declaringClass, config);
        } catch (IllegalMappingException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }
}