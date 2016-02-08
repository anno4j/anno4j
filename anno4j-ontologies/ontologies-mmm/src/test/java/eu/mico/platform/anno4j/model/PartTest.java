package eu.mico.platform.anno4j.model;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Body;
import com.github.anno4j.model.impl.targets.SpecificResource;
import com.github.anno4j.querying.QueryService;
import eu.mico.platform.anno4j.model.namespaces.MMM;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.annotations.Iri;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.LangString;

import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Suite to test the Part class.
 */
public class PartTest {

    private Anno4j anno4j;
    private QueryService queryService;

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
        queryService = anno4j.createQueryService();
        queryService.addPrefix("mmm", "http://www.mico-project.eu/ns/mmm/2.0/schema#");
    }

    @Test
    public void testPart() throws RepositoryException, IllegalAccessException, InstantiationException, QueryEvaluationException, ParseException, MalformedQueryException {
        Part part = anno4j.createObject(Part.class);

        part.setAnnotatedAt(2015, 12, 17, 14, 51, 00);

        SpecificResource spec = anno4j.createObject(SpecificResource.class);

        TestBody body = anno4j.createObject(TestBody.class);

        part.setBody(body);
        part.addTarget(spec);

        // Query for no existing Part
        List<Part> result = queryService.execute(MMM.PART);

        assertEquals(0, result.size());

        // Persist the Part
        anno4j.persist(part);

        // Query for one existing Part
        result = queryService.execute(MMM.PART);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getTarget() != null);

//        System.out.println(part.getTriples(RDFFormat.TURTLE));
    }

    @Iri("http://www.example.com/schema#bodyType")
    public static interface TestBody extends Body {
        @Iri("http://www.example.com/schema#doubleValue")
        Double getDoubleValue();

        @Iri("http://www.example.com/schema#doubleValue")
        void setDoubleValue(Double doubleValue);

        @Iri("http://www.example.com/schema#langValue")
        LangString getLangValue();

        @Iri("http://www.example.com/schema#langValue")
        void setLangValue(LangString langValue);

        @Iri("http://www.example.com/schema#value")
        String getValue();

        @Iri("http://www.example.com/schema#value")
        void setValue(String value);
    }
}
