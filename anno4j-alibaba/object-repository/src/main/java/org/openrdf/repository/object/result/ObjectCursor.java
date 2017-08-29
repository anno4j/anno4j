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

import info.aduna.iteration.CloseableIteration;
import info.aduna.iteration.LookAheadIteration;

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
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectFactory;
import org.openrdf.repository.object.traits.PropertyConsumer;

/**
 * Converts a tuple cursor into an object cursor.
 * 
 * @author James Leigh
 *
 */
public class ObjectCursor extends LookAheadIteration<Object, QueryEvaluationException> {
	private String binding;
	private CloseableIteration<BindingSet, QueryEvaluationException> result;
	private BindingSet next;
	private ObjectFactory of;
	private ObjectConnection manager;

	public ObjectCursor(ObjectConnection manager, CloseableIteration<BindingSet, QueryEvaluationException> result,
			String binding) throws QueryEvaluationException {
		this.binding = binding;
		this.result = result;
		this.next = result.hasNext() ? result.next() : null;
		this.manager = manager;
		this.of = manager.getObjectFactory();
	}

	@Override
	public Object getNextElement() throws QueryEvaluationException {
		if (next == null)
			return null;
		List<BindingSet> properties;
		Value resource = next.getValue(binding);
		properties = readProperties();
		if (resource == null)
			return null;
		return createRDFObject(resource, properties);
	}

	private List<BindingSet> readProperties() throws QueryEvaluationException {
		Value resource = next.getValue(binding);
		List<BindingSet> properties = new ArrayList<BindingSet>();
		while (next != null && equals(resource, next.getValue(binding))) {
			properties.add(next);
			next = result.hasNext() ? result.next() : null;
		}
		return properties;
	}

	private boolean equals(Value v1, Value v2) {
		return v1 == v2 || v1 != null && v1.equals(v2);
	}

	private Object createRDFObject(Value value, List<BindingSet> properties)
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
