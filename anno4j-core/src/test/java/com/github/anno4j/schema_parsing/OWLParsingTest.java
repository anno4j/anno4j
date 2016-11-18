package com.github.anno4j.schema_parsing;

import com.github.anno4j.Anno4j;
import com.github.anno4j.schema_parsing.model.owl.*;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.object.LangString;
import org.openrdf.rio.*;

import java.io.*;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Manu on 15/11/16.
 */
public class OWLParsingTest {

    private Anno4j anno4j;

    private final static String ECRM = "ecrm_160714.owl";

    private final static String CIDOC_NS = "http://www.cidoc-crm.org/cidoc-crm/";
    private final static String ERLANGEN_NS = "http://erlangen-crm.org/160714/";

    private final static String INSCRIPTION_URI = ERLANGEN_NS + "E34_Inscription";
    private final static String MARK_URI = ERLANGEN_NS + "E37_Mark";
    private final static String LINGUISTIC_OBJECT_URI = ERLANGEN_NS + "E33_Linguistic_Object";
    private final static String TRANSFER_OF_CUSTODY_URI = ERLANGEN_NS + "E10_Transfer_of_Custody";
    private final static String ACTIVITY_URI = ERLANGEN_NS + "E7_Activity";
    private final static String PERSON_URI = ERLANGEN_NS + "E21_Person";
    private final static String PHYSICAL_THING_URI = ERLANGEN_NS + "E18_Physical_Thing";
    private final static String ACQUISITION_URI = ERLANGEN_NS + "E8_Acquisition";

    private final static String TRANSFERRED_TITLE_TO_URI = ERLANGEN_NS + "P22_transferred_title_to";
    private final static String CARRIED_OUT_BY_URI = ERLANGEN_NS + "P14_carried_out_by";
    private final static String ACQUIRED_TITLE_THROUGH_URI = ERLANGEN_NS + "P22i_acquired_title_through";
    private final static String TRANSFERRED_CUSTODY_OF_URI = ERLANGEN_NS + "P30_transferred_custody_of";

    @Before
    public void setup() throws RepositoryConfigException, RepositoryException, RDFParseException, IOException, RDFHandlerException {
        this.anno4j = new Anno4j();

        parseOWL();
    }

    @Test
    public void countClassesAndProperties() throws RepositoryException, ParseException, MalformedQueryException, QueryEvaluationException {
        List<OWLClazz> clazzes = this.anno4j.createQueryService().execute(OWLClazz.class);

        assertEquals(84, clazzes.size());

        List<OWLInverseFunctionalProperty> inverseFunctionalProperties = this.anno4j.createQueryService().execute(OWLInverseFunctionalProperty.class);

        assertEquals(1, inverseFunctionalProperties.size());

        List<OWLFunctionalProperty> functionalProperties = this.anno4j.createQueryService().execute(OWLFunctionalProperty.class);

        assertEquals(1, functionalProperties.size());

        List<OWLSymmetricProperty> symmetricProperties = this.anno4j.createQueryService().execute(OWLSymmetricProperty.class);

        assertEquals(5, symmetricProperties.size());

        List<OWLTransitiveProperty> transitiveProperties = this.anno4j.createQueryService().execute(OWLTransitiveProperty.class);

        assertEquals(31, transitiveProperties.size());

        List<OWLObjectProperty> properties = this.anno4j.createQueryService().execute(OWLObjectProperty.class);

        assertEquals(273, properties.size());
    }

    @Test
    public void testOWLClazzFields() throws RepositoryException, ParseException, MalformedQueryException, QueryEvaluationException {
        OWLClazz inscription = this.anno4j.createQueryService().addCriteria(".", INSCRIPTION_URI).execute(OWLClazz.class).get(0);

        assertTrue(inscription.getComment().startsWith("Scope note:\nThis class"));
        assertEquals("E34 Inscription", inscription.getLabels().toArray()[0].toString());
        assertEquals("E34", inscription.getNotation());

        OWLClazz subClazz1 = (OWLClazz) inscription.getSubClazzes().toArray()[0];
        OWLClazz subClazz2 = (OWLClazz) inscription.getSubClazzes().toArray()[1];

        assertTrue(subClazz1.getResourceAsString().equals(LINGUISTIC_OBJECT_URI) || subClazz1.getResourceAsString().equals(MARK_URI));
        assertTrue(subClazz2.getResourceAsString().equals(LINGUISTIC_OBJECT_URI) || subClazz2.getResourceAsString().equals(MARK_URI));
    }

    @Test
    public void testRestriction() throws RepositoryException, ParseException, MalformedQueryException, QueryEvaluationException {
        OWLClazz transfer = this.anno4j.createQueryService().addCriteria(".", TRANSFER_OF_CUSTODY_URI).execute(OWLClazz.class).get(0);

        assertEquals(1, transfer.getRestrictions().size());

        OWLRestriction restriction = (OWLRestriction) transfer.getRestrictions().toArray()[0];
        assertEquals(restriction.getSomeValuesFrom().getResourceAsString(), PHYSICAL_THING_URI);
        assertEquals(restriction.getOnProperty().getResourceAsString(), TRANSFERRED_CUSTODY_OF_URI);

        // Test the "normal" sublcasses for completeness
        assertEquals(1, transfer.getSubClazzes().size());
        assertEquals(ACTIVITY_URI, ((OWLClazz) transfer.getSubClazzes().toArray()[0]).getResourceAsString());
    }

    @Test
    public void testObjectProperties() throws RepositoryException, ParseException, MalformedQueryException, QueryEvaluationException {
        OWLObjectProperty transferred = this.anno4j.createQueryService().addCriteria(".", TRANSFERRED_TITLE_TO_URI).execute(OWLObjectProperty.class).get(0);

        assertEquals("P22", transferred.getNotation());
        assertEquals("P22 transferred title to", ((LangString) transferred.getLabels().toArray()[0]).toString());
        assertEquals("en", ((LangString) transferred.getLabels().toArray()[0]).getLocale().toString());

        assertEquals(CARRIED_OUT_BY_URI, ((OWLObjectProperty) transferred.getSubProperties().toArray()[0]).getResourceAsString());

        assertEquals(ACQUIRED_TITLE_THROUGH_URI, transferred.getInverseOf().getResourceAsString());

        assertEquals(ACQUISITION_URI, transferred.getDomain().getResourceAsString());
    }

    @Test
    public void parseOWL() throws IOException, RDFParseException, RDFHandlerException {
        //Get file from resources folder
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(ECRM).getFile());

        assertTrue(file != null);

        InputStream is = new FileInputStream(file);

        RDFParser parser = Rio.createParser(RDFFormat.RDFXML);

        SchemaParsingHandler handler = new SchemaParsingHandler(this.anno4j);
        parser.setRDFHandler(handler);

        parser.parse(is, CIDOC_NS);
    }
}
