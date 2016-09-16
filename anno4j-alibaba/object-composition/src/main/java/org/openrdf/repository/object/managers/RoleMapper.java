/*
 * Copyright (c) 2009, James Leigh All rights reserved.
 * Copyright (c) 2011 Talis Inc., Some rights reserved.
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
package org.openrdf.repository.object.managers;

import org.openrdf.annotations.Classpath;
import org.openrdf.annotations.Iri;
import org.openrdf.annotations.Mixin;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.repository.object.exceptions.ObjectStoreConfigException;
import org.openrdf.repository.object.managers.helpers.HierarchicalRoleMapper;
import org.openrdf.repository.object.managers.helpers.RoleMatcher;
import org.openrdf.repository.object.managers.helpers.WeakValueMap;
import org.openrdf.repository.object.vocabulary.MSG;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Tracks the annotation, concept, and behaviour classes and what rdf:type they
 * should be used with.
 * 
 * @author James Leigh
 * 
 */
public class RoleMapper implements Cloneable {
	private static final WeakHashMap<ClassLoader, WeakValueMap<Set<URL>,ClassLoader>> classloaders = new WeakHashMap<ClassLoader, WeakValueMap<Set<URL>,ClassLoader>>();
	private ValueFactory vf;
	private Logger logger = LoggerFactory.getLogger(RoleMapper.class);
	private HierarchicalRoleMapper roleMapper = new HierarchicalRoleMapper();
	private Map<URI, List<Class<?>>> instances = new ConcurrentHashMap<URI, List<Class<?>>>(
			256);
	private RoleMatcher matches = new RoleMatcher();
	private Map<Method, URI> annotations = new HashMap<Method, URI>();
	private Map<URI, Method> annotationURIs = new HashMap<URI, Method>();
	private Map<Class<?>, String> complementIDs;
	private Map<Class<?>, Class<?>> complementClasses;
	private Map<Class<?>, List<Class<?>>> intersections;
	private Set<Class<?>> conceptClasses = new HashSet<Class<?>>();

	public RoleMapper() {
		this(ValueFactoryImpl.getInstance());
	}

	public RoleMapper(ValueFactory vf) {
		this.vf = vf;
		roleMapper.setURIFactory(vf);
		complementIDs = new ConcurrentHashMap<Class<?>, String>();
		complementClasses = new ConcurrentHashMap<Class<?>, Class<?>>();
		intersections = new ConcurrentHashMap<Class<?>, List<Class<?>>>();
	}

