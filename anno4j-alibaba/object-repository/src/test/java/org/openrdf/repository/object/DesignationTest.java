package org.openrdf.repository.object;

import java.util.Set;

import junit.framework.Test;

import org.openrdf.annotations.Iri;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.object.base.ObjectRepositoryTestCase;
import org.openrdf.repository.object.concepts.ClassConcept;
import org.openrdf.repository.object.concepts.Property;

public class DesignationTest extends ObjectRepositoryTestCase {

	public static Test suite() throws Exception {
		return ObjectRepositoryTestCase.suite(DesignationTest.class);
	}

	@Iri("http://www.w3.org/2000/01/rdf-schema#Resource")
	public interface Resource {
		/** The subject is an instance of a class. */
		@Iri("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
		public abstract Set<ClassConcept> getRdfTypes();

		/** The subject is an instance of a class. */
		public abstract void setRdfTypes(Set<ClassConcept> value);
	}

	@Override
	protected void setUp() throws Exception {
		config.addConcept(Resource.class);
		config.addConcept(Property.class);
		config.addConcept(ClassConcept.class);
		super.setUp();
	}

	public void testDesignateEntity() throws Exception {
		URI name = ValueFactoryImpl.getInstance().createURI("urn:resource");
		Resource resource = (Resource) con.getObject(name);
		assertEquals(0, resource.getRdfTypes().size());
		Property prop = con.addDesignation(resource, Property.class);
		assertEquals(1, prop.getRdfTypes().size());
		con.removeDesignation(prop, Property.class);
		resource = (Resource) con.getObject(name);
		assertTrue(!(resource instanceof Property));
		assertEquals(0, resource.getRdfTypes().size());
	}

}
