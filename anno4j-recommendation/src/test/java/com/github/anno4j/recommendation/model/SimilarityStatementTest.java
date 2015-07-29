package com.github.anno4j.recommendation.model;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.impl.ResourceObject;
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

    private ObjectConnection connection;

    @Before
    public void setUp() throws Exception {
        this.connection = Anno4j.getInstance().getObjectRepository().getConnection();
    }

    @After
    public void tearDown() throws Exception {
        connection.close();
    }

    @Test
    public void testSimilarityStatement() throws Exception {

        ResourceObject subject = new ResourceObject(SOME_PAGE + "subject");
        ResourceObject object = new ResourceObject(SOME_PAGE + "object");
        double similarity = 1.0;

        // Create the statement
        SimilarityStatement statement = new SimilarityStatement(subject, object, similarity);

        // Connect the statement with subject and predicate
        statement.setSubject(subject);
        statement.setObject(object);

        // Negative test, query for all SimilarityStatements
        List<SimilarityStatement> result = connection.getObjects(SimilarityStatement.class).asList();

        assertEquals(0, result.size());

        // Persist annotation
        connection.addObject(statement);

        // Query object
        result = connection.getObjects(SimilarityStatement.class).asList();

        assertEquals(1, result.size());

        SimilarityStatement resultObject = result.get(0);

        // Tests
        assertEquals(statement.getResource(), resultObject.getResource());

        assertEquals(statement.getSubject().getResource(), resultObject.getSubject().getResource());
    }
}
