package com.github.anno4j.schema_parsing;

import com.github.anno4j.Anno4j;
import org.eclipse.rdf4j.rio.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Manu on 02/11/16.
 */
public class TestBasicParsing {

    private Anno4j anno4j;
    private final static String CIDOC_URL = "http://new.cidoc-crm.org/sites/default/files/cidoc_crm_v5.0.4_official_release.rdfs.xml";

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
    }

    @Test
    public void readRDFS() {


        URL documentUrl = null;
        try {
            documentUrl = new URL(CIDOC_URL);
            InputStream inputStream = documentUrl.openStream();

            System.out.println(inputStream.toString());

            RDFParser parser = Rio.createParser(RDFFormat.RDFXML);

            Anno4jStatementHandler handler = new Anno4jStatementHandler(this.anno4j);
            parser.setRDFHandler(handler);

            parser.parse(inputStream, CIDOC_URL);

            inputStream.close();
        } catch (RDFParseException | RDFHandlerException | IOException e) {
            e.printStackTrace();
        }

        System.out.println("test");
    }
}
