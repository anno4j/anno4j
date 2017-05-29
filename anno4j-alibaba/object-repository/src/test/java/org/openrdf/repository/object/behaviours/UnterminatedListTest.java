package org.openrdf.repository.object.behaviours;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.base.RepositoryTestCase;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;
import org.openrdf.rio.RDFFormat;

public class UnterminatedListTest extends RepositoryTestCase {
	public static Test suite() throws Exception {
		return RepositoryTestCase.suite(UnterminatedListTest.class);
	}

	private ObjectConnection manager;

	private ObjectRepository factory;

	private List list;

	public void testAddFour() throws Exception {
		assertEquals("[one, two, three]", list.toString());
		list.add("four");
		assertEquals("[one, two, three, four]", list.toString());
	}

	public void testAdd() throws Exception {
		assertEquals(Arrays.asList("one", "two", "three"), list);
		list = (List<Object>) manager.getObject("urn:root");
		assertEquals(Arrays.asList("one", "two", "three"), list);
		list.add(0, "zero");
		assertEquals(Arrays.asList("zero", "one", "two", "three"), list);
		list.add(2, "1.5");
		assertEquals(Arrays.asList("zero", "one", "1.5", "two", "three"), list);
		list.clear();
		list.addAll(Arrays.asList("one", "two", "three"));
		assertEquals(Arrays.asList("one", "two", "three"), list);
	}

	public void testRemove() throws Exception {
		assertEquals(Arrays.asList("one", "two", "three"), list);
		Iterator<Object> it = list.iterator();
		it.next();
		it.remove();
		assertEquals(Arrays.asList("two", "three"), list);
		it = list.iterator();
		it.next();
		it.next();
		it.remove();
		assertEquals(Arrays.asList("two"), list);
	}

	public void testSet() throws Exception {
		assertEquals(Arrays.asList("one", "two", "three"), list);
		list.set(0, "ONE");
		assertEquals(Arrays.asList("ONE", "two", "three"), list);
		list.set(1, "TWO");
		assertEquals(Arrays.asList("ONE", "TWO", "three"), list);
		list.set(2, "THREE");
		assertEquals(Arrays.asList("ONE", "TWO", "THREE"), list);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// Create repository connection
		RepositoryConnection conn = repository.getConnection();
		InputStream stream = getClass().getResourceAsStream("/testcases/unterminated-list.rdf");
		conn.add(stream, "", RDFFormat.RDFXML);
		conn.close();
		// Build manager
		factory = (ObjectRepository) repository;
		manager = factory.getConnection();
		list = (List) manager.getObject("urn:root");
	}

	@Override
	protected Repository getRepository() throws Exception {
		return new ObjectRepositoryFactory().createRepository(super.getRepository());
	}

	@Override
	protected void tearDown() throws Exception {
		manager.close();
		super.tearDown();
	}

}
