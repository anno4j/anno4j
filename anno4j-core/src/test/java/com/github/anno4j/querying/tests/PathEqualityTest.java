package com.github.anno4j.querying.tests;


import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Body;
import com.github.anno4j.querying.QueryService;
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

public class PathEqualityTest {

    private QueryService queryService = null;
    private Anno4j anno4j;

    @Before
    public void resetQueryService() throws RepositoryConfigException, RepositoryException, InstantiationException, IllegalAccessException {
        this.anno4j = new Anno4j();
        queryService = anno4j.createQueryService();
        queryService.addPrefix("ex", "http://www.example.com/schema#");

        // Persisting some data
        Annotation annotation = anno4j.createObject(Annotation.class);
        FirstPathEqualityTestBody firstTestBody = anno4j.createObject(FirstPathEqualityTestBody.class);
        firstTestBody.setValue("First Value");
        firstTestBody.setAnotherValue("Another Value");
        annotation.setBody(firstTestBody);
        anno4j.createPersistenceService().persistAnnotation(annotation);

        Annotation annotation1 = anno4j.createObject(Annotation.class);
        SecondPathEqualityTestBody secondTestBody = anno4j.createObject(SecondPathEqualityTestBody.class);
        secondTestBody.setValue("Second Value");
        secondTestBody.setAnotherValue("Another Value");
        annotation1.setBody(secondTestBody);
        anno4j.createPersistenceService().persistAnnotation(annotation1);
    }

    @Test
    public void firstBodyTest() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException, RepositoryConfigException, IllegalAccessException, InstantiationException {
        List<Annotation> list = queryService.addCriteria("oa:hasBody[ex:pathEqualityTestFirstValue is \"First Value\"]").execute();
        assertEquals(1, list.size());

        FirstPathEqualityTestBody firstPathEqualityTestBody = (FirstPathEqualityTestBody) list.get(0).getBody();
        assertEquals("First Value", firstPathEqualityTestBody.getValue());
        assertEquals("Another Value", firstPathEqualityTestBody.getAnotherValue());
    }

    @Test
    public void secondBodyTest() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException, RepositoryConfigException, IllegalAccessException, InstantiationException {
        List<Annotation> list1 = queryService.addCriteria("oa:hasBody[ex:pathEqualityTestSecondValue is \"Second Value\"]").execute();
        assertEquals(1, list1.size());

        SecondPathEqualityTestBody secondPathEqualityTestBody = (SecondPathEqualityTestBody) list1.get(0).getBody();
        assertEquals("Second Value", secondPathEqualityTestBody.getValue());
        assertEquals("Another Value", secondPathEqualityTestBody.getAnotherValue());
    }

    @Test
    public void bothBodyTest() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException, RepositoryConfigException, IllegalAccessException, InstantiationException {
        List<Annotation> list1 = queryService.addCriteria("oa:hasBody[ex:pathEqualityTestAnotherValue is \"Another Value\"]").execute();
        assertEquals(2, list1.size());

        FirstPathEqualityTestBody firstPathEqualityTestBody = (FirstPathEqualityTestBody) list1.get(0).getBody();
        assertEquals("First Value", firstPathEqualityTestBody.getValue());
        assertEquals("Another Value", firstPathEqualityTestBody.getAnotherValue());

        SecondPathEqualityTestBody secondPathEqualityTestBody = (SecondPathEqualityTestBody) list1.get(1).getBody();
        assertEquals("Second Value", secondPathEqualityTestBody.getValue());
        assertEquals("Another Value", secondPathEqualityTestBody.getAnotherValue());
    }


    @Iri("http://www.example.com/schema#firstPathEqualityBodyType")
    public static interface FirstPathEqualityTestBody extends Body {

        @Iri("http://www.example.com/schema#pathEqualityTestFirstValue")
        String getValue();

        @Iri("http://www.example.com/schema#pathEqualityTestFirstValue")
        void setValue(String value);

        @Iri("http://www.example.com/schema#pathEqualityTestAnotherValue")
        String getAnotherValue();

        @Iri("http://www.example.com/schema#pathEqualityTestAnotherValue")
        void setAnotherValue(String anotherValue);
    }

    @Iri("http://www.example.com/schema#secondPathEqualityBodyType")
    public static interface SecondPathEqualityTestBody extends Body {

        @Iri("http://www.example.com/schema#pathEqualityTestSecondValue")
        String getValue();

        @Iri("http://www.example.com/schema#pathEqualityTestSecondValue")
        void setValue(String value);

        @Iri("http://www.example.com/schema#pathEqualityTestAnotherValue")
        String getAnotherValue();

        @Iri("http://www.example.com/schema#pathEqualityTestAnotherValue")
        void setAnotherValue(String anotherValue);
    }
}
