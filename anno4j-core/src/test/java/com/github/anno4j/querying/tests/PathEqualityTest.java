package com.github.anno4j.querying.tests;


import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Body;
import com.github.anno4j.querying.QuerySetup;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Test;
import org.openrdf.annotations.Iri;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class PathEqualityTest extends QuerySetup {


    @Override
    public void persistTestData() throws RepositoryException, InstantiationException, IllegalAccessException {
        // Persisting some data
        Annotation annotation = anno4j.createObject(Annotation.class);
        FirstPathEqualityTestBody firstTestBody = anno4j.createObject(FirstPathEqualityTestBody.class);
        firstTestBody.setValue("First Value");
        firstTestBody.setAnotherValue("Another Value");
        annotation.addBody(firstTestBody);

        Annotation annotation1 = anno4j.createObject(Annotation.class);
        SecondPathEqualityTestBody secondTestBody = anno4j.createObject(SecondPathEqualityTestBody.class);
        secondTestBody.setValue("Second Value");
        secondTestBody.setAnotherValue("Another Value");
        annotation1.addBody(secondTestBody);

        Annotation annotation2 = anno4j.createObject(Annotation.class);
        FirstPathEqualityTestBody firstTestBody2 = anno4j.createObject(FirstPathEqualityTestBody.class);
        firstTestBody2.setValue("Second Value");
        annotation2.addBody(firstTestBody2);
    }

    @Test
    public void inequalityTest() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException, RepositoryConfigException, IllegalAccessException, InstantiationException {
        List<Annotation> list = queryService.addCriteria("oa:hasBody[!ex:pathEqualityTestFirstValue is \"First Value\"]").execute();
        assertEquals(1, list.size());

        FirstPathEqualityTestBody firstPathEqualityTestBody = (FirstPathEqualityTestBody) list.get(0).getBodies().iterator().next();
        assertEquals("Second Value", firstPathEqualityTestBody.getValue());
    }


    @Test
    public void firstBodyTest() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException, RepositoryConfigException, IllegalAccessException, InstantiationException {
        List<Annotation> list = queryService.addCriteria("oa:hasBody[ex:pathEqualityTestFirstValue is \"First Value\"]").execute();
        assertEquals(1, list.size());

        FirstPathEqualityTestBody firstPathEqualityTestBody = (FirstPathEqualityTestBody) list.get(0).getBodies().iterator().next();
        assertEquals("First Value", firstPathEqualityTestBody.getValue());
        assertEquals("Another Value", firstPathEqualityTestBody.getAnotherValue());
    }

    @Test
    public void secondBodyTest() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException, RepositoryConfigException, IllegalAccessException, InstantiationException {
        List<Annotation> list1 = queryService.addCriteria("oa:hasBody[ex:pathEqualityTestSecondValue is \"Second Value\"]").execute();
        assertEquals(1, list1.size());

        SecondPathEqualityTestBody secondPathEqualityTestBody = (SecondPathEqualityTestBody) list1.get(0).getBodies().iterator().next();
        assertEquals("Second Value", secondPathEqualityTestBody.getValue());
        assertEquals("Another Value", secondPathEqualityTestBody.getAnotherValue());
    }

    @Test
    public void bothBodyTest() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException, RepositoryConfigException, IllegalAccessException, InstantiationException {
        List<Annotation> list1 = queryService.addCriteria("oa:hasBody[ex:pathEqualityTestAnotherValue is \"Another Value\"]").execute();
        assertEquals(2, list1.size());

        FirstPathEqualityTestBody firstPathEqualityTestBody = (FirstPathEqualityTestBody) list1.get(0).getBodies().iterator().next();
        assertEquals("First Value", firstPathEqualityTestBody.getValue());
        assertEquals("Another Value", firstPathEqualityTestBody.getAnotherValue());

        SecondPathEqualityTestBody secondPathEqualityTestBody = (SecondPathEqualityTestBody) list1.get(1).getBodies().iterator().next();
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
