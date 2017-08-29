/*
 * Copyright (c) 2009, James Leigh All rights reserved.
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
package org.openrdf.repository.object.advisers.helpers;

import static org.openrdf.query.QueryLanguage.SPARQL;

import java.util.HashMap;
import java.util.Map;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.TupleQuery;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectQuery;
import org.openrdf.repository.object.managers.PropertyMapper;

/**
 * Creates the query used to retrieve property values.
 * 
 * @author James Leigh
 *
 */
public class ObjectQueryFactory {

	private PropertyMapper mapper;

	private ObjectConnection connection;

	private Map<PropertySetFactory, ObjectQuery> queries = new HashMap<PropertySetFactory, ObjectQuery>();

	public ObjectQueryFactory(ObjectConnection connection, PropertyMapper mapper) {
		this.connection = connection;
		this.mapper = mapper;
	}

	public ObjectQuery createQuery(PropertySetFactory factory)
			throws RepositoryException {
		synchronized (queries) {
			ObjectQuery query = queries.remove(factory);
			if (query != null)
				return query;
		}
		Class<?> type = factory.getPropertyType();
		Map<String, String> properties = mapper.findEagerProperties(type);
		if (properties == null)
			return null;
		// TODO this should be a static string
		String sparql = buildQuery(properties, factory);
		try {
			TupleQuery tuples = connection.prepareTupleQuery(SPARQL, sparql);
			return new ObjectQuery(connection, tuples);
		} catch (MalformedQueryException e) {
			throw new RepositoryException(e);
		}
	}

	public void returnQuery(PropertySetFactory factory, ObjectQuery query) {
		synchronized (queries) {
			// will we need to close old query?
			queries.put(factory, query);
		}
	}

	private String buildQuery(Map<String, String> properties,
			PropertySetFactory factory) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ?obj ");
		for (String name : properties.keySet()) {
			sb.append(" ?obj_").append(name);
		}
		sb.append("\nWHERE { ");
		String uri = factory.getPredicate().stringValue();
		sb.append(" $self <").append(uri).append("> ?obj ");
		for (String name : properties.keySet()) {
			String pred = properties.get(name);
			sb.append("\nOPTIONAL {").append(" ?obj <");
			sb.append(pred);
			sb.append("> ?obj_").append(name).append(" } ");
		}
		sb.append(" } ");
		return sb.toString();
	}
}
