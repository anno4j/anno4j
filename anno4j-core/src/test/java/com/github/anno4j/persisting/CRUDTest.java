package com.github.anno4j.persisting;

import com.github.anno4j.Anno4j;
import com.github.anno4j.Transaction;
import com.github.anno4j.model.Agent;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Audience;
import com.github.anno4j.model.Body;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.model.impl.agent.Person;
import com.github.anno4j.model.namespaces.*;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Basic test for the CRUD operations of Anno4j - Create, Read, Update, Delete.
 * Thus these tests cover vary basic behaviour of Anno4j under different assumptions.
 */
public class CRUDTest {

    /**
     * Returns all statements that are present in any context of a repository.
     * The returned set does not contain any inferred triples.
     * @param connection A connection to the repository to query.
     * @return Returns the set of all triples present in the connected repository.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    private Collection<Statement> getStatements(RepositoryConnection connection) throws RepositoryException {
        return getStatements(connection, null, null, null);
    }

    /**
     * Returns all statements that are present in any context of a repository having the specified subject, predicate and/or object.
     * @param connection A connection to the repository to query.
     * @param subject The subject the returned triples should have or null for any subject.
     * @param predicate The predicate the returned triples should have or null for any predicate.
     * @param object The object the returned triples should have or null for any object.
     * @return Returns the set of all triples present in the repository having the desired spo-structure.
     * @throws RepositoryException Thrown if an error occurs while querying the repository.
     */
    private Collection<Statement> getStatements(RepositoryConnection connection, Resource subject, URI predicate, Value object) throws RepositoryException {
        // Query the repository:
        RepositoryResult<Statement> result = connection.getStatements(subject, predicate, object, false);

        // Fetch all statements from the result:
        Collection<Statement> statements = new HashSet<>();
        while (result.hasNext()) {
            statements.add(result.next());
        }
        return statements;
    }


    /**
     * Tests basic object creation and persistence of triples.
     */
    @Test
    public void testCreate() throws Exception {
        // Create an Anno4j instance and get its repository connection for direct triple access:
        Anno4j anno4j = new Anno4j(new SailRepository(new MemoryStore()), null, false);
        RepositoryConnection repoConnection = anno4j.getRepository().getConnection();

        // Test simple object creation:
        Person p = anno4j.createObject(Person.class, (Resource) new URIImpl("urn:anno4j_test:p1"));
        p.setMbox("alice@example.org");

        // Two statments (rdf:type, foaf:mbox) should be created:
        Collection<Statement> statements = getStatements(repoConnection);
        assertEquals(2, statements.size());
        assertTrue(statements.contains(new StatementImpl(new URIImpl("urn:anno4j_test:p1"),
                                                         new URIImpl(RDF.TYPE),
                                                         new URIImpl(FOAF.PERSON))));
        assertTrue(statements.contains(new StatementImpl(new URIImpl("urn:anno4j_test:p1"),
                new URIImpl(FOAF.MBOX),
                new LiteralImpl("alice@example.org"))));
    }

    /**
     * Tests re-creation of already created objects in the same context.
     */
    @Test
    public void testCreateExisting() throws Exception {
        // Create an Anno4j instance and get its object connection:
        Anno4j anno4j = new Anno4j(new SailRepository(new MemoryStore()), null, false);
        ObjectConnection connection = anno4j.getObjectRepository().getConnection();

        // Create an object:
        Person a = anno4j.createObject(Person.class, (Resource) new URIImpl("urn:anno4j_test:p1"));
        a.setMbox("alice@example.org");

        // Create another object with the same IRI:
        Person b = anno4j.createObject(Person.class, (Resource) new URIImpl("urn:anno4j_test:p1"));

        // The new object should have the same value for the property foaf:mbox:
        assertEquals("alice@example.org", b.getMbox());

        // The objects should be comparable:
        assertTrue(a.equals(b) && b.equals(a));

        // Modification done to the second object should persist to the first (after refreshing):
        b.setMbox("bob@example.org");
        connection.refresh(a);
        assertEquals("bob@example.org", a.getMbox());
    }

