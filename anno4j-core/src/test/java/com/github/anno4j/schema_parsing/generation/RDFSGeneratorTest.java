package com.github.anno4j.schema_parsing.generation;

import com.github.anno4j.Anno4j;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;

import java.net.URL;

import static org.junit.Assert.*;

/**
 * Created by Manu on 24/11/16.
 */
public class RDFSGeneratorTest {

    private Anno4j anno4j;
    private final static String CIDOC_URL = "http://new.cidoc-crm.org/sites/default/files/cidoc_crm_v5.0.4_official_release.rdfs.xml";
    private final static String CIDOC_NS = "http://www.cidoc-crm.org/cidoc-crm/";

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
        this.anno4j.parseSchema(new URL(CIDOC_URL), RDFFormat.RDFXML, CIDOC_NS);
    }

    @Test
    public void testRDFSGeneration() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
//        RDFSGenerator gen = new RDFSGenerator(this.anno4j, "com.github.anno4j.schema_parsing.generation", CIDOC_NS, "cidoc");
//
//        gen.generateModel();
//
//        System.out.println("test");
    }
}