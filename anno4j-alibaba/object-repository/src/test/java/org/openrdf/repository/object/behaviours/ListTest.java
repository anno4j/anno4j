/*
 * Copyright (c) 2007, James Leigh All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution. 
 * - Neither the name of the openrdf.org nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package org.openrdf.repository.object.behaviours;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import junit.framework.Test;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.RDFObject;
import org.openrdf.repository.object.base.RepositoryTestCase;
import org.openrdf.repository.object.concepts.List;
import org.openrdf.repository.object.config.ObjectRepositoryConfig;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;

public class ListTest extends RepositoryTestCase {
	public static Test suite() throws Exception {
		return RepositoryTestCase.suite(ListTest.class);
	}

	private ObjectRepository factory;

	private ObjectConnection manager;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		factory = (ObjectRepository) repository;
		this.manager = factory.getConnection();
	}

	@Override
	protected Repository getRepository() throws Exception {
		ObjectRepositoryFactory factory = new ObjectRepositoryFactory();
		ObjectRepositoryConfig config = factory.getConfig();
		config.addConcept(List.class);
		return factory.createRepository(config, super.getRepository());
	}

	@Override
	protected void tearDown() throws Exception {
		manager.close();
		factory.shutDown();
		super.tearDown();
	}

	public void testAdd() throws Exception {
		List<Object> list = manager.addDesignation(manager.getObject("urn:root"), List.class);
		list.add("one");
		list.add("two");
		list.add("three");
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
		List<Object> list = manager.addDesignation(manager.getObject("urn:root"), List.class);
		list.add("one");
		list.add("two");
		list.add("four");
		list.add(2, "three");
		assertEquals(Arrays.asList("one", "two", "three", "four"), list);
		Iterator<Object> it = list.iterator();
		it.next();
		it.remove();
		assertEquals(Arrays.asList("two", "three", "four"), list);
		it = list.iterator();
		it.next();
		it.next();
		it.remove();
		assertEquals(Arrays.asList("two", "four"), list);
		it = list.iterator();
		it.next();
		it.next();
		it.remove();
		assertEquals(Arrays.asList("two"), list);
	}

	public void testSet() throws Exception {
		List<Object> list = manager.addDesignation(manager.getObject("urn:root"), List.class);
		list.add("one");
		list.add("two");
		list.add("three");
		assertEquals(Arrays.asList("one", "two", "three"), list);
		list.set(0, "ONE");
		assertEquals(Arrays.asList("ONE", "two", "three"), list);
		list.set(1, "TWO");
		assertEquals(Arrays.asList("ONE", "TWO", "three"), list);
		list.set(2, "THREE");
		assertEquals(Arrays.asList("ONE", "TWO", "THREE"), list);
	}

	public void testDelete() throws Exception {
		int before = getSize(repository);
		URI uri = ValueFactoryImpl.getInstance().createURI("urn:", "root");
		List<Object> list = manager.addDesignation(manager.getObject(uri), List.class);
		list.add("one");
		list.add("two");
		list.add("three");
		list.clear();
		assertEquals(0, list.size());
		manager.remove(uri, null, null);
		int difference = getSize(repository) - before;
		assertEquals(0, difference);
	}

	private int getSize(Repository repository) throws RepositoryException {
		int size = 0;
		RepositoryConnection connection = null;
		RepositoryResult<Statement> iter = null;
		try {
			connection = repository.getConnection();
			iter = connection.getStatements(null, null, null, false);
			while (iter.hasNext()) {
				iter.next();
				++size;
			}
		} finally {
			if (iter != null)
				iter.close();
			if (connection != null)
				connection.close();
		}
		return size;
	}

	public static class ArrayListEntity<T> extends ArrayList<T> implements
			RDFObject {
		public ObjectConnection getObjectConnection() {
			return null;
		}

		public URI getResource() {
			return ValueFactoryImpl.getInstance().createURI("urn:", "root");
		}

		public Set<Object> get(String pred) {
			// TODO Auto-generated method stub
			return null;
		}

		public void set(String pred, Set<?> values) {
			// TODO Auto-generated method stub
			
		}

		public Object getSingle(String pred) {
			// TODO Auto-generated method stub
			return null;
		}

		public void setSingle(String pred, Object value) {
			// TODO Auto-generated method stub
			
		}
	}

	public void testMerge() throws Exception {
		java.util.List<Object> list = new ArrayListEntity<Object>();
		list.add("one");
		list.add("two");
		list.add("Three");
		URI uri = ValueFactoryImpl.getInstance().createURI("urn:", "root");
		assertEquals(uri, manager.addObject(list));
		java.util.List rdfList = (java.util.List) manager.getObject(uri);
		assertEquals(list, new ArrayList<Object>(rdfList));
	}
}
