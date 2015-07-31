/*
 * Copyright (c) 2007-2009, James Leigh All rights reserved.
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
package org.openrdf.repository.object.result;

import info.aduna.iteration.LookAheadIteration;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectFactory;
import org.openrdf.repository.object.traits.PropertyConsumer;

/**
 * Converts the repository result into an array of Objects.
 * 
 * @author James Leigh
 * 
 */
public class ObjectArrayCursor extends LookAheadIteration<Object, QueryEvaluationException> {

	private List<String> bindings;
	private TupleQueryResult result;
	private BindingSet next;
	private ObjectFactory of;
	private ObjectConnection manager;
	private final Class<?> componentType;

	public ObjectArrayCursor(ObjectConnection manager, TupleQueryResult result,
			List<String> bindings) throws QueryEvaluationException {
		this(manager, result, bindings, Object.class);
	}

	public ObjectArrayCursor(ObjectConnection manager, TupleQueryResult result,
			List<String> bindings, Class<?> componentType) throws QueryEvaluationException {
		this.bindings = bindings;
		this.result = result;
		this.next = result.hasNext() ? result.next() : null;
		this.manager = manager;
		this.of = manager.getObjectFactory();
		this.componentType = componentType;
	}

	@Override
	public Object getNextElement() throws QueryEvaluationException {
		if (next == null)
			return null;
		List<BindingSet> properties;
		Value[] resources = new Value[bindings.size()];
		for (int i = 0; i < resources.length; i++) {
			resources[i] = next.getValue(bindings.get(i));
		}
		properties = readProperties(resources);
		Object result = Array.newInstance(componentType, resources.length);
		for (int i = 0; i < resources.length; i++) {
			if (resources[i] != null) {
				Object value = createRDFObject(resources[i], bindings.get(i), properties);
				Array.set(result, i, value);
			}
		}
		return result;
	}

	private List<BindingSet> readProperties(Value... values)
			throws QueryEvaluationException {
		List<BindingSet> properties = new ArrayList<BindingSet>();
		while (next != null) {
			for (int i = 0; i < values.length; i++) {
				if (!equals(values[i], next.getValue(bindings.get(i))))
					return properties;
			}
			properties.add(next);
			next = result.hasNext() ? result.next() : null;
		}
		return properties;
	}

	private boolean equals(Value v1, Value v2) {
		return v1 == v2 || v1 != null && v1.equals(v2);
	}

	private Object createRDFObject(Value value, String binding, List<BindingSet> properties)
			throws QueryEvaluationException {
		if (value == null)
			return null;
		if (value instanceof Literal)
			return of.createObject((Literal) value);
		Object obj;
		if (properties.get(0).hasBinding(binding + "_class")) {
			Set<URI> list = new HashSet<URI>(properties.size());
			for (BindingSet bindings : properties) {
				Value t = bindings.getValue(binding + "_class");
				if (t instanceof URI) {
					list.add((URI) t);
				}
			}
			obj = manager.getObject(list, (Resource) value);
		} else {
			try {
				obj = manager.getObject(value);
			} catch (RepositoryException e) {
				throw new QueryEvaluationException(e);
			}
		}
		if (obj instanceof PropertyConsumer) {
			((PropertyConsumer) obj).usePropertyBindings(binding, properties);
		}
		return obj;
	}

	@Override
	public void handleClose() throws QueryEvaluationException {
		result.close();
	}
}
