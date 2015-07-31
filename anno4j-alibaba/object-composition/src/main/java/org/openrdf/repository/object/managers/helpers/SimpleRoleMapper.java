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
package org.openrdf.repository.object.managers.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tracks types and maps them to their assigned roles.
 * 
 * @author James Leigh
 * 
 */
public class SimpleRoleMapper implements Cloneable {

	/** http://www.w3.org/2000/01/rdf-schema#Resource */
	private static final String BASE_TYPE = "http://www.w3.org/2000/01/rdf-schema#Resource";

	private final Logger logger = LoggerFactory.getLogger(SimpleRoleMapper.class);

	private URI baseType;

	private boolean empty = true;

	private Map<URI, List<Class<?>>> roles; // javancss cannot parse Map<URI, Class<?>[]>

	private Map<URI, Boolean> unregisteredTypes = new ConcurrentHashMap<URI, Boolean>();

	public SimpleRoleMapper() {
		roles = new ConcurrentHashMap<URI, List<Class<?>>>(256);
	}

	public SimpleRoleMapper clone() {
		try {
			SimpleRoleMapper cloned = (SimpleRoleMapper) super.clone();
			cloned.roles = clone(roles);
			return cloned;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}

	private <K, V> Map<K, List<V>> clone(Map<K, List<V>> map) {
		Map<K, List<V>> cloned = new HashMap<K, List<V>>(map);
		for (Map.Entry<K, List<V>> e : cloned.entrySet()) {
			e.setValue(new ArrayList<V>(e.getValue()));
		}
		return cloned;
	}

	public void setURIFactory(ValueFactory vf) {
		baseType = vf.createURI(BASE_TYPE);
		List<Class<?>> list = Collections.emptyList();
		roles.put(baseType, list);
	}

	public URI getBaseType() {
		return baseType;
	}

	public Collection<Class<?>> findAllRoles() {
		List<Class<?>> list = new ArrayList<Class<?>>(roles.size());
		for (List<Class<?>> v : roles.values()) {
			list.addAll(v);
		}
		return list;
	}

	public Collection<Class<?>> findRoles(URI type) {
		List<Class<?>> classes = roles.get(type);
		if (classes == null) {
			unregistered(type);
			return findBaseRoles();
		}
		return classes;
	}

	public Collection<Class<?>> findRoles(Collection<URI> types,
			Collection<Class<?>> classes) {
		boolean found = false;
		for (URI type : types) {
			List<Class<?>> javaClass = roles.get(type);
			if (javaClass == null) {
				unregistered(type);
			} else {
				found = true;
				classes.addAll(javaClass);
			}
		}
		if (!found) {
			classes.addAll(findBaseRoles());
			return classes;
		}
		return classes;
	}

	public boolean isNamedTypePresent() {
		return !empty;
	}

	public boolean isTypeRecorded(URI type) {
		return roles.containsKey(type);
	}

	public synchronized Set<Class<?>> recordRoles(Set<Class<?>> role, URI uri) {
		List<Class<?>> set = roles.get(uri);
		Set<Class<?>> changed = new HashSet<Class<?>>();
		if (set == null) {
			List<Class<?>> bar = roles.get(baseType);
			if (bar == null) {
				changed = role;
			} else {
				changed.addAll(bar);
				changed.addAll(role);
			}
		} else {
			changed.addAll(set);
			changed.addAll(role);
		}
		if (set == null || changed.size() != set.size()) {
			empty &= uri.equals(baseType);
			roles.put(uri, Arrays.asList(changed.toArray(new Class<?>[changed.size()])));
		}
		return changed;

	}

	public synchronized void recordBaseRole(Class<?> role) {
		for (Map.Entry<URI, List<Class<?>>> e : roles.entrySet()) {
			List<Class<?>> set = e.getValue();
			boolean contains = false;
			for (Class<?> c : set) {
				if (role.equals(c)) {
					contains = true;
					break;
				}
			}
			if (contains)
				continue;
			List<Class<?>> ar = new ArrayList<Class<?>>(set.size() + 1);
			ar.addAll(set);
			ar.add(role);
			e.setValue(ar);
		}
	}

	private Collection<Class<?>> findBaseRoles() {
		return roles.get(baseType);
	}

	private void unregistered(URI type) {
		if (!unregisteredTypes.containsKey(type)) {
			unregisteredTypes.put(type, Boolean.TRUE);
			logger.info("Unregistered type {}", type);
		}
	}
}