    /**
     * Tests re-creation of already created objects in a different context.
     */
    @Test
    public void testCreateExistingOtherContext() throws Exception {
        // Create an Anno4j instance and get its object connection:
        Anno4j anno4j = new Anno4j(new SailRepository(new MemoryStore()), null, false);
        ObjectConnection connection = anno4j.getObjectRepository().getConnection();

        // Create an object:
        Person a = anno4j.createObject(Person.class, (Resource) new URIImpl("urn:anno4j_test:p1"));
        a.setMbox("alice@example.org");

        // Create another object with the same IRI:
        Person b = anno4j.createObject(Person.class, new URIImpl("urn:anno4j_test:context1"), new URIImpl("urn:anno4j_test:p1"));

        // The new object should NOT have a value for the property foaf:mbox:
        assertNull(b.getMbox());

        // The objects should be comparable (because the resource identified by an IRI is always the same):
        assertTrue(a.equals(b) && b.equals(a));

        // Modification done to the second object should NOT persist to the first (after refreshing):
        b.setMbox("bob@example.org");
        connection.refresh(a);
        assertEquals("alice@example.org", a.getMbox());
    }

    /**
     * Tests reading objects and their property values from the repository.
     */
    @Test
    public void testRead() throws Exception {
        // Create an Anno4j instance and get its repository connection for direct triple access:
        Anno4j anno4j = new Anno4j(new SailRepository(new MemoryStore()), null, false);
        RepositoryConnection repoConnection = anno4j.getRepository().getConnection();

        // Add some triples to the repository:
        repoConnection.add(new StatementImpl(new URIImpl("urn:anno4j_test:p1"),
                                             new URIImpl(RDF.TYPE),
                                             new URIImpl(FOAF.PERSON)));
        repoConnection.add(new StatementImpl(new URIImpl("urn:anno4j_test:p1"),
                new URIImpl(FOAF.MBOX),
                new LiteralImpl("alice@example.org")));

        Person p = anno4j.findByID(Person.class, "urn:anno4j_test:p1");
        assertNotNull(p);
        assertEquals("alice@example.org", p.getMbox());
    }

