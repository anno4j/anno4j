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
package org.openrdf.repository.object.managers.helpers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.URI;

/**
 * Tracks explicit types and roles assignment.
 * 
 * @author James Leigh
 * 
 */
public class DirectMapper implements Cloneable {

	private Map<Class<?>, Set<URI>> directTypes;

	private Map<URI, Set<Class<?>>> directRoles;

	public DirectMapper() {
		directTypes = new HashMap<Class<?>, Set<URI>>(256);
		directRoles = new HashMap<URI, Set<Class<?>>>(256);
	}

	public DirectMapper clone() {
		try {
			DirectMapper cloned = (DirectMapper) super.clone();
			cloned.directTypes = clone(directTypes);
			cloned.directRoles = clone(directRoles);
			return cloned;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}

	private <K,V> Map<K, Set<V>> clone(Map<K, Set<V>> map) {
		Map<K, Set<V>> cloned = new HashMap<K, Set<V>>(map);
		for (Map.Entry<K, Set<V>> e : cloned.entrySet()) {
			e.setValue(new HashSet<V>(e.getValue()));
		}
		return cloned;
	}

	public Set<Class<?>> getDirectRoles(URI type) {
		return directRoles.get(type);
	}

	public Set<URI> getDirectTypes(Class<?> role) {
		return directTypes.get(role);
	}

	public void recordRole(Class<?> role, URI rdfType) {
		Set<URI> set = directTypes.get(role);
		if (set == null)
			directTypes.put(role, set = new HashSet<URI>());
		if (rdfType != null)
			set.add(rdfType);
		if (rdfType != null) {
			Set<Class<?>> set1 = directRoles.get(rdfType);
			if (set1 == null)
				directRoles.put(rdfType, set1 = new HashSet<Class<?>>());
			if (role != null)
				set1.add(role);
		}
	}
}