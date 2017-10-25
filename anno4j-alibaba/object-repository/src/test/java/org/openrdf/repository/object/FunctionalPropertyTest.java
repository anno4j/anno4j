package org.openrdf.repository.object;

import junit.framework.Test;

import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.base.ObjectRepositoryTestCase;
import org.openrdf.result.Result;

public class FunctionalPropertyTest extends ObjectRepositoryTestCase {

	public static Test suite() throws Exception {
		return ObjectRepositoryTestCase.suite(FunctionalPropertyTest.class);
	}

	@Iri("urn:test:MyResource")
	public static class MyResource {
		@Iri("urn:test:name")
		private String name;

		public MyResource() {
			super();
		}

		public MyResource(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	@Iri("urn:test:MyClass")
	public static class MyClass {
		@Iri("urn:test:name")
		private String name;
		@Iri("urn:test:resource")
		private MyResource resource;

		public MyClass() {
			super();
		}

		public MyClass(String name, MyResource resource) {
			this.name = name;
			this.resource = resource;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public MyResource getMyResource() {
			return resource;
		}

		public void setMyResource(MyResource resource) {
			this.resource = resource;
		}
	}

	@Iri("urn:test:MyInterfaceResource")
	public interface MyInterfaceResource {
		@Iri("urn:test:name")
		String getName();
		void setName(String name);
	}

	@Iri("urn:test:MyInterface")
	public interface MyInterface {
		@Iri("urn:test:name")
		String getName();
		void setName(String name);
		@Iri("urn:test:resource")
		MyInterfaceResource getMyInterfaceResource();
		void setMyInterfaceResource(MyInterfaceResource resource);
	}

	public void setUp() throws Exception {
		config.addConcept(MyClass.class);
		config.addConcept(MyResource.class);
		config.addConcept(MyInterface.class);
		config.addConcept(MyInterfaceResource.class);
		super.setUp();
	}

	public void testClass() throws Exception {
		con.addObject(new MyClass("my class", new MyResource("my resource")));
		Result<MyClass> result = con.getObjects(MyClass.class);
		while (result.hasNext()) {
			assertEquals("my resource", result.next().getMyResource().getName());
		}
		result.close();
	}

	public void testInterface() throws Exception {
		con.setAutoCommit(false);
		MyInterface my = con.addDesignation(of.createObject(), MyInterface.class);
		MyInterfaceResource res = con.addDesignation(of.createObject(), MyInterfaceResource.class);
		my.setName("my interface");
		my.setMyInterfaceResource(res);
		res.setName("my interface resource");
		con.setAutoCommit(true);
		Result<MyInterface> result = con.getObjects(MyInterface.class);
		while (result.hasNext()) {
			assertEquals("my interface resource", result.next().getMyInterfaceResource().getName());
		}
		result.close();
	}

	public void testEmptyClass() throws Exception {
		con.addObject(new MyClass("my class", null));
		Result<MyClass> result = con.getObjects(MyClass.class);
		while (result.hasNext()) {
			assertNull(result.next().getMyResource());
		}
		result.close();
	}

	public void testEmptyInterface() throws Exception {
		con.setAutoCommit(false);
		MyInterface my = con.addDesignation(of.createObject(), MyInterface.class);
		my.setName("my interface");
		con.setAutoCommit(true);
		Result<MyInterface> result = con.getObjects(MyInterface.class);
		while (result.hasNext()) {
			assertNull(result.next().getMyInterfaceResource());
		}
		result.close();
	}
}
