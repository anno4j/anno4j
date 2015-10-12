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

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ReversePathTest {

    private QueryService queryService = null;
    private Anno4j anno4j;

    @Before
    public void resetQueryService() throws RepositoryConfigException, RepositoryException, InstantiationException, IllegalAccessException {
        this.anno4j = new Anno4j();
        queryService = anno4j.createQueryService();
        queryService.addPrefix("ex", "http://www.example.com/schema#");

        // Persisting some data
        Annotation annotation = anno4j.createObject(Annotation.class);
        annotation.setSerializedAt("07.05.2015");
        InverseBody inverseBody = anno4j.createObject(InverseBody.class);
        inverseBody.setValue("Some Testing Value");
        annotation.setBody(inverseBody);
        anno4j.createPersistenceService().persistAnnotation(annotation);

        Annotation annotation1 = anno4j.createObject(Annotation.class);
        annotation1.setAnnotatedAt("01.01.2011");
        InverseBody inverseBody2 = anno4j.createObject(InverseBody.class);
        inverseBody2.setValue("Another Testing Value");
        annotation1.setBody(inverseBody2);
        anno4j.createPersistenceService().persistAnnotation(annotation1);
    }


    @Test
    public void testFirstBody() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List<Annotation> annotations = queryService
                .addCriteria("oa:hasBody[is-a ex:inverseBody]/^oa:hasBody/oa:serializedAt", "07.05.2015")
                .execute();

        assertEquals(1, annotations.size());

        Annotation annotation = annotations.get(0);
        assertEquals("07.05.2015", annotation.getSerializedAt());

        // Testing if the body was persisted correctly
        InverseBody testBody = (InverseBody) annotation.getBody();
        assertEquals("Some Testing Value", testBody.getValue());
    }

    @Test
    public void testSecondBody() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List<Annotation> annotations = queryService
                .addCriteria("oa:hasBody[is-a ex:inverseBody]/^oa:hasBody/oa:annotatedAt", "01.01.2011")
                .execute();

        assertEquals(1, annotations.size());

        Annotation annotation = annotations.get(0);
        assertEquals("01.01.2011", annotation.getAnnotatedAt());

        // Testing if the body was persisted correctly
        InverseBody testBody = (InverseBody) annotation.getBody();
        assertEquals("Another Testing Value", testBody.getValue());
    }

    @Test
    public void falseTest() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List<Annotation> annotations = queryService
                .addCriteria("oa:hasBody[is-a ex:inverseBody]/^oa:hasBody/oa:serzializedAt", "01.01.2011")
                .execute();

        assertEquals(0, annotations.size());
    }


    @Iri("http://www.example.com/schema#inverseBody")
    public static interface InverseBody extends Body {
        @Iri("http://www.example.com/schema#value")
        String getValue();

        @Iri("http://www.example.com/schema#value")
        void setValue(String value);
    }
}
