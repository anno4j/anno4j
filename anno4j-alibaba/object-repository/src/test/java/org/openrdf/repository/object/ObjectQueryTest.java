package org.openrdf.repository.object;

import junit.framework.Test;

import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.object.base.ObjectRepositoryTestCase;
import org.openrdf.repository.object.concepts.Person;

public class ObjectQueryTest extends ObjectRepositoryTestCase {

	private static final String PREFIX = "PREFIX foaf: <urn:foaf:>\n";

	public static Test suite() throws Exception {
		return ObjectRepositoryTestCase.suite(ObjectQueryTest.class);
	}

	private static final String QUERY_PERSON_SMITH = PREFIX
			+ "SELECT ?person WHERE { ?person foaf:family_name \"Smith\" }";

	private static final String QUERY_PERSON_NAME_SMITH = PREFIX
			+ "SELECT ?person ?name WHERE { ?person foaf:family_name \"Smith\" ; foaf:name ?name }";

	private static final String QUERY_PERSON_NAME_GENDER_SMITH = PREFIX
			+ "SELECT ?person ?name ?gender WHERE { ?person foaf:family_name \"Smith\" ; foaf:name ?name OPTIONAL { ?person foaf:gender ?gender } }";

	private static final String QUERY_NAME_SMITH = PREFIX
			+ "SELECT ?name WHERE { ?person foaf:family_name \"Smith\" ; foaf:name ?name }";

	private static final String QUERY_FRIENDS_SMITH = PREFIX
			+ "SELECT ?friend WHERE { ?person foaf:family_name \"Smith\" OPTIONAL { ?person foaf:knows ?friend } }";

	public void testBeanQuery() throws Exception {
		ObjectQuery query = con.prepareObjectQuery(QUERY_PERSON_SMITH);
		int count = 0;
		for (Object bean : query.evaluate().asList()) {
			Person person = (Person) bean;
			count++;
			assertTrue(person.getFoafNames().contains("Bob")
					|| person.getFoafNames().contains("John"));
		}
		assertEquals(2, count);
	}

	public void testOptionalBeanQuery() throws Exception {
		ObjectQuery query = con.prepareObjectQuery(QUERY_FRIENDS_SMITH);
		assertTrue(query.evaluate().asList().isEmpty());
	}

	public void testTupleQuery() throws Exception {
		ObjectQuery query = con.prepareObjectQuery(QUERY_PERSON_NAME_SMITH);
		int count = 0;
		for (Object row : query.evaluate().asList()) {
			Person person = (Person) ((Object[]) row)[0];
			String name = (String) ((Object[]) row)[1];
			count++;
			assertTrue(person.getFoafNames().contains("Bob")
					|| person.getFoafNames().contains("John"));
			assertTrue(name.equals("Bob") || name.equals("John"));
		}
		assertEquals(2, count);
	}

	public void testTupleOptionalQuery() throws Exception {
		ObjectQuery query = con.prepareObjectQuery(QUERY_PERSON_NAME_GENDER_SMITH);
		int count = 0;
		for (Object row : query.evaluate().asList()) {
			Person person = (Person) ((Object[]) row)[0];
			String name = (String) ((Object[]) row)[1];
			String gender = (String) ((Object[]) row)[2];
			count++;
			assertTrue(person.getFoafNames().contains("Bob")
					|| person.getFoafNames().contains("John"));
			assertTrue(name.equals("Bob") || name.equals("John"));
			assertNull(gender);
		}
		assertEquals(2, count);
	}

	public void testLiteralQuery() throws Exception {
		ObjectQuery query = con.prepareObjectQuery(QUERY_NAME_SMITH);
		int count = 0;
		for (Object result : query.evaluate().asList()) {
			String name = (String) result;
			count++;
			assertTrue(name.equals("Bob") || name.equals("John"));
		}
		assertEquals(2, count);
	}

	public void testResourceQuery() throws Exception {
		ObjectQuery query = con.prepareObjectQuery(QUERY_PERSON_SMITH);
		int count = 0;
		for (Object bean : query.evaluate().asList()) {
			Person person = (Person) bean;
			count++;
			assertTrue(person.getFoafNames().contains("Bob")
					|| person.getFoafNames().contains("John"));
		}
		assertEquals(2, count);
	}

	public void testTupleQueryBinding() throws Exception {
		Person jamie = con.addDesignation(con.getObjectFactory().createObject(),
				Person.class);
		jamie.getFoafNames().add("Jamie");
		jamie.getFoafFamily_names().add("Leigh");
		String q = PREFIX + "Select ?name where { ?person foaf:name ?name }";
		TupleQuery query = con.prepareTupleQuery(q);
		query.setBinding("person", ((RDFObject)jamie).getResource());
		TupleQueryResult result = query.evaluate();
		assertTrue(result.hasNext());
		assertEquals("Jamie", result.next().getValue("name").stringValue());
		result.close();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Person bob = con.addDesignation(con.getObjectFactory().createObject(),
				Person.class);
		bob.getFoafNames().add("Bob");
		bob.getFoafFamily_names().add("Smith");
		Person john = con.addDesignation(con.getObjectFactory().createObject(),
				Person.class);
		john.getFoafNames().add("John");
		john.getFoafFamily_names().add("Smith");
	}
}
