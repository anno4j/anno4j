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

public class LogicalTests extends QuerySetup {

    @Test
    public void logicalOrTest() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException, RepositoryConfigException, IllegalAccessException, InstantiationException {

        List<Annotation> list = queryService.addCriteria("oa:hasBody[is-a ex:firstLogicalBodyType  | is-a ex:secondLogicalBodyType]").execute();
        assertEquals(2, list.size());

        super.setupUpQueryTest();

        List<Annotation> list1 = queryService.addCriteria("oa:hasBody/ex:languageValue[@de]").execute();
        assertEquals(1, list1.size());
        FirstLogicalTestBody firstLogicalTestBody = (FirstLogicalTestBody) list1.get(0).getBody();
        assertEquals(firstLogicalTestBody.getLangValue().toString(), "Testwert");

        super.setupUpQueryTest();

        List<Annotation> list2 = queryService.addCriteria("oa:hasBody/ex:languageValue[@en]").execute();
        assertEquals(1, list2.size());
        SecondLogicalTestBody secondLogicalTestBody = (SecondLogicalTestBody) list2.get(0).getBody();
        assertEquals(secondLogicalTestBody.getLangValue().toString(), "Second Body Lang Value");

        super.setupUpQueryTest();

        List list3 = queryService.addCriteria("oa:hasBody/ex:languageValue[@en | @de]").execute();
        assertEquals(2, list3.size());

        super.setupUpQueryTest();

        List list4 = queryService.addCriteria("oa:hasBody/ex:languageValue[@es | @de]").execute();
        assertEquals(1, list4.size());
    }

    @Test
    public void logicalAndTest() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException, RepositoryConfigException, IllegalAccessException, InstantiationException {
        List<Annotation> list = queryService.addCriteria("oa:hasBody[ex:logicalTestFirstValue is \"First Value\"  & ex:logicalTestAnotherValue is \"Another Value\"]").execute();
        assertEquals(1, list.size());
        FirstLogicalTestBody firstLogicalTestBody = (FirstLogicalTestBody) list.get(0).getBody();
        assertEquals("First Value", firstLogicalTestBody.getValue());
        assertEquals("Another Value", firstLogicalTestBody.getAnotherValue());

        super.setupUpQueryTest();

        List<Annotation> list1 = queryService.addCriteria("oa:hasBody[rdf:type is ex:firstLogicalBodyType  & ex:logicalTestAnotherValue is \"Another Value\"]").execute();
        assertEquals(1, list1.size());
        assertEquals("Another Value", ((FirstLogicalTestBody) list.get(0).getBody()).getAnotherValue());
    }

    @Override
    public void persistTestData() throws RepositoryException, InstantiationException, IllegalAccessException {
        // Persisting some data
        Annotation annotation = anno4j.createObject(Annotation.class);
        FirstLogicalTestBody firstTestBody = anno4j.createObject(FirstLogicalTestBody.class);
        firstTestBody.setValue("First Value");
        firstTestBody.setAnotherValue("Another Value");
        firstTestBody.setLangValue(new LangString("Testwert", "de"));
        annotation.setBody(firstTestBody);
        anno4j.createPersistenceService().persistAnnotation(annotation);

        Annotation annotation1 = anno4j.createObject(Annotation.class);
        SecondLogicalTestBody secondTestBody = anno4j.createObject(SecondLogicalTestBody.class);
        secondTestBody.setValue("Second Value");
        secondTestBody.setLangValue(new LangString("Second Body Lang Value", "en"));
        annotation1.setBody(secondTestBody);
        anno4j.createPersistenceService().persistAnnotation(annotation1);
    }

    @Iri("http://www.example.com/schema#firstLogicalBodyType")
    public static interface FirstLogicalTestBody extends Body {

        @Iri("http://www.example.com/schema#logicalTestFirstValue")
        String getValue();

        @Iri("http://www.example.com/schema#logicalTestFirstValue")
        void setValue(String value);

        @Iri("http://www.example.com/schema#logicalTestAnotherValue")
        String getAnotherValue();

        @Iri("http://www.example.com/schema#logicalTestAnotherValue")
        void setAnotherValue(String anotherValue);

        @Iri("http://www.example.com/schema#languageValue")
        LangString getLangValue();

        @Iri("http://www.example.com/schema#languageValue")
        void setLangValue(LangString langValue);
    }

    @Iri("http://www.example.com/schema#secondLogicalBodyType")
    public static interface SecondLogicalTestBody extends Body {

        @Iri("http://www.example.com/schema#logicalTestSecondValue")
        String getValue();

        @Iri("http://www.example.com/schema#logicalTestSecondValue")
        void setValue(String value);

        @Iri("http://www.example.com/schema#languageValue")
        LangString getLangValue();

        @Iri("http://www.example.com/schema#languageValue")
        void setLangValue(LangString langValue);
    }
}

