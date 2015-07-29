package org.openrdf.repository.object;

import junit.framework.Test;

import org.openrdf.annotations.Iri;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.object.base.ObjectRepositoryTestCase;

public class AbstractConceptTest extends ObjectRepositoryTestCase {

	public static Test suite() throws Exception {
		return ObjectRepositoryTestCase.suite(AbstractConceptTest.class);
	}

	public static abstract class Person implements RDFObject {
		@Iri("urn:test:name")
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public abstract String getFirstName();
	}

	public static abstract class FirstNameSupport {
		public abstract String getName();
		public String getFirstName() {
			return getName().split(" ")[0];
		}
	}

	@Override
	public void setUp() throws Exception {
		config.addConcept(Person.class, new URIImpl("urn:test:Person"));
		config.addBehaviour(FirstNameSupport.class, new URIImpl("urn:test:Person"));
		super.setUp();
	}

// TODO fix test. there is no urn:test:me object persisted.
//	public void testAbstractConcept() throws Exception {
//		URIImpl id = new URIImpl("urn:test:me");
//		Person me = con.addDesignation(con.getObject(id), Person.class);
//		me.setName("James Leigh");
//		assertEquals("James", me.getFirstName());
//	}
}
