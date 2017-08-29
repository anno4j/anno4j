package org.openrdf.repository.object;

import junit.framework.Test;

import org.openrdf.repository.object.base.ObjectRepositoryTestCase;
import org.openrdf.repository.object.concepts.Agent;
import org.openrdf.repository.object.concepts.Person;

public class FunctionalTest extends ObjectRepositoryTestCase {
	public static Test suite() throws Exception {
		return ObjectRepositoryTestCase.suite(FunctionalTest.class);
	}

	public void testGender() throws Exception {
		Agent a = con.addDesignation(con.getObjectFactory().createObject(), Agent.class);
		a.setFoafGender("male");
		Object item = con.prepareObjectQuery("SELECT DISTINCT ?item WHERE {?item ?p ?o}").evaluate().singleResult();
		assertTrue(((Agent)item).getFoafGender().equals("male"));
	}

	public void testEagerGender() throws Exception {
		con.prepareUpdate("INSERT DATA { <urn:test:agent> a <urn:foaf:Agent>; <urn:foaf:gender> 'male'}").execute();
		Agent a = con.getObject(Agent.class, "urn:test:agent");
		con.prepareUpdate("DELETE { ?agent <urn:foaf:gender> 'male'} INSERT { ?agent <urn:foaf:gender> 'female'} WHERE {?agent <urn:foaf:gender> 'male'}").execute();
		assertEquals("male", a.getFoafGender());
	}

	public void testEagerPersonGender() throws Exception {
		con.prepareUpdate("INSERT DATA { <urn:test:mary> a <urn:foaf:Person>; <urn:foaf:knows> <urn:test:anne>; <urn:foaf:gender> 'female'}").execute();
		con.prepareUpdate("INSERT DATA { <urn:test:anne> a <urn:foaf:Person>; <urn:foaf:knows> <urn:test:mary>; <urn:foaf:gender> 'female'}").execute();
		Person mary = con.getObject(Person.class, "urn:test:mary");
		Person anne = mary.getFoafKnows().iterator().next();
		con.prepareUpdate(
				"DELETE { ?person <urn:foaf:gender> 'female'\n"
						+ "} INSERT { ?person <urn:foaf:gender> 'male'\n"
						+ "} WHERE {?person <urn:foaf:gender> 'female'}")
				.execute();
		assertEquals("female", anne.getFoafGender());
	}

	public void testCacheReadProperties() throws Exception {
		con.prepareUpdate("INSERT DATA { <urn:test:mary> a <urn:foaf:Person>; <urn:foaf:firstName> 'Mary'; <urn:foaf:knows> <urn:test:anne>; <urn:foaf:gender> 'female'}").execute();
		Person mary = con.getObject(Person.class, "urn:test:mary");
		String firstName = mary.getFoafFirstNames().iterator().next();
		con.prepareUpdate(
				"DELETE { ?person <urn:foaf:firstName> 'Mary'; <urn:foaf:gender> 'female'\n"
						+ "} INSERT { ?person <urn:foaf:firstName> 'Martin'; <urn:foaf:gender> 'male'\n"
						+ "} WHERE {?person <urn:foaf:firstName> 'Mary'; <urn:foaf:gender> 'female'}")
				.execute();
		assertEquals("female", mary.getFoafGender());
		assertEquals(firstName, mary.getFoafFirstNames().iterator().next());
	}

	public void testPropertyCycle() throws Exception {
		con.prepareUpdate("INSERT DATA { <urn:test:mary> a <urn:foaf:Person>; <urn:foaf:firstName> 'Mary'; <urn:foaf:knows> <urn:test:anne>; <urn:foaf:gender> 'female'}").execute();
		con.prepareUpdate("INSERT DATA { <urn:test:anne> a <urn:foaf:Person>; <urn:foaf:firstName> 'Anne'; <urn:foaf:knows> <urn:test:mary>; <urn:foaf:gender> 'female'}").execute();
		Person mary = con.getObject(Person.class, "urn:test:mary");
		String firstName = mary.getFoafFirstNames().iterator().next();
		Person anne = mary.getFoafKnows().iterator().next();
		con.prepareUpdate(
				"DELETE { ?person <urn:foaf:firstName> 'Mary'; <urn:foaf:gender> 'female'\n"
						+ "} INSERT { ?person <urn:foaf:firstName> 'Martin'; <urn:foaf:gender> 'male'\n"
						+ "} WHERE {?person <urn:foaf:firstName> 'Mary'; <urn:foaf:gender> 'female'}")
				.execute();
		Person mary2 = anne.getFoafKnows().iterator().next();
		assertEquals("male", mary2.getFoafGender()); // eager properties are reloaded
		assertEquals(firstName, mary2.getFoafFirstNames().iterator().next()); // other properties are still cached
		assertEquals(System.identityHashCode(mary), System.identityHashCode(mary2));
		Person martin = con.refresh(mary2);
		assertEquals(System.identityHashCode(mary), System.identityHashCode(martin));
		assertEquals("male", martin.getFoafGender());
		assertEquals("Martin", martin.getFoafFirstNames().iterator().next());
		assertEquals("Martin", mary.getFoafFirstNames().iterator().next());
		assertEquals("Martin", mary2.getFoafFirstNames().iterator().next());
	}

	public void testRefreshType() throws Exception {
		con.prepareUpdate("INSERT DATA { <urn:test:mary> a <urn:foaf:Person>; <urn:foaf:name> 'Mary'; <urn:foaf:knows> <urn:test:anne>; <urn:foaf:gender> 'female'}").execute();
		con.prepareUpdate("INSERT DATA { <urn:test:anne> a <urn:foaf:Person>; <urn:foaf:name> 'Anne'; <urn:foaf:knows> <urn:test:mary>; <urn:foaf:gender> 'female'}").execute();
		Person mary = con.getObject(Person.class, "urn:test:mary");
		assertEquals("Mary", mary.getFoafNames().iterator().next());
		Person anne = mary.getFoafKnows().iterator().next();
		Person mary2 = anne.getFoafKnows().iterator().next();
		con.prepareUpdate(
				"DELETE { ?person a <urn:foaf:Person>; <urn:foaf:name> 'Mary'; <urn:foaf:gender> 'female'\n"
						+ "} INSERT { ?person a <urn:foaf:Agent>; <urn:foaf:name> 'Mary Centre'\n"
						+ "} WHERE {?person a <urn:foaf:Person>; <urn:foaf:name> 'Mary'; <urn:foaf:gender> 'female'}")
				.execute();
		Agent centre = con.refresh(mary2);
		assertFalse(centre instanceof Person);
		assertEquals("Mary Centre", centre.getFoafNames().iterator().next());
	}

	@Override
	protected void setUp() throws Exception {
		config.addConcept(Agent.class);
		super.setUp();
	}
}
