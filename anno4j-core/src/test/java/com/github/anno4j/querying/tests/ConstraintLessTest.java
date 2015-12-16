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

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Containing all tests, that do not provide a constraint value
 * when defining a criteria.
 */
public class ConstraintLessTest extends QuerySetup {

    @Override
    public void persistTestData() throws RepositoryException, InstantiationException, IllegalAccessException {
        Annotation annotation = anno4j.createObject(Annotation.class);
        annotation.setSerializedAt("07.05.2015");

        ConstraintLessBody constraintLessBody = anno4j.createObject(ConstraintLessBody.class);
        constraintLessBody.setValue("Value 1");
        annotation.setBody(constraintLessBody);
        anno4j.persist(annotation);

        Annotation annotation1 = anno4j.createObject(Annotation.class);
        annotation1.setAnnotatedAt("01.01.2011");
        ConstraintLessBody constraintLessBody2 = anno4j.createObject(ConstraintLessBody.class);
        constraintLessBody2.setValue("Value 2");

        annotation1.setBody(constraintLessBody2);
        anno4j.persist(annotation1);

        // This
        Annotation annotation2 = anno4j.createObject(Annotation.class);
        annotation2.setAnnotatedAt("01.01.2011");
        annotation2.setBody(anno4j.createObject(ConstraintLessBody.class));
        anno4j.persist(annotation2);
    }

    @Test
    /**
     * Querying for all annotation objects, where the containing body has a specific attribute set.
     */
    public void retrieveAll() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List<Annotation> list = queryService
                .addCriteria("oa:hasBody/ex:constraintLessValue")
                .execute();

        assertEquals(2, list.size());

        // Test for annotation specific attributes
        assertEquals("07.05.2015", list.get(0).getSerializedAt());
        assertEquals("01.01.2011", list.get(1).getAnnotatedAt());

        // Test for the value attribute of the body object
        assertEquals("Value 1", ((ConstraintLessBody) list.get(0).getBody()).getValue());
        assertEquals("Value 2", ((ConstraintLessBody) list.get(1).getBody()).getValue());
    }

    @Test
    /**
     * Trying to query for an object that was not persisted in the first place.
     */
    public void falseTest() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        List<Annotation> list = queryService
                .addCriteria("oa:hasBody/ex:nonExistingVaue")
                .execute();

        assertEquals(0, list.size());
    }

    @Iri("http://www.example.com/schema#constraintLessBody")
    public static interface ConstraintLessBody extends Body {

        @Iri("http://www.example.com/schema#constraintLessValue")
        public String getValue();

        @Iri("http://www.example.com/schema#constraintLessValue")
        public void setValue(String value);
    }
}
