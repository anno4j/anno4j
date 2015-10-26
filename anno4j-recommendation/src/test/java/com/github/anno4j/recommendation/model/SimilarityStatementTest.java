package com.github.anno4j.recommendation.model;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.querying.QueryService;
import com.github.anno4j.recommendation.ontologies.ANNO4JREC;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.repository.Repository;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Suite to test the {@link com.github.anno4j.recommendation.model.SimilarityStatement}
 */
public class SimilarityStatementTest {

    public final static String SOME_PAGE = "http://example.org/";

    private Anno4j anno4j;
    private QueryService queryService;

    @Before
    public void setUp() throws Exception {
        SailRepository repository = new SailRepository(new MemoryStore());
        repository.initialize();

        anno4j = new Anno4j();

        anno4j.setRepository(repository);
        queryService = anno4j.createQueryService();
        queryService.addPrefix(ANNO4JREC.PREFIX, ANNO4JREC.NS);
    }

    @Test
    public void testSimilarityStatement() throws Exception {
        ResourceObject subject = anno4j.createObject(ResourceObject.class);
        subject.setResourceAsString(SOME_PAGE + "subject");
        ResourceObject object = anno4j.createObject(ResourceObject.class);
        object.setResourceAsString(SOME_PAGE + "object");
        double similarity = 1.0;

        // Create an annotation
        Annotation annotation = anno4j.createObject(Annotation.class);

        // Create the statement
        SimilarityStatement statement = anno4j.createObject(SimilarityStatement.class);
        statement.setSubject(subject);
        statement.setObject(object);
        statement.setSimilarity(similarity);

        // Connect the statement with subject and predicate
        statement.setSubject(subject);
        statement.setObject(object);

        // Conntect annotation and body
        annotation.setBody(statement);

        // Negative test, query for all SimilarityStatements
        List<Annotation> result = queryService.addCriteria("oa:hasBody[is-a arec:SimilarityStatement]").execute();

        assertEquals(0, result.size());

        // Persist annotation
        anno4j.createPersistenceService().persistAnnotation(annotation);

        // Query object
        result = queryService.execute();

        assertEquals(1, result.size());

//        SimilarityStatement resultObject = result.get(0);
        Annotation resultObject = result.get(0);
        SimilarityStatement resultStatement = (SimilarityStatement) resultObject.getBody();

        // Tests
        assertEquals(statement.getResource(), resultStatement.getResource());

        assertEquals(statement.getSubject().getResource(), resultStatement.getSubject().getResource());
    }
}
