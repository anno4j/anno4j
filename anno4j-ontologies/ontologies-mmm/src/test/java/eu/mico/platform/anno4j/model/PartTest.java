package eu.mico.platform.anno4j.model;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Body;
import com.github.anno4j.model.impl.targets.SpecificResource;
import eu.mico.platform.anno4j.model.impl.micotarget.MicoSpecificResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.annotations.Iri;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.LangString;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.result.Result;
import org.openrdf.rio.RDFFormat;

import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Suite to test the Part class.
 */
public class PartTest {

    private Anno4j anno4j;
    private ObjectConnection connection;

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
        this.connection = this.anno4j.getObjectRepository().getConnection();
    }

    @After
    public void tearDown() throws Exception {
        connection.close();
    }

    @Test
    public void testPart() throws RepositoryException, IllegalAccessException, InstantiationException, QueryEvaluationException {
        Part part = anno4j.createObject(Part.class);

        part.setAnnotatedAt(2015, 12, 17, 14, 51, 00);

        SpecificResource spec = anno4j.createObject(SpecificResource.class);

        TestBody body = anno4j.createObject(TestBody.class);

        part.setBody(body);
        part.addTarget(spec);

        // Query for no existing Part
        Result<Part> result = connection.getObjects(Part.class);

        assertEquals(0, result.asList().size());

        // Persist the Part
        anno4j.persist(part);

        // Query for one existing Part
        result = connection.getObjects(Part.class);
        List<Part> resultList = result.asList();

        assertEquals(1, resultList.size());
        assertTrue(resultList.get(0).getTarget() != null);

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