	public RoleMapper clone() {
		try {
			RoleMapper cloned = (RoleMapper) super.clone();
			cloned.roleMapper = roleMapper.clone();
			cloned.instances = clone(instances);
			cloned.matches = matches.clone();
			cloned.annotations = new HashMap<Method, URI>(annotations);
			cloned.annotationURIs = new HashMap<URI, Method>(annotationURIs);
			cloned.complementIDs = new ConcurrentHashMap<Class<?>, String>(complementIDs);
			cloned.complementClasses = new ConcurrentHashMap<Class<?>, Class<?>>(complementClasses);
			cloned.intersections = clone(intersections);
			cloned.conceptClasses = new HashSet<Class<?>>(conceptClasses);
			return cloned;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}

	private <K, V> Map<K, List<V>> clone(Map<K, List<V>> map) {
		Map<K, List<V>> cloned = new ConcurrentHashMap<K, List<V>>(map);
		for (Map.Entry<K, List<V>> e : cloned.entrySet()) {
			e.setValue(new CopyOnWriteArrayList<V>(e.getValue()));
		}
		return cloned;
	}

	public Collection<Class<?>> getConceptClasses() {
		return conceptClasses;
	}

	public Collection<Class<?>> findIndividualRoles(URI instance,
			Collection<Class<?>> classes) {
		List<Class<?>> list = instances.get(instance);
		if (list != null) {
			classes.addAll(list);
			addImpliedRoles(list, classes);
		}
		list = new ArrayList<Class<?>>();
		matches.findRoles(instance.stringValue(), list);
		classes.addAll(list);
		addImpliedRoles(list, classes);
		return classes;
	}

	public boolean isRecordedConcept(URI type, ClassLoader cl) {
		if (roleMapper.isTypeRecorded(type)) {
			for (Class<?> role : findAllRoles(type)) {
				if (findType(role) != null)
					return true;
			}
		}
		if ("java:".equals(type.getNamespace())) {
			try {
				synchronized (cl) {
					java.lang.Class.forName(type.getLocalName(), true, cl);
				}
				return true;
			} catch (ClassNotFoundException e) {
				return false;
			}
		}
		return false;
	}

	public Class<?> findInterfaceConcept(URI uri) {
		Class<?> concept = null;
		Class<?> mapped = null;
		Collection<Class<?>> rs = findAllRoles(uri);
		for (Class<?> r : rs) {
			URI type = findType(r);
			if (r.isInterface() && type != null) {
				concept = r;
				if (uri.equals(type)) {
					mapped = r;
					if (r.getSimpleName().equals(uri.getLocalName())) {
						return r;
					}
				}
			}
		}
		if (mapped != null)
			return mapped;
		if (concept != null)
			return concept;
		return null;
	}

	public Class<?> findConcept(URI uri, ClassLoader cl) {
		if (roleMapper.isTypeRecorded(uri)) {
			Class<?> concept = null;
			Class<?> mapped = null;
			Class<?> face = null;
			Collection<Class<?>> rs = findAllRoles(uri);
			for (Class<?> r : rs) {
				URI type = findType(r);
				if (type != null && r.isInterface()) {
					concept = r;
				}
				if (uri.equals(type)) {
					mapped = r;
					if (r.getSimpleName().equals(uri.getLocalName())) {
						return r;
					} else if (r.isInterface()) {
						face = r;
					}
				}
			}
			if (face != null)
				return face;
			if (mapped != null)
				return mapped;
			if (concept != null)
				return concept;
		}
		if ("java:".equals(uri.getNamespace())) {
			try {
				if (cl == null) {
					ClassLoader ccl = Thread.currentThread().getContextClassLoader();
					return java.lang.Class.forName(uri.getLocalName(), true, ccl);
				}
				return java.lang.Class.forName(uri.getLocalName(), true, cl);
			} catch (ClassNotFoundException e) {
				return null;
			}
		}
		return null;
	}

	public Collection<Class<?>> findRoles(URI type) {
		return findAdditionalRoles(roleMapper.findRoles(type));
	}

	public Collection<Class<?>> findRoles(Collection<URI> types,
			Collection<Class<?>> roles) {
		return findAdditionalRoles(roleMapper.findRoles(types, roles));
	}

	public Collection<Class<?>> findAdditionalRoles(Collection<Class<?>> classes) {
		if (intersections.isEmpty() && complementIDs.isEmpty() && complementClasses.isEmpty())
			return classes;
		List<Class<?>> result;
		result = new ArrayList<Class<?>>(classes.size() * 2 + 2);
		result.addAll(classes);
		int before = result.size();
		addIntersectionsAndComplements(result);
		int after = result.size();
		if (before != after) {
			ArrayList<Class<?>> anonymous;
			anonymous = new ArrayList<Class<?>>(result.subList(before, after));
			addImpliedRoles(anonymous, result);
		}
		return result;
	}

	public Collection<URI> findSubTypes(Class<?> role, Collection<URI> rdfTypes) {
		return roleMapper.findSubTypes(role, rdfTypes);
	}

	public URI findType(Class<?> concept) {
		return roleMapper.findType(concept);
	}

	public boolean isNamedTypePresent() {
		return roleMapper.isNamedTypePresent();
	}

	public boolean isIndividualRolesPresent(URI instance) {
		return !matches.isEmpty() || !instances.isEmpty() && instances.containsKey(instance);
	}

	public URI findAnnotation(Method ann) {
		return annotations.get(ann);
	}

	public Method findAnnotationMethod(URI uri) {
		return annotationURIs.get(uri);
	}

	public boolean isRecordedAnnotation(URI uri) {
		return findAnnotationMethod(uri) != null;
	}

	public void addAnnotation(Class<?> annotation) {
		for (Method m : annotation.getDeclaredMethods()) {
			if (!m.isAnnotationPresent(Iri.class)) {
				String msg = "@" + Iri.class.getSimpleName()
						+ " annotation required in " + m.toGenericString();
				throw new IllegalArgumentException(msg);
			}
			String uri = m.getAnnotation(Iri.class).value();
			addAnnotation(m, new URIImpl(uri));
		}
	}

	public void addAnnotation(Class<?> annotation, URI uri) {
		if (annotation.getDeclaredMethods().length != 1)
			throw new IllegalArgumentException(
					"Must specify annotation method if multiple methods exist: "
							+ annotation);
		addAnnotation(annotation.getDeclaredMethods()[0], uri);
	}

	public void addAnnotation(Method annotation) {
		if (!annotation.isAnnotationPresent(Iri.class))
			throw new IllegalArgumentException("@" + Iri.class.getSimpleName()
					+ " annotation required in " + annotation.toGenericString());
		String uri = annotation.getAnnotation(Iri.class).value();
		addAnnotation(annotation, new URIImpl(uri));
	}

	public void addAnnotation(Method annotation, URI uri) {
		annotations.put(annotation, uri);
		annotationURIs.put(uri, annotation);
		if (annotation.isAnnotationPresent(Iri.class)) {
			String iri = annotation.getAnnotation(Iri.class).value();
			if (!uri.stringValue().equals(iri)) {
				addAnnotation(annotation);
			}
		}
	}

	public void addConcept(Class<?> role) throws ObjectStoreConfigException {
		recordRole(role, role, null, true, true, true);
	}

	public void addConcept(Class<?> role, URI type)
			throws ObjectStoreConfigException {
		recordRole(role, role, type, true, true, true);
	}

	public void addBehaviour(Class<?> role) throws ObjectStoreConfigException {
		assertBehaviour(role);
		boolean hasType = false;
		for (Class<?> face : role.getInterfaces()) {
			boolean recorded = recordRole(role, face, null, true, false, false);
			if (recorded && hasType) {
				throw new ObjectStoreConfigException(role.getSimpleName()
						+ " can only implement one concept");
			} else {
				hasType |= recorded;
			}
		}
		if (!hasType)
			throw new ObjectStoreConfigException(role.getSimpleName()
					+ " must implement a concept or mapped explicitly");
	}

	public void addBehaviour(Class<?> role, URI type)
			throws ObjectStoreConfigException {
		assertBehaviour(role);
		recordRole(role, null, type, true, false, false);
	}

	private void assertBehaviour(Class<?> role)
			throws ObjectStoreConfigException {
		if (isAnnotationPresent(role))
			throw new ObjectStoreConfigException(role.getSimpleName()
					+ " cannot have a concept annotation");
		if (role.isInterface())
			throw new ObjectStoreConfigException(role.getSimpleName()
					+ " is an interface and not a behaviour");
		for (Method method : role.getDeclaredMethods()) {
			if (isAnnotationPresent(method)
					&& method.getName().startsWith("get"))
				throw new ObjectStoreConfigException(role.getSimpleName()
						+ " cannot have a property annotation");
		}
	}

	private Collection<Class<?>> findAllRoles(URI type) {
		Set<Class<?>> set = new HashSet<Class<?>>();
		for (Class<?> role : findRoles(type)) {
			if (set.add(role)) {
				addInterfaces(set, role.getSuperclass());
				addInterfaces(set, role.getInterfaces());
			}
		}
		return set;
	}

	private void addInterfaces(Set<Class<?>> set, Class<?>... list) {
		for (Class<?> c : list) {
			if (c != null && set.add(c)) {
				addInterfaces(set, c.getSuperclass());
				addInterfaces(set, c.getInterfaces());
			}
		}
	}

	private boolean isAnnotationPresent(AnnotatedElement role)
			throws ObjectStoreConfigException {
		return role.isAnnotationPresent(Iri.class);
	}

	private boolean recordRole(Class<?> role, Class<?> elm, URI rdfType,
			boolean equiv, boolean concept, boolean primary)
			throws ObjectStoreConfigException {

		// TODO remove this, only needed for testing purposes
		if (role != null) {
			System.out.println("Added role: " + role.toString());
		}

		boolean hasType = false;
		if (rdfType != null) {
			if (concept) {
				roleMapper.recordConcept(role, rdfType, equiv, primary);
			} else {
				roleMapper.recordBehaviour(role, rdfType, equiv);
			}
			hasType = true;
		} else if (elm != null) {
			URI defType = findDefaultType(elm);
			if (defType != null) {
				if (concept) {
					roleMapper.recordConcept(role, defType, equiv, role.equals(elm));
				} else {
					roleMapper.recordBehaviour(role, defType, equiv);
				}
				hasType = true;
			}
			hasType |= recordAnonymous(role, elm, concept);
		}
		if (!hasType && elm != null) {
			for (Class<?> face : elm.getInterfaces()) {
				hasType |= recordRole(role, face, null, equiv, concept, false);
			}
		}
		if (!hasType && primary) {
			throw new ObjectStoreConfigException(role.getSimpleName()
					+ " does not have an RDF type mapping");
		}
		recordMixins(role, elm, rdfType);
		if (concept && !role.isInterface()) {
			conceptClasses.add(role);
		}
		return hasType;
	}

	private void recordMixins(Class<?> role, Class<?> elm, URI rdfType)
			throws ObjectStoreConfigException {
		Mixin mixin = role.getAnnotation(Mixin.class);
		if (mixin != null) {
			for (Class<?> mix : mixin.value()) {
				assertBehaviour(mix);
				recordRole(mix, elm, rdfType, true, false, false);
			}
			for (String name : mixin.name()) {
				Classpath cp = role.getAnnotation(Classpath.class);
				String[] jars = cp == null ? new String[0] : cp.value();
				Class<?> mix = findClass(name, role.getClassLoader(), jars,
						role);
				if (mix != null) {
					assertBehaviour(mix);
					recordRole(mix, elm, rdfType, true, false, false);
				} else {
					logger.error("Could not find {} in {}", name, jars);
				}
			}
		}
	}

	private Class<?> findClass(String name, ClassLoader cl, String[] jars,
			Class<?> base) {
		try {
			return Class.forName(name, true, findClassLoader(cl, jars, base));
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	private ClassLoader findClassLoader(ClassLoader parent, String[] jars,
			Class<?> base) {
		if (jars.length == 0)
			return parent; // no children
		Set<URL> urls = resolve(jars, base);
		if (urls.isEmpty())
			return parent; // no valid children
		return createClassLoader(parent, urls);
	}

	private static synchronized ClassLoader createClassLoader(ClassLoader parent, Set<URL> urls) {
		WeakValueMap<Set<URL>, ClassLoader> map = classloaders.get(parent);
		if (map == null) {
			map = new WeakValueMap<Set<URL>, ClassLoader>();
			classloaders.put(parent, map);
		}
		ClassLoader direct = map.get(urls);
		if (direct != null)
			return direct; // already loaded
		for (Map.Entry<Set<URL>, ClassLoader> e : map.entrySet()) {
			ClassLoader p = e.getValue();
			if (p != null && urls.containsAll(e.getKey())) {
				Set<URL> set = new HashSet<URL>(urls);
				set.removeAll(e.getKey());
				URL[] array = set.toArray(new URL[set.size()]);
				ClassLoader cl = new URLClassLoader(array, p);
				map.put(urls, cl);
				return cl; // reuse an exiting cl as parent
			}
		}
		URL[] array = urls.toArray(new URL[urls.size()]);
		ClassLoader cl = new URLClassLoader(array, parent);
		map.put(urls, cl);
		return cl; // new cl
	}

	private Set<URL> resolve(String[] jars, Class<?> base) {
		Set<URL> urls = new HashSet<URL>(jars.length);
		for (int i = 0; i < jars.length; i++) {
			try {
				urls.add(resolve(jars[i], base));
			} catch (MalformedURLException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return urls;
	}

	private URL resolve(String jar, Class<?> base) throws MalformedURLException {
		java.net.URI jurl = java.net.URI.create(jar);
		if (jurl.isAbsolute())
			return jurl.toURL();
		URL url = base.getResource(jar);
		if (url != null)
			return url;
		java.net.URI uri = java.net.URI.create(getSystemId(base));
		return uri.resolve(jar).toURL();
	}

	private String getSystemId(Class<?> base) {
		if (base.isAnnotationPresent(Iri.class))
			return base.getAnnotation(Iri.class).value();
		String name = base.getSimpleName() + ".class";
		URL url = base.getResource(name);
		if (url != null)
			return url.toExternalForm();
		return "java:" + base.getName();
	}

	private boolean recordAnonymous(Class<?> role, Class<?> elm,
			boolean isConcept) throws ObjectStoreConfigException {
		boolean recorded = false;
		for (Annotation ann : elm.getAnnotations()) {
			for (Method m : ann.annotationType().getDeclaredMethods()) {
				try {
					URI name = findAnnotation(m);
					if (name == null && m.isAnnotationPresent(Iri.class)) {
						addAnnotation(m);
						name = findAnnotation(m);
					}
					if (name == null)
						continue;
					Object value = m.invoke(ann);
					recorded |= recordAnonymous(role, isConcept, name, value);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					continue;
				}
			}
		}
		return recorded;
	}

	private boolean recordAnonymous(Class<?> role, boolean isConcept, URI name,
			Object value) throws ObjectStoreConfigException {
		boolean recorded = false;
		if (OWL.EQUIVALENTCLASS.equals(name)) {
			Object[] values = (Object[]) value;
			for (Object v : values) {
				if (v instanceof Class<?>) {
					Class<?> concept = (Class<?>) v;
					URI uri = findDefaultType(concept);
					if (uri != null) {
						// only equivalent named concepts are supported here
						recorded |= recordRole(role, concept, uri, true, isConcept, false);
					}
				} else if (v instanceof String) {
					URI uri = vf.createURI((String) v);
					recorded |= recordRole(role, role, uri, true, isConcept, false);
				} else {
					logger.error("{} must have a value of type Class[] or String[]",
							name);
				}
			}
		}
		if (MSG.MATCHING.equals(name)) {
			String[] values = (String[]) value;
			for (String pattern : values) {
				matches.addRoles(pattern, role);
				recorded = true;
			}
		}
		if (OWL.ONEOF.equals(name)) {
			String[] values = (String[]) value;
			for (String instance : values) {
				URI uri = vf.createURI(instance);
				List<Class<?>> list = instances.get(uri);
				if (list == null) {
					list = new CopyOnWriteArrayList<Class<?>>();
					instances.put(uri, list);
				}
				list.add(role);
				recorded = true;
			}
		}
		if (OWL.COMPLEMENTOF.equals(name)) {
			if (value instanceof Object[]) {
				Object[] ar = (Object[]) value;
				if (ar.length != 1) {
					logger.error("{} must have exactly one value",
							name);
				}
				value = ar[0];
			}
			if (value instanceof Class<?>) {
				Class<?> concept = (Class<?>) value;
				recordRole(concept, concept, null, true, true, true);
				complementClasses.put(role, concept);
				recorded = true;
			} else if (value instanceof String) {
				complementIDs.put(role, (String) value);
				recorded = true;
			} else {
				logger.error("{} must have a value of type Class or String",
						name);
			}
		}
		if (OWL.INTERSECTIONOF.equals(name)) {
			List<Class<?>> ofs = new ArrayList<Class<?>>();
			loop: for (Object v : (Object[]) value) {
				if (v instanceof Class<?>) {
					Class<?> concept = (Class<?>) v;
					recordRole(concept, concept, null, true, true, true);
					ofs.add(concept);
				} else if (v instanceof String) {
					Class<?> superclass = role.getSuperclass();
					if (superclass != null
							&& superclass.isAnnotationPresent(Iri.class)) {
						if (v.equals(superclass.getAnnotation(Iri.class)
								.value())) {
							recordRole(superclass, superclass, null, true, true, true);
							ofs.add(superclass);
							continue loop;
						}
					}
					for (Class<?> sp : role.getInterfaces()) {
						if (sp.isAnnotationPresent(Iri.class)) {
							if (v.equals(sp.getAnnotation(Iri.class).value())) {
								recordRole(sp, sp, null, true, true, true);
								ofs.add(sp);
								continue loop;
							}
						}
					}
					logger.error("{} can only reference super classes", name);
				} else {
					logger.error(
							"{} must have a value of type Class[] or String[]",
							name);
				}
			}
			intersections.put(role, ofs);
			recorded = true;
		}
		if (OWL.UNIONOF.equals(name)) {
			for (Object v : (Object[]) value) {
				if (v instanceof Class<?>) {
					Class<?> concept = (Class<?>) v;
					recordRole(concept, concept, null, true, true, true);
					if (role.isAssignableFrom(concept)) {
						recorded = true; // implied
					} else {
						recorded |= recordRole(role, concept, null, false,
								isConcept, false);
					}
				} else if (v instanceof String) {
					recorded |= recordRole(role, null,
							vf.createURI((String) v), false, isConcept, false);
				} else {
					logger.error(
							"{} must have a value of type Class[] or String[]",
							name);
				}
			}
		}
		return recorded;
	}

	private URI findDefaultType(AnnotatedElement elm) {
		if (elm.isAnnotationPresent(Iri.class)) {
			String value = elm.getAnnotation(Iri.class).value();
			if (value != null) {
				return vf.createURI(value);
			}
		}
		return null;
	}

	private void addIntersectionsAndComplements(Collection<Class<?>> roles) {
		for (Map.Entry<Class<?>, List<Class<?>>> e : intersections.entrySet()) {
			Class<?> inter = e.getKey();
			List<Class<?>> of = e.getValue();
			if (!roles.contains(inter) && intersects(roles, of)) {
				roles.add(inter);
			}
		}
		if (complementIDs.isEmpty() && complementClasses.isEmpty())
			return;
		boolean complementAdded = false;
		for (Map.Entry<Class<?>, String> e : complementIDs.entrySet()) {
			Class<?> comp = e.getKey();
			String of = e.getValue();
			if (!roles.contains(comp) && !contains(roles, of)) {
				complementAdded = true;
				roles.add(comp);
			}
		}
		for (Map.Entry<Class<?>, Class<?>> e : complementClasses.entrySet()) {
			Class<?> comp = e.getKey();
			Class<?> of = e.getValue();
			if (!roles.contains(comp) && !contains(roles, of)) {
				complementAdded = true;
				roles.add(comp);
			}
		}
		if (!complementAdded)
			return;
		for (Map.Entry<Class<?>, List<Class<?>>> e : intersections.entrySet()) {
			Class<?> inter = e.getKey();
			List<Class<?>> of = e.getValue();
			if (!roles.contains(inter) && intersects(roles, of)) {
				roles.add(inter);
			}
		}
	}

	private void addImpliedRoles(Collection<Class<?>> anonymous,
			Collection<Class<?>> result) {
		for (Class<?> r : anonymous) {
			Class<?> sc = r.getSuperclass();
			if (sc != null) {
				URI uri = findType(sc);
				if (uri != null) {
					result.addAll(roleMapper.findRoles(uri));
				}
			}
			for (Class<?> c : r.getInterfaces()) {
				URI uri = findType(c);
				if (uri != null) {
					result.addAll(roleMapper.findRoles(uri));
				}
			}
		}
	}

	private boolean intersects(Collection<Class<?>> roles, Collection<Class<?>> ofs) {
		for (Class<?> of : ofs) {
			if (!contains(roles, of))
				return false;
		}
		return true;
	}

	private boolean contains(Collection<Class<?>> roles, Class<?> of) {
		for (Class<?> type : roles) {
			if (of.isAssignableFrom(type))
				return true;
		}
		return false;
	}

	private boolean contains(Collection<Class<?>> roles, String iri) {
		Set<Class<?>> set = new HashSet<Class<?>>();
		for (Class<?> type : roles) {
			if (isImplementationPresent(type, iri, set))
				return true;
		}
		return false;
	}

	private boolean isImplementationPresent(Class<?> type, String iri, Set<Class<?>> ignore) {
		if (type == null || ignore.contains(type))
			return false;
		Iri id = type.getAnnotation(Iri.class);
		if (id != null && iri.equals(id.value()))
			return true;
		for (Class<?> face : type.getInterfaces()) {
			if (isImplementationPresent(face, iri, ignore))
				return true;
		}
		return isImplementationPresent(type.getSuperclass(), iri, ignore);
	}
}
