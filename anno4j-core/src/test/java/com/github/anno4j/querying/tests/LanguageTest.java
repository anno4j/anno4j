package com.github.anno4j.querying.tests;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Body;
import com.github.anno4j.querying.QueryService;
import com.github.anno4j.querying.QuerySetup;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.annotations.Iri;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.object.LangString;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Containing all tests, that do query for specific lang values.
 */
public class LanguageTest extends QuerySetup {

    @Test
    /**
     * Querying for annotations, containing bodies with english lang values.
     */
    public void testFirstBody() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List<Annotation> list = queryService
                .addCriteria("oa:hasBody/ex:languageValue[@en]", "First Value")
                .execute();

        LangTestBody testBody = (LangTestBody) list.get(0).getBody();
        assertEquals("en", testBody.getLangString().getLang());
        assertEquals("First Value", testBody.getLangString().toString());
    }

    @Test
    /**
     * Querying for annotations, containing bodies with german lang values.
     */
    public void testSecondBody() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List<Annotation> list = queryService
                .addCriteria("oa:hasBody/ex:languageValue[@de]")
                .execute();

        assertEquals(1, list.size());

        LangTestBody testBody = (LangTestBody) list.get(0).getBody();
        assertEquals("de", testBody.getLangString().getLang());
        assertEquals("Zweiter Wert", testBody.getLangString().toString());
    }

    @Test
    /**
     * Querying for annotations, containing bodies with spanish lang values. This should not return any result.
     */
    public void falseBody() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List<Annotation> list = queryService
                .addCriteria("oa:hasBody/ex:languageValue[@es]")
                .execute();

        assertEquals(0, list.size());
    }

    @Override
    public void persistTestData() throws RepositoryException, InstantiationException, IllegalAccessException {
        // Persisting some data
        Annotation annotation = anno4j.createObject(Annotation.class);
        annotation.setSerializedAt("07.05.2015");
        LangTestBody langTestBody = anno4j.createObject(LangTestBody.class);
        langTestBody.setLangString(new LangString("First Value", "en"));
        annotation.setBody(langTestBody);
        anno4j.createPersistenceService().persistAnnotation(annotation);

        Annotation annotation1 = anno4j.createObject(Annotation.class);
        annotation1.setAnnotatedAt("01.01.2011");
        LangTestBody langTestBody2 = anno4j.createObject(LangTestBody.class);
        langTestBody2.setLangString(new LangString("Zweiter Wert", "de"));
        annotation1.setBody(langTestBody2);
        anno4j.createPersistenceService().persistAnnotation(annotation1);
    }

    @Iri("http://www.example.com/schema#langBody")
    public static interface LangTestBody extends Body {
        @Iri("http://www.example.com/schema#languageValue")
        LangString getLangString();

        @Iri("http://www.example.com/schema#languageValue")
        void setLangString (LangString value);
    }

}
