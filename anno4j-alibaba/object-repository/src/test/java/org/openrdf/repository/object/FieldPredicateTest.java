package org.openrdf.repository.object;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Test;

import org.openrdf.annotations.Iri;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.object.base.ObjectRepositoryTestCase;

public class FieldPredicateTest extends ObjectRepositoryTestCase {

	public static Test suite() throws Exception {
		return ObjectRepositoryTestCase.suite(FieldPredicateTest.class);
	}

	@Iri("urn:test:Party")
	public static abstract class Party {
		@Iri("urn:test:label")
		private String label;
		@Iri("urn:test:number")
		private int surname;

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public int getNumber() {
			return surname;
		}

		public void setNumber(int count) {
			this.surname = count;
		}

		public abstract String getType();

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((label == null) ? 0 : label.hashCode());
			result = prime * result + surname;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Party other = (Party) obj;
			if (label == null) {
				if (other.label != null)
					return false;
			} else if (!label.equals(other.label))
				return false;
			if (surname != other.surname)
				return false;
			return true;
		}
	}

	// urn:test:Person
	public static class Person extends Party {
		// urn:test:surname
		private String surname;
		// urn:test:givenNames
		protected Set<String> givenNames = new HashSet<String>();
		public Person spouse;

		public String getSurname() {
			return surname;
		}

		public void setSurname(String surname) {
			this.surname = surname;
			super.setLabel(toString());
		}

		public Set<String> getGivenNames() {
			return givenNames;
		}

		public void setGivenNames(Set<String> givenNames) {
			this.givenNames = givenNames;
			super.setLabel(toString());
		}

		// urn:test:spouse
		public Person getSpouse() {
			return spouse;
		}

		public void setSpouse(Person spouse) {
			this.spouse = spouse;
		}

		public String toString() {
			return givenNames + " " + surname;
		}

		public String getType() {
			return "Person";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result
					+ ((givenNames == null) ? 0 : givenNames.hashCode());
			result = prime * result
					+ ((spouse == null) ? 0 : spouse.hashCode());
			result = prime * result
					+ ((surname == null) ? 0 : surname.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			Person other = (Person) obj;
			if (givenNames == null) {
				if (other.givenNames != null)
					return false;
			} else if (!givenNames.equals(other.givenNames))
				return false;
			if (spouse == null) {
				if (other.spouse != null)
					return false;
			} else if (!spouse.equals(other.spouse))
				return false;
			if (surname == null) {
				if (other.surname != null)
					return false;
			} else if (!surname.equals(other.surname))
				return false;
			return true;
		}
	}

	@Iri("urn:test:Company")
	public static class Company extends Party {
		@Iri("urn:test:name")
		private String name;
		@Iri("urn:test:employees")
		Set<Person> employees = new HashSet<Person>();
		@Iri("urn:test:count")
		protected int count;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
			super.setLabel(name);
		}

		public boolean isNamePresent() {
			return name != null;
		}

		public Set<Person> getEmployees() {
			return Collections.unmodifiableSet(employees);
		}

		public void addEmployee(Person employee) {
			employees.add(employee);
			count++;
		}

		public boolean isEmployed(Person employee) {
			return employees.contains(employee);
		}

		public int getNumberOfEmployees() {
			return count;
		}
	
		public Person findByGivenName(String given) {
			Person found = null;
			for (Person person : employees) {
				if (person.getGivenNames().contains(given)) {
					found = person;
				}
			}
			return found;
		}
	
		public Person findByGivenName(String given, String alternative) {
			Person found = null;
			for (Person person : employees) {
				Set<String> names = person.getGivenNames();
				if (names.contains(given) || names.contains(alternative)) {
					found = person;
				}
			}
			return found;
		}

		public String getType() {
			return "Company";
		}
	}

	public void testReadField() throws Exception {
		Company c = new Company();
		c = (Company) con.getObject(con.addObject(c));
		c.setName("My Company");
		c = con.getObjects(Company.class).singleResult();
		assertEquals("My Company", c.getName());
	}

	public void testWriteField() throws Exception {
		Company c = new Company();
		Person p = new Person();
		Person w = new Person();
		c.setName("My Company");
		p.getGivenNames().add("me");
		w.getGivenNames().add("my");
		w.setSurname("wife");
		p.setSpouse(w);
		c.addEmployee(p);
		c = (Company) con.getObject(con.addObject(c));
		p = c.findByGivenName("me");
		w = p.getSpouse();
		assertEquals(Collections.singleton("me"), p.getGivenNames());
		assertEquals("wife", w.getSurname());
		assertTrue(c.isEmployed(p));
		assertTrue(c.isNamePresent());
		assertEquals("my wife", w.toString());
	}

	public void testWriteFieldAlt() throws Exception {
		Company c = new Company();
		Person p = new Person();
		Person w = new Person();
		c.setName("My Company");
		p.getGivenNames().add("me");
		w.getGivenNames().add("my");
		w.setSurname("wife");
		p.setSpouse(w);
		c.addEmployee(p);
		c = (Company) con.getObject(con.addObject(c));
		p = c.findByGivenName("nobody", "me");
		w = p.getSpouse();
		assertEquals(Collections.singleton("me"), p.getGivenNames());
		assertEquals("wife", w.getSurname());
		assertTrue(c.isEmployed(p));
		assertTrue(c.isNamePresent());
		assertEquals("my wife", w.toString());
	}

	public void testPrimitive() throws Exception {
		Company c = new Company();
		Person p = new Person();
		c.setName("My Company");
		p.getGivenNames().add("me");
		c.addEmployee(p);
		c = (Company) con.getObject(con.addObject(c));
		assertEquals(1, c.getNumberOfEmployees());
	}

	public void testSuper() throws Exception {
		Company c = (Company) con.getObject(con.addObject(new Company()));
		c.setName("My Company");
		c = con.getObjects(Company.class).singleResult();
		assertEquals("My Company", c.getLabel());
	}

	public void testModifyCall() throws Exception {
		Person p = (Person) con.getObject(con.addObject(new Person()));
		p.setSurname("Smith");
		assertEquals("Smith", p.getSurname());
	}

	public void testSameFieldName() throws Exception {
		Person p = new Person();
		p.setNumber(4);
		p = (Person) con.getObject(con.addObject(p));
		p.setSurname("Smith");
		assertEquals("Smith", p.getSurname());
		assertEquals(4, p.getNumber());
	}

	public void testAbstractConcept() throws Exception {
		assertEquals("Person", ((Person) con.getObject(con.addObject(new Person()))).getType());
		List<Person> list = con.getObjects(Person.class).asList();
		assertFalse(list.isEmpty());
	}

	public void testEquals() throws Exception {
		Person p1 = new Person();
		p1.setSurname("Smith");
		Person p2 = new Person();
		p2.setSurname("Smith");
		assertTrue(p1.equals(p2));
		p1 = (Person) con.getObject(con.addObject(new Person()));
		p1.setSurname("Smith");
		p2 = (Person) con.getObject(con.addObject(new Person()));
		p2.setSurname("Smith");
		assertFalse(p1.equals(p2));
	}

	public void testReadAccess() throws Exception {
		ValueFactory vf = con.getValueFactory();
		URI graph = vf.createURI("urn:test:graph");
		con.setAddContexts(graph);
		Company c = new Company();
		c = (Company) con.getObject(con.addObject(c));
		c.setName("My Company");
		assertEquals("My Company", c.getName());
		TupleQuery query = con.prepareTupleQuery("SELECT ?g WHERE {GRAPH ?g {?o <urn:test:name> ?name}}");
		query.setBinding("name", vf.createLiteral("My Company"));
		TupleQueryResult results = query.evaluate();
		Value g = results.next().getValue("g");
		results.close();
		assertEquals(graph, g);
		con.setAddContexts();
		assertEquals("My Company", c.getName());
		query = con.prepareTupleQuery("SELECT ?g WHERE {GRAPH ?g {?o <urn:test:name> ?name}}");
		query.setBinding("name", vf.createLiteral("My Company"));
		results = query.evaluate();
		g = results.next().getValue("g");
		results.close();
		assertEquals(graph, g);
	}

	@Override
	protected void setUp() throws Exception {
		// Person = urn:test:Person
		config.addConcept(Party.class);
		config.addConcept(Company.class);
		super.setUp();
	}

}
