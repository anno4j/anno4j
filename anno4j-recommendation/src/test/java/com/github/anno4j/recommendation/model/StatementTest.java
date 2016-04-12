package com.github.anno4j.recommendation.model;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.recommendation.RecommendationTestSetup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test suite for the {@link com.github.anno4j.recommendation.model.Statement}.
 *
 * A simple statement is set up, then persisted and queried.
 */
public class StatementTest extends RecommendationTestSetup {

    private final static String SOME_PAGE = "http://example.org/";

    @Test
    public void testStatement() throws Exception {
        // Create the statement
        Statement statement = anno4j.createObject(Statement.class);

        // Create random subject and object
        ResourceObject subject = anno4j.createObject(ResourceObject.class);
        subject.setResourceAsString(SOME_PAGE + "subject");
        ResourceObject object = anno4j.createObject(ResourceObject.class);
        object.setResourceAsString(SOME_PAGE + "object");

        // Connect the statement with subject and predicate
        statement.setSubject(subject);
        statement.setObject(object);

        // Set the statement's predicate
        statement.setPredicateAsString(SOME_PAGE + "predicate");

        // Persist annotation
        anno4j.getObjectRepository().getConnection().addObject(statement);

        // Query object
        List<Statement> result = anno4j.getObjectRepository().getConnection().getObjects(Statement.class).asList();

        assertEquals(1, result.size());

        Statement resultObject = result.get(0);

        // Tests
        assertEquals(statement.getResource(), resultObject.getResource());

        assertEquals(statement.getSubject().getResource(), resultObject.getSubject().getResource());
    }

    @Override
    protected void persistTestData() throws RepositoryException, InstantiationException, IllegalAccessException {}
}
