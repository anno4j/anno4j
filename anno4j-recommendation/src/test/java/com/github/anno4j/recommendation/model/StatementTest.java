package com.github.anno4j.recommendation.model;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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
public class StatementTest {

    public final static String SOME_PAGE = "http://example.org/";

    private ObjectConnection connection;

    @Before
    public void setUp() throws Exception {
        SailRepository repository = new SailRepository(new MemoryStore());
        repository.initialize();
        Anno4j.getInstance().setRepository(repository);
        this.connection = Anno4j.getInstance().getObjectRepository().getConnection();
    }

    @After
    public void tearDown() throws Exception {
        this.connection.close();
    }

    @Test
    public void testStatement() throws Exception {
        // Create the statement
        Statement statement = new Statement();

        // Create random subject and object
        ResourceObject subject = new ResourceObject(SOME_PAGE + "subject");
        ResourceObject object = new ResourceObject(SOME_PAGE + "object");

        // Connect the statement with subject and predicate
        statement.setSubject(subject);
        statement.setObject(object);

        // Set the statement's predicate
        statement.setPredicateAsString(SOME_PAGE + "predicate");

        // Persist annotation
        connection.addObject(statement);

        // Query object
        List<Statement> result = connection.getObjects(Statement.class).asList();

        assertEquals(1, result.size());

        Statement resultObject = result.get(0);

        // Tests
        assertEquals(statement.getResource(), resultObject.getResource());

        assertEquals(statement.getSubject().getResource(), resultObject.getSubject().getResource());
    }
}
