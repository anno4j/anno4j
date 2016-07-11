package com.github.anno4j.model.impl;


import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Body;
import com.github.anno4j.model.Selector;
import com.github.anno4j.model.Target;
import com.github.anno4j.model.impl.targets.SpecificResource;
import com.github.anno4j.querying.QueryService;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.annotations.Iri;
import org.openrdf.model.Statement;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Class to test the delete functionality of the ResourceObject and all
 * its support underlying implementations, e.g. AnnotationSupport,
 * SpecificResourceSupport etc.
 *
 * @author andreaseisenkolb
 */
public class DeletionTest {

    private Anno4j anno4j;

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
    }

    @Test
    public void deletePlainAnnotation() throws RepositoryException, IllegalAccessException, InstantiationException, QueryEvaluationException, ParseException, MalformedQueryException {
        // Create test annotation
        Annotation annotation = anno4j.createObject(Annotation.class);
        annotation.setCreated("2015-01-28T12:00:00Z");
        annotation.setGenerated("2015-01-28T12:00:00Z");

        // query persisted object
        Annotation result = (Annotation) anno4j.createQueryService().execute().get(0);

        assertEquals(annotation.getResource().toString(), result.getResource().toString());

        // delete the annotation
        result.delete();

        // checking if there are no statements left
        RepositoryResult<Statement> statements = anno4j.getObjectRepository().getConnection().getStatements(null, null, null);
        assertEquals(false, statements.hasNext());

        // confirming the empty result set with the QueryService
        List<Annotation> emptyList = anno4j.createQueryService().execute();
        assertEquals(0, emptyList.size());
    }

    @Test
    public void deleteAnnotationWithBody() throws RepositoryException, IllegalAccessException, InstantiationException, ParseException, MalformedQueryException, QueryEvaluationException {
        // create test annotation
        Annotation annotation = anno4j.createObject(Annotation.class);
        annotation.setCreated("2015-01-28T12:00:00Z");
        annotation.setGenerated("2015-01-28T12:00:00Z");

        // create test body
        DeletionTestBody body = anno4j.createObject(DeletionTestBody.class);
        body.setValue("TestValue");

        annotation.setBody(body);

        // query persisted object
        Annotation result = anno4j.findByID(Annotation.class, annotation.getResourceAsString());
        assertEquals(annotation.getResource().toString(), result.getResource().toString());
        assertEquals(body.getValue(), ((DeletionTestBody) result.getBody()).getValue());

        // delete the annotation
        result.delete();

        // confirming the empty result set with the QueryService
        List<Annotation> emptyList = anno4j.createQueryService().execute();
        assertEquals(0, emptyList.size());

        // checking if there are no statements left (repository should be completely empty)
        RepositoryResult<Statement> statements = anno4j.getObjectRepository().getConnection().getStatements(null, null, null);
        assertEquals(false, statements.hasNext());
    }

    @Test
    public void deleteAnnotationWithBodyAndTargets() throws RepositoryException, IllegalAccessException, InstantiationException, ParseException, MalformedQueryException, QueryEvaluationException {
        // create test annotation
        Annotation annotation = anno4j.createObject(Annotation.class);
        annotation.setCreated("2015-01-28T12:00:00Z");
        annotation.setGenerated("2015-01-28T12:00:00Z");

        // create test body
        DeletionTestBody body = anno4j.createObject(DeletionTestBody.class);
        body.setValue("TestValue");

        // create test selector
        DeletionTestSelector selector = anno4j.createObject(DeletionTestSelector.class);
        selector.setValue("TestSelectorValue");

        SpecificResource specificResource = anno4j.createObject(SpecificResource.class);
        specificResource.setSelector(selector);

        annotation.setBody(body);
        annotation.addTarget(specificResource);

        // query persisted objects
        Annotation result = (Annotation) anno4j.createQueryService().execute().get(0);
        assertEquals(annotation.getResource().toString(), result.getResource().toString());
        assertEquals(body.getValue(), ((DeletionTestBody) result.getBody()).getValue());

        SpecificResource specRes = (SpecificResource) result.getTarget().toArray()[0];
        DeletionTestSelector deletionTestSelector = (DeletionTestSelector) specRes.getSelector();
        assertEquals(selector.getValue(), deletionTestSelector.getValue());

        // delete the annotation
        result.delete();

        // confirming the empty result set with the QueryService
        List<Annotation> emptyList = anno4j.createQueryService().execute();
        assertEquals(0, emptyList.size());

        // checking if there are no statements left (repository should be completely empty)
        RepositoryResult<Statement> statements = anno4j.getObjectRepository().getConnection().getStatements(null, null, null);
        assertEquals(false, statements.hasNext());
    }

    @Test
    public void deleteBodyOfAnnotation() throws RepositoryException, IllegalAccessException, InstantiationException, ParseException, MalformedQueryException, QueryEvaluationException {
        // create test annotation
        Annotation annotation = anno4j.createObject(Annotation.class);
        annotation.setCreated("2015-01-28T12:00:00Z");
        annotation.setGenerated("2015-01-28T12:00:00Z");

        // create test body
        DeletionTestBody body = anno4j.createObject(DeletionTestBody.class);
        body.setValue("TestValue");

        annotation.setBody(body);

        // query persisted objects
        Annotation result = (Annotation) anno4j.createQueryService().execute().get(0);

        // deleting only the body
        result.getBody().delete();

        // checking if the persisted annotation still has the body
        Annotation annotationWithoutBody = (Annotation) anno4j.createQueryService().execute().get(0);
        assertEquals(null, annotationWithoutBody.getBody());
    }

    @Test
    public void deleteTargetOfAnnotation() throws RepositoryException, IllegalAccessException, InstantiationException, ParseException, MalformedQueryException, QueryEvaluationException {
        // create test annotation
        Annotation annotation = anno4j.createObject(Annotation.class);
        annotation.setCreated("2015-01-28T12:00:00Z");
        annotation.setGenerated("2015-01-28T12:00:00Z");

        // create test selector
        DeletionTestSelector selector = anno4j.createObject(DeletionTestSelector.class);
        selector.setValue("TestSelectorValue");

        SpecificResource specificResource = anno4j.createObject(SpecificResource.class);
        specificResource.setSelector(selector);

        annotation.addTarget(specificResource);

        // query persisted objects
        Annotation result = anno4j.findByID(Annotation.class, annotation.getResourceAsString());
        Set<Target> targetSet = result.getTarget();

        // removing the targets one by one
        for (Target target : targetSet) {
            target.delete();
        }

        // finally testing if the targets of the persisted annotation is still existent
        Annotation annotationWithoutTarget = anno4j.findByID(Annotation.class, annotation.getResourceAsString());
        assertEquals(0, annotationWithoutTarget.getTarget().size());
    }

    /**
     * Body interface for testing purpose
     */
    @Iri("http://www.example.com/schema#deletionTestBody")
    public static interface DeletionTestBody extends Body {
        @Iri("http://www.example.com/schema#deletionTestBodyValue")
        String getValue();

        @Iri("http://www.example.com/schema#deletionTestBodyValue")
        void setValue(String value);
    }

    /**
     * Selector interface for testing purpose
     */
    @Iri("http://www.example.com/schema#deletionTestSelector")
    public static interface DeletionTestSelector extends Selector {
        @Iri("http://www.example.com/schema#deletionSelectorValue")
        String getValue();

        @Iri("http://www.example.com/schema#deletionSelectorValue")
        String setValue(String value);
    }
}
