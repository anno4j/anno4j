package com.github.anno4j.schema_parsing;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.schema_parsing.model.rdfs.RDFSClazz;
import com.github.anno4j.schema_parsing.model.rdfs.RDFSProperty;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Test suite that tests the parsing of an RDF schema and the Anno4j classes therefore.
 */
public class BasicRDFSParsingTest {

    private Anno4j anno4j;
    private final static String CIDOC_URL = "http://new.cidoc-crm.org/sites/default/files/cidoc_crm_v5.0.4_official_release.rdfs.xml";
    private final static String CIDOC_NS = "http://www.cidoc-crm.org/cidoc-crm/";

    private final static String ENTITY_URI = CIDOC_NS + "E1_CRM_Entity";
    private final static String TEMPORAL_ENTITY_URI = CIDOC_NS + "E2_Temporal_Entity";
    private final static String ACTIVITY_URI = CIDOC_NS + "E7_Activity";
    private final static String INSCRIPTION_URI = CIDOC_NS + "E34_Inscription";
    private final static String LINGUISTIC_OBJECT_URI = CIDOC_NS + "E33_Linguistic_Object";
    private final static String MARK_URI = CIDOC_NS + "E37_Mark";

    private final static String WAS_MOTIVATED_BY_URI = CIDOC_NS + "P17_was_motivated_by";
    private final static String WAS_INFLUENCED_BY_URI = CIDOC_NS + "P15_was_influenced_by";

    private final static String USED_SPECIFIC_OBJECT_URI = CIDOC_NS + "P16_used_specific_object";
    private final static String OCCURED_IN_THE_PRESENCE_OF_URI = CIDOC_NS + "P12_occurred_in_the_presence_of";

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
        parseRDF();
    }

    @Test
    public void testClazzesAndProperties() throws RepositoryException, ParseException, MalformedQueryException, QueryEvaluationException {
        List<RDFSClazz> clazzes = this.anno4j.createQueryService().execute(RDFSClazz.class);

        assertEquals(82, clazzes.size());

        List<RDFSProperty> properties = this.anno4j.createQueryService().execute(RDFSProperty.class);

        assertEquals(262, properties.size());
    }

    @Test
    public void testClazzFields() throws RepositoryException, ParseException, MalformedQueryException, QueryEvaluationException {
        RDFSClazz entity = this.anno4j.createQueryService().addCriteria(".", ENTITY_URI).execute(RDFSClazz.class).get(0);

        assertEquals(6, entity.getLabels().size());
        assertTrue(entity.getComment().startsWith("This class comprises"));

        RDFSClazz temporalEntity = this.anno4j.createQueryService().addCriteria(".", TEMPORAL_ENTITY_URI).execute(RDFSClazz.class).get(0);

        assertEquals(entity.getResourceAsString(), ((ResourceObject) temporalEntity.getSubClazzes().toArray()[0]).getResourceAsString());

        RDFSClazz inscription = this.anno4j.createQueryService().addCriteria(".", INSCRIPTION_URI).execute(RDFSClazz.class).get(0);

        assertEquals(2, inscription.getSubClazzes().size());
        RDFSClazz subClazz1 = (RDFSClazz) inscription.getSubClazzes().toArray()[0];
        RDFSClazz subClazz2 = (RDFSClazz) inscription.getSubClazzes().toArray()[1];

        assertTrue(subClazz1.getResourceAsString().equals(LINGUISTIC_OBJECT_URI) || subClazz1.getResourceAsString().equals(MARK_URI));
        assertTrue(subClazz2.getResourceAsString().equals(LINGUISTIC_OBJECT_URI) || subClazz2.getResourceAsString().equals(MARK_URI));
    }

    @Test
    public void testPropertyFields() throws RepositoryException, ParseException, MalformedQueryException, QueryEvaluationException {
        RDFSProperty motivated = this.anno4j.createQueryService().addCriteria(".", WAS_MOTIVATED_BY_URI).execute(RDFSProperty.class).get(0);
        RDFSProperty influenced = this.anno4j.createQueryService().addCriteria(".", WAS_INFLUENCED_BY_URI).execute(RDFSProperty.class).get(0);

        RDFSClazz entity = this.anno4j.createQueryService().addCriteria(".", ENTITY_URI).execute(RDFSClazz.class).get(0);
        RDFSClazz activity = this.anno4j.createQueryService().addCriteria(".", ACTIVITY_URI).execute(RDFSClazz.class).get(0);

        assertEquals(6, motivated.getLabels().size());
        assertTrue(motivated.getComment().startsWith("This property describes an item"));
        assertEquals(influenced.getResourceAsString(), ((ResourceObject) motivated.getSubProperties().toArray()[0]).getResourceAsString());
        assertEquals(activity.getResourceAsString(), motivated.getDomain().getResourceAsString());
        assertEquals(entity.getResourceAsString(), motivated.getRange().getResourceAsString());

        RDFSProperty specific = this.anno4j.createQueryService().addCriteria(".", USED_SPECIFIC_OBJECT_URI).execute(RDFSProperty.class).get(0);
        RDFSProperty subProperty1 = (RDFSProperty) specific.getSubProperties().toArray()[0];
        RDFSProperty subProperty2 = (RDFSProperty) specific.getSubProperties().toArray()[1];

        assertTrue(subProperty1.getResourceAsString().equals(OCCURED_IN_THE_PRESENCE_OF_URI) || subProperty1.getResourceAsString().equals(WAS_INFLUENCED_BY_URI));
        assertTrue(subProperty2.getResourceAsString().equals(OCCURED_IN_THE_PRESENCE_OF_URI) || subProperty2.getResourceAsString().equals(WAS_INFLUENCED_BY_URI));
    }

    private void parseRDF() {
        URL documentUrl = null;
        try {
            documentUrl = new URL(CIDOC_URL);
            InputStream inputStream = documentUrl.openStream();

            RDFParser parser = Rio.createParser(RDFFormat.RDFXML);

            SchemaParsingHandler handler = new SchemaParsingHandler(this.anno4j);
            parser.setRDFHandler(handler);

            parser.parse(inputStream, CIDOC_URL);

            inputStream.close();
        } catch (RDFParseException | RDFHandlerException | IOException e) {
            e.printStackTrace();
        }
    }
}
