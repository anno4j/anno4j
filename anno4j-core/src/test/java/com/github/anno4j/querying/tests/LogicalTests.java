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

public class LogicalTests {

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
        annotation.setBody(new FirstLogicalTestBody("First Value", "Another Value", new LangString("Testwert", "de")));
        Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation);

        Annotation annotation1 = new Annotation();
        annotation1.setBody(new SecondLogicalTestBody("Second Value", new LangString("Second Body Lang Value", "en")));
        Anno4j.getInstance().createPersistenceService().persistAnnotation(annotation1);
    }

    @Test
    public void logicalOrTest() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List list = queryService.setAnnotationCriteria("oa:hasBody[is-a ex:firstLogicalBodyType  | is-a ex:secondLogicalBodyType]").execute();
        assertEquals(2, list.size());

        resetQueryService();

        List<Annotation> list1 = queryService.setAnnotationCriteria("oa:hasBody/ex:languageValue[@de]").execute();
        assertEquals(1, list1.size());
        FirstLogicalTestBody firstLogicalTestBody = (FirstLogicalTestBody) list1.get(0).getBody();
        assertEquals(firstLogicalTestBody.getLangValue().toString(), "Testwert");

        resetQueryService();

        List<Annotation> list2 = queryService.setAnnotationCriteria("oa:hasBody/ex:languageValue[@en]").execute();
        assertEquals(1, list2.size());
        SecondLogicalTestBody secondLogicalTestBody = (SecondLogicalTestBody) list2.get(0).getBody();
        assertEquals(secondLogicalTestBody.getLangValue().toString(), "Second Body Lang Value");

        resetQueryService();

        List list3 = queryService.setAnnotationCriteria("oa:hasBody/ex:languageValue[@en | @de]").execute();
        assertEquals(2, list3.size());

        resetQueryService();

        List list4 = queryService.setAnnotationCriteria("oa:hasBody/ex:languageValue[@es | @de]").execute();
        assertEquals(1, list4.size());
    }

    @Test
    public void logicalAndTest() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List<Annotation> list = queryService.setAnnotationCriteria("oa:hasBody[ex:logicalTestFirstValue is \"First Value\"  & ex:logicalTestAnotherValue is \"Another Value\"]").execute();
        assertEquals(1, list.size());
        FirstLogicalTestBody firstLogicalTestBody = (FirstLogicalTestBody) list.get(0).getBody();
        assertEquals("First Value", firstLogicalTestBody.getValue());
        assertEquals("Another Value", firstLogicalTestBody.getAnotherValue());

        resetQueryService();

        List<Annotation> list1 = queryService.setAnnotationCriteria("oa:hasBody[rdf:type is ex:firstLogicalBodyType  & ex:logicalTestAnotherValue is \"Another Value\"]").execute();
        assertEquals(1, list1.size());
        assertEquals("Another Value", ((FirstLogicalTestBody) list.get(0).getBody()).getAnotherValue());
    }

    @Iri("http://www.example.com/schema#firstLogicalBodyType")
    public static class FirstLogicalTestBody extends Body {

        @Iri("http://www.example.com/schema#logicalTestFirstValue")
        private String value;

        @Iri("http://www.example.com/schema#logicalTestAnotherValue")
        private String anotherValue;

        @Iri("http://www.example.com/schema#languageValue")
        private LangString langValue;

        public FirstLogicalTestBody() {
        }

        public FirstLogicalTestBody(String value, String anotherValue, LangString langValue) {
            this.value = value;
            this.anotherValue = anotherValue;
            this.langValue = langValue;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }

        public LangString getLangValue() {
            return langValue;
        }

        public void setLangValue(LangString langValue) {
            this.langValue = langValue;
        }

        public String getAnotherValue() {
            return anotherValue;
        }

        public void setAnotherValue(String anotherValue) {
            this.anotherValue = anotherValue;
        }
    }

    @Iri("http://www.example.com/schema#secondLogicalBodyType")
    public static class SecondLogicalTestBody extends Body {

        @Iri("http://www.example.com/schema#logicalTestSecondValue")
        private String value;

        @Iri("http://www.example.com/schema#languageValue")
        private LangString langValue;

        public SecondLogicalTestBody() {
        }

        public SecondLogicalTestBody(String value, LangString langValue) {
            this.value = value;
            this.langValue = langValue;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }

        public LangString getLangValue() {
            return langValue;
        }

        public void setLangValue(LangString langValue) {
            this.langValue = langValue;
        }
    }
}
