package com.github.anno4j.querying.tests;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Body;
import com.github.anno4j.querying.QueryService;
import com.google.gson.Gson;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.annotations.Iri;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.LangString;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Containing all tests, that do query for specific lang values.
 */
public class LanguageTest {

    private QueryService<Annotation> queryService = null;

    @Before
    public void resetQueryService() {
        queryService = Anno4j.getInstance().createQueryService(Annotation.class);
        queryService.addPrefix("ex", "http://www.example.com/schema#");
    }

    @BeforeClass
    public static void setUp() throws RepositoryException {
        // Persisting some data
        Annotation annotation = new Annotation();
        annotation.setSerializedAt("07.05.2015");
        annotation.setBody(new LangTestBody(new LangString("First Value", "en")));
        Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation);

        Annotation annotation1 = new Annotation();
        annotation1.setAnnotatedAt("01.01.2011");
        annotation1.setBody(new LangTestBody(new LangString("Zweiter Wert", "de")));
        Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation1);
    }

    @Test
    /**
     * Querying for annotations, containing bodies with english lang values.
     */
    public void testFirstBody() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List<Annotation> list = queryService
                .setBodyCriteria("/ex:languageValue[@en]", "First Value")
                .execute();

        LangTestBody testBody = (LangTestBody) list.get(0).getBody();
        assertEquals("en", testBody.getValue().getLang());
        assertEquals("First Value", testBody.getValue().toString());
    }

    @Test
    /**
     * Querying for annotations, containing bodies with german lang values.
     */
    public void testSecondBody() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List<Annotation> list = queryService
                .setBodyCriteria("/ex:languageValue[@de]")
                .execute();

        assertEquals(1, list.size());

        LangTestBody testBody = (LangTestBody) list.get(0).getBody();
        assertEquals("de", testBody.getValue().getLang());
        assertEquals("Zweiter Wert", testBody.getValue().toString());
    }

    @Test
    /**
     * Querying for annotations, containing bodies with spanish lang values. This should not return any result.
     */
    public void falseBody() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List<Annotation> list = queryService
                .setBodyCriteria("ex:languageValue[@es]")
                .execute();

        assertEquals(0, list.size());
    }

    @Iri("http://www.example.com/schema#langBody")
    public static class LangTestBody extends Body {

        @Iri("http://www.example.com/schema#languageValue")
        private LangString langString;

        public LangTestBody() {
        }

        public LangTestBody(LangString value) {
            this.langString = value;
        }

        public LangString getValue() {
            return langString;
        }

        public void setValue(LangString value) {
            this.langString = value;
        }

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }

}
