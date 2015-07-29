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
import java.util.List;

import junit.framework.Test;

import org.openrdf.model.Value;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.object.base.ObjectRepositoryTestCase;
import org.openrdf.repository.object.concepts.Seq;

public class ContainerTest extends ObjectRepositoryTestCase {

	public static Test suite() throws Exception {
		return ObjectRepositoryTestCase.suite(ContainerTest.class);
	}

	public void testType() throws Exception {
		Seq list = con.addDesignation(con.getObject(ValueFactoryImpl
				.getInstance().createURI("urn:", "root")), Seq.class);
		list.add("one");
		assertNotNull(list.get(0));
		assertEquals(String.class, list.get(0).getClass());
	}

	public void testAdd() throws Exception {
		Seq list = con.addDesignation(con.getObject(ValueFactoryImpl
				.getInstance().createURI("urn:", "root")), Seq.class);
		list.add("one");
		list.add("two");
		list.add("four");
		list.add(2, "three");
		assertEquals(Arrays.asList("one", "two", "three", "four"), list);
		list = (Seq) con.getObject(ValueFactoryImpl.getInstance()
				.createURI("urn:", "root"));
		assertEquals(Arrays.asList("one", "two", "three", "four"), list);
	}

	public void testRemove() throws Exception {
		Seq list = con.addDesignation(con.getObject(ValueFactoryImpl
				.getInstance().createURI("urn:", "root")), Seq.class);
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
		Seq list = con.addDesignation(con.getObject(ValueFactoryImpl
				.getInstance().createURI("urn:", "root")), Seq.class);
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

	public void testMerge() throws Exception {
		List<Object> list = new ArrayList<Object>();
		list.add("one");
		list.add("two");
		list.add("Three");
		assertEquals(list, ((List<Object>) con.getObject(con
				.addObject(list))));
	}

	public void test_large() throws Exception {
		List<Integer> list = new ArrayList<Integer>(1000);
		for (int i = 0; i < 1000; i++) {
			list.add(i);
		}
		Value uri = con.addObject(list);
		list = (List<Integer>) con.getObject(uri);
		int sum = 0;
		for (Integer item : list) {
			sum += item;
		}
	}
}
