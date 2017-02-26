package com.github.anno4j.schema_parsing.generation;

import com.github.anno4j.Anno4j;
import com.github.anno4j.rdfs_parser.model.RDFSProperty;
import com.squareup.javapoet.JavaFile;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Manu on 24/11/16.
 */
public class InterfaceContainerTest {

    private Anno4j anno4j;

    private final static String CIDOC_NS = "http://www.cidoc-crm.org/cidoc-crm/";
    private final static String CIDOC_URL = "http://new.cidoc-crm.org/sites/default/files/cidoc_crm_v5.0.4_official_release.rdfs.xml";
    private final static String PACKAGE_PATH = "com.github.anno4j.schema_parsing.generation";

    private final static String ENTITY_URI = CIDOC_NS + "E1_CRM_Entity";
    private final static String ACTIVITY_URI = CIDOC_NS + "E7_Activity";

    private final static String INFLUENCED_URI = CIDOC_NS + "P15i_influenced";

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
        this.anno4j.parseSchema(new URL(CIDOC_URL), RDFFormat.RDFXML, CIDOC_NS);
    }

    @Test
    public void testInterfaceContainer() throws IOException, RepositoryException, ParseException, MalformedQueryException, QueryEvaluationException {
        RDFSProperty influenced = this.anno4j.createQueryService().addCriteria(".", INFLUENCED_URI).execute(RDFSProperty.class).get(0);

        InterfaceContainer ic = new InterfaceContainer(ENTITY_URI, PACKAGE_PATH);

        ic.addProperty(influenced);
        ic.addSuperClazz(ACTIVITY_URI);

        JavaFile javaFile = ic.generateInterface();

//        assertEquals(OUTPUT, javaFile.toString());

        javaFile.writeTo(System.out);

        JavaFile support = ic.generateSupport();

        support.writeTo(System.out);
    }

}