    /**
     * Tests behaviour of Anno4j when the type of resources in the repository are different from the ones in the Java domain model.
     */
    @Test
    public void testReadWrongType() throws Exception {
        // Create an Anno4j instance and get its repository connection for direct triple access:
        Anno4j anno4j = new Anno4j(new SailRepository(new MemoryStore()), null, false);
        RepositoryConnection repoConnection = anno4j.getRepository().getConnection();

        // Add some triples to the repository:
        repoConnection.add(new StatementImpl(new URIImpl("urn:anno4j_test:p1"),
                new URIImpl(RDF.TYPE),
                new URIImpl("urn:anno4j_test:some_other_class")));
        repoConnection.add(new StatementImpl(new URIImpl("urn:anno4j_test:a2"),
                new URIImpl(RDF.TYPE),
                new URIImpl(OADM.ANNOTATION)));
        repoConnection.add(new StatementImpl(new URIImpl("urn:anno4j_test:a2"),
                new URIImpl(OADM.HAS_BODY),
                new LiteralImpl("I'm not an IRI.")));

        // Test retrieving a resource with a different type:
        Person p1 = anno4j.findByID(Person.class, "urn:anno4j_test:p1");
        assertNull(p1);

        // Test retrieving a property value with wrong type:
        boolean exceptionThrown = false;
        try {
            Annotation a2 = anno4j.findByID(Annotation.class, "urn:anno4j_test:a2");
            Set<Body> bodies = a2.getBodies();
            Body body = bodies.iterator().next();
            body.getResourceAsString(); // Prevent code elimination
        } catch (ClassCastException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    /**
     * Tests reading and modifying the same resource in different contexts.
     */
    @Test
    public void testReadUpdateOtherContext() throws Exception {
        // Create an Anno4j instance and get its repository connection for direct triple access:
        Anno4j anno4j = new Anno4j(new SailRepository(new MemoryStore()), null, false);
        RepositoryConnection repoConnection = anno4j.getRepository().getConnection();

        // Add some triples to the repository:
        repoConnection.add(new StatementImpl(new URIImpl("urn:anno4j_test:p1"),
                new URIImpl(RDF.TYPE),
                new URIImpl(FOAF.PERSON)));
        repoConnection.add(new StatementImpl(new URIImpl("urn:anno4j_test:p1"),
                new URIImpl(FOAF.MBOX),
                new LiteralImpl("alice@example.org")));
        repoConnection.add(new StatementImpl(new URIImpl("urn:anno4j_test:p1"),
                new URIImpl(RDF.TYPE),
                new URIImpl(FOAF.PERSON)), new URIImpl("urn:anno4j_test:context1"));
        repoConnection.add(new StatementImpl(new URIImpl("urn:anno4j_test:p1"),
                new URIImpl(FOAF.MBOX),
                new LiteralImpl("bob@example.org")), new URIImpl("urn:anno4j_test:context1"));

        Person a = anno4j.findByID(Person.class, "urn:anno4j_test:p1");

        // Use a transaction to access objects from the other context:
        Transaction transaction = anno4j.createTransaction(new URIImpl("urn:anno4j_test:context1"));
        transaction.begin();
        Person b = transaction.findByID(Person.class, "urn:anno4j_test:p1");
        assertEquals("alice@example.org", a.getMbox());
        assertEquals("bob@example.org", b.getMbox());
        a.setMbox("charlie@example.org");
        assertEquals("charlie@example.org", a.getMbox());
        assertEquals("bob@example.org", b.getMbox());
    }

    /**
     * Tests reading of resources that do not have a type assigned in the repository.
     */
    @Test
    public void testReadNoType() throws Exception {
        // Create an Anno4j instance and get its repository connection for direct triple access:
        Anno4j anno4j = new Anno4j(new SailRepository(new MemoryStore()), null, false);
        RepositoryConnection repoConnection = anno4j.getRepository().getConnection();

        // Resource urn:anno4j_test:p1 is present but does not have a rdf:type
        repoConnection.add(new StatementImpl(new URIImpl("urn:anno4j_test:p1"),
                            new URIImpl(FOAF.MBOX),
                            new LiteralImpl("alice@example.org")));

        // Try retrieving the resource with a specific type:
        Person p = anno4j.findByID(Person.class, "urn:anno4j_test:p1");
        assertNull(p);
    }

    /**
     * Tests retrieving resources with a more general type than specified in the repository.
     */
    @Test
    public void testReadGeneralType() throws Exception {
        // Create an Anno4j instance and get its repository connection for direct triple access:
        Anno4j anno4j = new Anno4j(new SailRepository(new MemoryStore()), null, false);
        RepositoryConnection repoConnection = anno4j.getRepository().getConnection();

        // Add some triples to the repository:
        repoConnection.add(new StatementImpl(new URIImpl("urn:anno4j_test:p1"),
                                            new URIImpl(RDF.TYPE),
                                            new URIImpl(FOAF.PERSON)));

        ResourceObject p = anno4j.findByID(Person.class, "urn:anno4j_test:p1");
        assertNotNull(p);
        p = anno4j.findByID(Agent.class, "urn:anno4j_test:p1");
        assertNotNull(p);
        p = anno4j.findByID(ResourceObject.class, "urn:anno4j_test:p1");
        assertNotNull(p);
    }

    /**
     * Tests the triples contained in the repository after updating property values via resource objects.
     */
    @Test
    public void testUpdate() throws Exception {
        // Create an Anno4j instance and get its repository connection for direct triple access:
        Anno4j anno4j = new Anno4j(new SailRepository(new MemoryStore()), null, false);
        RepositoryConnection repoConnection = anno4j.getRepository().getConnection();

        // Add some triples to the repository:
        repoConnection.add(new StatementImpl(new URIImpl("urn:anno4j_test:p1"),
                new URIImpl(RDF.TYPE),
                new URIImpl(FOAF.PERSON)));
        repoConnection.add(new StatementImpl(new URIImpl("urn:anno4j_test:p1"),
                new URIImpl(FOAF.MBOX),
                new LiteralImpl("alice@example.org")));
        repoConnection.add(new StatementImpl(new URIImpl("urn:anno4j_test:a1"),
                new URIImpl(RDF.TYPE),
                new URIImpl(OADM.ANNOTATION)));
        repoConnection.add(new StatementImpl(new URIImpl("urn:anno4j_test:a1"),
                new URIImpl(OADM.BODY_TEXT),
                new LiteralImpl("Text 1")));
        repoConnection.add(new StatementImpl(new URIImpl("urn:anno4j_test:a1"),
                new URIImpl(OADM.BODY_TEXT),
                new LiteralImpl("Text 2")));

        // Modified single-valued property of p1:
        Person p1 = anno4j.findByID(Person.class, "urn:anno4j_test:p1");
        p1.setMbox("bob@example.org");
        // Get all triples with p1 as subject. There should be exavtly two (rdf:type and new foaf:mbox):
        Collection<Statement> p1Statements = getStatements(repoConnection, new URIImpl("urn:anno4j_test:p1"), null, null);
        assertEquals(2, p1Statements.size());
        assertTrue(p1Statements.contains(new StatementImpl(new URIImpl("urn:anno4j_test:p1"),
                                                           new URIImpl(RDF.TYPE),
                                                           new URIImpl(FOAF.PERSON))));
        assertTrue(p1Statements.contains(new StatementImpl(new URIImpl("urn:anno4j_test:p1"),
                new URIImpl(FOAF.MBOX),
                new LiteralImpl("bob@example.org"))));

        // Modify multi-valued property of a1:
        Annotation a1 = anno4j.findByID(Annotation.class, "urn:anno4j_test:a1");
        a1.setBodyTexts(Sets.newHashSet("Text 1"));
        // Get the triples with a1 as subject. There should be only two now (rdf:type and the oadm:body_text just set):
        Collection<Statement> a1Statements = getStatements(repoConnection, new URIImpl("urn:anno4j_test:a1"), null, null);
        assertEquals(2, a1Statements.size());
        assertTrue(a1Statements.contains(new StatementImpl(new URIImpl("urn:anno4j_test:a1"),
                                                            new URIImpl(RDF.TYPE),
                                                            new URIImpl(OADM.ANNOTATION))));
        assertTrue(a1Statements.contains(new StatementImpl(new URIImpl("urn:anno4j_test:a1"),
                                                            new URIImpl(OADM.BODY_TEXT),
                                                            new LiteralImpl("Text 1"))));
    }

    /**
     * Tests propagation of updates across different objects for the same resource.
     */
    @Test
    public void testUpgradePropagation() throws Exception {
        // Create an Anno4j instance and get its object connection:
        Anno4j anno4j = new Anno4j(new SailRepository(new MemoryStore()), null, false);
        ObjectConnection connection = anno4j.getObjectRepository().getConnection();

        // Get two objects for the same resource:
        Annotation a = anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:anno4j_test:a1"));
        Annotation b = anno4j.findByID(Annotation.class, "urn:anno4j_test:a1");

        // Test that both objects have the same values after setting at one:
        a.setBodyTexts(Sets.newHashSet("a"));
        assertEquals(Sets.newHashSet("a"), a.getBodyTexts());
        assertEquals(Sets.newHashSet("a"), b.getBodyTexts());

        // Refresh objects:
        connection.refresh(a);
        connection.refresh(b);

        // Test other way round (after values have been cached):
        b.setBodyTexts(Sets.newHashSet("a", "b"));
        assertEquals(Sets.newHashSet("a", "b"), new HashSet<>(a.getBodyTexts()));
        assertEquals(Sets.newHashSet("a", "b"), new HashSet<>(b.getBodyTexts()));
    }

    /**
     * Tests deletion of resource objects and its effect on the repository.
     */
    @Test
    public void testDelete() throws Exception {
        // Create an Anno4j instance and get its repository connection for direct triple access:
        Anno4j anno4j = new Anno4j(new SailRepository(new MemoryStore()), null, false);
        RepositoryConnection repoConnection = anno4j.getRepository().getConnection();

        // Add some triples to the repository:
        /*
        Persisted triples:
        :p1   a                       foaf:Person ;
              foaf:mbox               "alice@example.org" .
        :a1   a                       oadm:Annotation ;
              oadm:bodyText           "Text 1", "Text 2" ;
              schema:audience         :b1, :b2 .
        :b1   a                       schema:Audience .

         */
        repoConnection.add(new StatementImpl(new URIImpl("urn:anno4j_test:p1"),
                                            new URIImpl(RDF.TYPE),
                                            new URIImpl(FOAF.PERSON)));
        repoConnection.add(new StatementImpl(new URIImpl("urn:anno4j_test:p1"),
                                            new URIImpl(FOAF.MBOX),
                                            new LiteralImpl("alice@example.org")));
        repoConnection.add(new StatementImpl(new URIImpl("urn:anno4j_test:a1"),
                                            new URIImpl(RDF.TYPE),
                                            new URIImpl(OADM.ANNOTATION)));
        repoConnection.add(new StatementImpl(new URIImpl("urn:anno4j_test:a1"),
                                            new URIImpl(OADM.BODY_TEXT),
                                            new LiteralImpl("Text 1")));
        repoConnection.add(new StatementImpl(new URIImpl("urn:anno4j_test:a1"),
                                            new URIImpl(OADM.BODY_TEXT),
                                            new LiteralImpl("Text 2")));
        repoConnection.add(new StatementImpl(new URIImpl("urn:anno4j_test:a1"),
                                            new URIImpl(SCHEMA.AUDIENCE_RELATIONSHIP),
                                            new URIImpl("urn:anno4j_test:b1")));
        repoConnection.add(new StatementImpl(new URIImpl("urn:anno4j_test:a1"),
                                            new URIImpl(SCHEMA.AUDIENCE_RELATIONSHIP),
                                            new URIImpl("urn:anno4j_test:b2")));
        repoConnection.add(new StatementImpl(new URIImpl("urn:anno4j_test:b1"),
                                            new URIImpl(RDF.TYPE),
                                            new URIImpl(SCHEMA.AUDIENCE_CLASS)));


        // Test that all triples are removed:
        Person p1 = anno4j.findByID(Person.class, "urn:anno4j_test:p1");
        p1.delete();
        Collection<Statement> p1Statements = getStatements(repoConnection, new URIImpl("urn:anno4j_test:p1"), null, null);
        assertEquals(0, p1Statements.size());

        // Test that all triples are removed that have a1 as subject. b1 should be still retrievable:
        Annotation a1 = anno4j.findByID(Annotation.class, "urn:anno4j_test:a1");
        a1.delete();
        Collection<Statement> a1Statements = getStatements(repoConnection, new URIImpl("urn:anno4j_test:a1"), null, null);
        assertEquals(0, a1Statements.size());
        Audience b1 = anno4j.findByID(Audience.class, "urn:anno4j_test:b1");
        assertNotNull(b1);
        // After removing :b1 the repository should be empty. :b2 is removed because it didn't occur as a subject:
        b1.delete();
        Collection<Statement> allStatements = getStatements(repoConnection);
        assertEquals(0, allStatements.size());
    }
}
