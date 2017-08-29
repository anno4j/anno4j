package org.openrdf.repository.object;

import java.util.List;

import junit.framework.Test;

import org.openrdf.annotations.Iri;
import org.openrdf.model.URI;
import org.openrdf.repository.object.base.ObjectRepositoryTestCase;
import org.openrdf.result.Result;

public class FindAllTest extends ObjectRepositoryTestCase {
	private static final String BASE = "urn:test:";

	public static Test suite() throws Exception {
		return ObjectRepositoryTestCase.suite(FindAllTest.class);
	}

	@Iri("urn:test:MyClass")
	public interface MyClass {}

	@Iri("urn:test:MyOtherClass")
	public interface MyOtherClass extends MyClass {}

	public void testOtherClass() throws Exception {
		Result<MyOtherClass> iter = con.getObjects(MyOtherClass.class);
		assertTrue(iter.hasNext());
		URI other = con.getValueFactory().createURI(BASE, "my-other-class");
		assertEquals(other, con.addObject(iter.next()));
		assertFalse(iter.hasNext());
	}

	public void testClass() throws Exception {
		List<MyClass> list = con.getObjects(MyClass.class).asList();
		URI other = con.getValueFactory().createURI(BASE, "my-other-class");
		assertTrue(list.contains(con.getObject(other)));
		assertEquals(2, list.size());
	}

	@Override
	protected void setUp() throws Exception {
		config.addConcept(MyClass.class);
		config.addConcept(MyOtherClass.class);
		super.setUp();
		URI myClass = con.getValueFactory().createURI(BASE, "my-class");
		URI myOtherClass = con.getValueFactory().createURI(BASE, "my-other-class");
		con.addDesignation(con.getObject(myClass), MyClass.class);
		con.addDesignation(con.getObject(myOtherClass), MyOtherClass.class);
	}
}
