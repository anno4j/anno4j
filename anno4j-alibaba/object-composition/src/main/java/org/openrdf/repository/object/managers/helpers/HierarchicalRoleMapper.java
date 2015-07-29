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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.object.exceptions.ObjectCompositionException;

/**
 * Tracks recorded roles and maps them to their subject type.
 * 
 * @author James Leigh
 * 
 */
public class HierarchicalRoleMapper implements Cloneable {

	private DirectMapper directMapper = new DirectMapper();
	private TypeMapper typeMapper = new TypeMapper();
	private SimpleRoleMapper simpleRoleMapper = new SimpleRoleMapper();
	private Map<Class<?>, Set<Class<?>>> subclasses = new HashMap<Class<?>, Set<Class<?>>>(256);

	public HierarchicalRoleMapper clone() {
		try {
			HierarchicalRoleMapper cloned = (HierarchicalRoleMapper) super.clone();
			cloned.directMapper = directMapper.clone();
			cloned.typeMapper = typeMapper.clone();
			cloned.simpleRoleMapper = simpleRoleMapper.clone();
			cloned.subclasses = clone(subclasses);
			return cloned;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}

	private <K, V> Map<K, Set<V>> clone(Map<K, Set<V>> map) {
		Map<K, Set<V>> cloned = new HashMap<K, Set<V>>(map);
		for (Map.Entry<K, Set<V>> e : cloned.entrySet()) {
			e.setValue(new HashSet<V>(e.getValue()));
		}
		return cloned;
	}

	public void setURIFactory(ValueFactory vf) {
		simpleRoleMapper.setURIFactory(vf);
	}

	public Collection<Class<?>> findAllRoles() {
		return simpleRoleMapper.findAllRoles();
	}

	public Collection<Class<?>> findRoles(URI type) {
		return simpleRoleMapper.findRoles(type);
	}

	public Collection<Class<?>> findRoles(Collection<URI> types,
			Collection<Class<?>> classes) {
		return simpleRoleMapper.findRoles(types, classes);
	}

	public boolean isNamedTypePresent() {
		return simpleRoleMapper.isNamedTypePresent();
	}

	public boolean isTypeRecorded(URI type) {
		return simpleRoleMapper.isTypeRecorded(type);
	}

	/**
	 * Finds the rdf:Class<?> for this Java Class<?>.
	 * 
	 * @param role
	 * @return URI of the rdf:Class<?> for this Java Class<?> or null.
	 */
	public URI findType(Class<?> role) {
		return typeMapper.findType(role);
	}

	public Collection<URI> findSubTypes(Class<?> role, Collection<URI> rdfTypes) {
		URI type = findType(role);
		if (type == null)
			throw new ObjectCompositionException("Concept not registered: "
					+ role.getSimpleName());
		rdfTypes.add(type);
		Set<Class<?>> subset = subclasses.get(role);
		if (subset == null)
			return rdfTypes;
		for (Class<?> c : subset) {
			findSubTypes(c, rdfTypes);
		}
		return rdfTypes;
	}

	public synchronized void recordConcept(Class<?> role, URI type, boolean equiv, boolean primary) {
		assert type != null;
		recordClassHierarchy(role);
		if (primary) {
			typeMapper.recordRole(role, type);
		}
		if (equiv) {
			directMapper.recordRole(role, type);
		}
		if (simpleRoleMapper.getBaseType().equals(type)) {
			simpleRoleMapper.recordBaseRole(role);
		} else {
			Set<Class<?>> superRoles = getSuperRoles(role);
			Set<Class<?>> newRoles = new HashSet<Class<?>>(
					superRoles.size() + 1);
			newRoles.addAll(superRoles);
			newRoles.add(role);
			recordSubclasses(type, newRoles);
		}
	}

	public synchronized void recordBehaviour(Class<?> role, URI type, boolean equiv) {
		assert type != null;
		if (equiv) {
			directMapper.recordRole(role, type);
		}
		if (simpleRoleMapper.getBaseType().equals(type)) {
			recordClassHierarchy(role);
			simpleRoleMapper.recordBaseRole(role);
		} else {
			Set<Class<?>> newRoles = new HashSet<Class<?>>();
			newRoles.add(role);
			recordSubclasses(type, newRoles);
		}
	}

	/**
	 * Record the class hierarchy of the concept to looks subclasses of related
	 * subject types.
	 * 
	 * @param concept
	 */
	private void recordClassHierarchy(Class<?> concept) {
		for (Class<?> sup : concept.getInterfaces()) {
			Set<Class<?>> set = subclasses.get(sup);
			if (set == null)
				subclasses.put(sup, set = new HashSet<Class<?>>());
			if (!set.contains(concept)) {
				set.add(concept);
				recordClassHierarchy(sup);
			}
		}
		Class<?> sup = concept.getSuperclass();
		if (sup != null) {
			Set<Class<?>> set = subclasses.get(sup);
			if (set == null)
				subclasses.put(sup, set = new HashSet<Class<?>>());
			if (!set.contains(concept)) {
				set.add(concept);
				recordClassHierarchy(sup);
			}
		}
	}

	private Set<Class<?>> getSuperRoles(Class<?> role) {
		Set<Class<?>> superRoles = new HashSet<Class<?>>();
		for (Class<?> sup : role.getInterfaces()) {
			Set<Class<?>> sr = getSuperRoles(sup);
			addRelatedRoles(sr, sup, superRoles);
		}
		Class<?> sup = role.getSuperclass();
		if (sup != null) {
			Set<Class<?>> sr = getSuperRoles(sup);
			addRelatedRoles(sr, sup, superRoles);
		}
		return superRoles;
	}

	private void recordSubclasses(URI type, Set<Class<?>> newRoles) {
		newRoles = simpleRoleMapper.recordRoles(newRoles, type);
		Set<Class<?>> directRoles = directMapper.getDirectRoles(type);
		if (directRoles != null) {
			for (Class<?> r : directRoles) {
				addRolesInSubclasses(r, newRoles);
			}
		}
	}

	private void addRolesInSubclasses(Class<?> role, Set<Class<?>> newRoles) {
		Set<Class<?>> subset = subclasses.get(role);
		if (subset == null)
			return; // no subclasses
		for (Class<?> sub : subset) {
			Set<Class<?>> subRoles = new HashSet<Class<?>>();
			subRoles = addRelatedRoles(newRoles, sub, subRoles);
			addRolesInSubclasses(sub, subRoles);
		}
	}

	private Set<Class<?>> addRelatedRoles(Set<Class<?>> existing,
			Class<?> role, Set<Class<?>> roles) {
		roles.addAll(existing);
		Set<URI> set = directMapper.getDirectTypes(role);
		if (set != null) {
			for (URI uri : set) {
				simpleRoleMapper.recordRoles(existing, uri);
				for (Class<?> c : simpleRoleMapper.findRoles(uri)) {
					roles.add(c);
				}
			}
		}
		return roles;
	}
}