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
package org.openrdf.repository.object;

import static org.openrdf.query.QueryLanguage.SPARQL;
import info.aduna.iteration.CloseableIteration;
import info.aduna.iteration.LookAheadIteration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.contextaware.ContextAwareConnection;
import org.openrdf.repository.object.exceptions.BlobConflictException;
import org.openrdf.repository.object.exceptions.BlobStoreException;
import org.openrdf.repository.object.exceptions.ObjectPersistException;
import org.openrdf.repository.object.managers.helpers.WeakValueMap;
import org.openrdf.repository.object.result.ObjectIterator;
import org.openrdf.repository.object.traits.Mergeable;
import org.openrdf.repository.object.traits.RDFObjectBehaviour;
import org.openrdf.repository.object.traits.Refreshable;
import org.openrdf.result.Result;
import org.openrdf.result.impl.ResultImpl;
import org.openrdf.store.blob.BlobObject;
import org.openrdf.store.blob.BlobStore;
import org.openrdf.store.blob.BlobVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Primary interface for object retrieval and persistence.
 *
 * @author James Leigh
 *
 */
public class ObjectConnection extends ContextAwareConnection {
	/**
	 * Closes open iterators.
	 *
	 * @param iter
	 */
	public static void close(Iterator<?> iter) {
		ObjectIterator.close(iter);
	}

	final Logger logger = LoggerFactory.getLogger(ObjectConnection.class);
	private final ObjectRepository repository;
	private String language;
	private final TypeManager types;
	private final ObjectFactory of;
	private final Map<Object, Resource> assigned = new IdentityHashMap<Object, Resource>();
	private final Set<Resource> merged = new HashSet<Resource>();
	private final Map<Class<?>, Map<Integer, ObjectQuery>> queries = new HashMap<Class<?>, Map<Integer, ObjectQuery>>();
	private final BlobStore blobs;
	private URI versionBundle;
	private BlobVersion blobVersion;
	private final Map<Resource, RDFObject> cachedObjects = new WeakValueMap<Resource, RDFObject>(512);

	protected ObjectConnection(ObjectRepository repository,
			RepositoryConnection connection, ObjectFactory factory,
			TypeManager types, BlobStore blobs) throws RepositoryException {
		super(repository, connection);
		this.repository = repository;
		this.of = factory;
		this.types = types;
		this.blobs = blobs;
		types.setConnection(this);
		factory.setObjectConnection(this);
	}

	@Override
	public ObjectRepository getRepository() {
		return repository;
	}

	/**
	 * An identifier for this connection if assigned, or null.
	 *
	 * @return a {@link URI} representing the current connection or null
	 */
	public URI getVersionBundle() {
		return versionBundle;
	}

	/**
	 * Assigns a URI to this connection to be used for new blob versions and the
	 * default insert graph.
	 *
	 * @param bundle
	 *            a unique URI
	 */
	public void setVersionBundle(URI bundle) {
		versionBundle = bundle;
		if (null == getInsertContext()) {
			setInsertContext(bundle);
		}
	}

	public String toString() {
		URI uri = getVersionBundle();
		if (uri == null)
			return getDelegate().toString();
		return uri.stringValue();
	}

	@Override
	public void close() throws RepositoryException {
		try {
			super.close();
		} finally {
			cachedObjects.clear();
		}
	}

	@Override
	public synchronized void rollback() throws RepositoryException {
		if (blobVersion != null) {
			try {
				blobVersion.rollback();
			} catch (IOException e) {
				throw new RepositoryException(e.toString(), e);
			}
		}
		super.rollback();
		cachedObjects.clear();
	}

	@Override
	public synchronized void commit() throws RepositoryException {
		try {
			try {
				if (blobVersion != null) {
					try {
						blobVersion.prepare();
					} catch(IOException exc) {
						throw new BlobConflictException(exc);
					}
				}
				super.commit();
				if (blobVersion != null) {
					blobVersion.commit();
					blobVersion = null;
				}
			} finally {
				if (blobVersion != null) {
					blobVersion.rollback();
				}
			}
		} catch (IOException e) {
			throw new BlobStoreException(e);
		}
	}

	@Override
	public synchronized void setAutoCommit(boolean auto) throws RepositoryException {
		if (!auto && isAutoCommit()) {
			try {
				try {
					if (blobVersion != null) {
						try {
							blobVersion.prepare();
						} catch(IOException exc) {
							throw new BlobConflictException(exc);
						}
					}
					super.setAutoCommit(auto);
					if (blobVersion != null) {
						blobVersion.commit();
						blobVersion = null;
					}
				} finally {
					if (blobVersion != null) {
						blobVersion.rollback();
						blobVersion = null;
					}
				}
			} catch (IOException e) {
				throw new BlobStoreException(e);
			}
		} else {
			super.setAutoCommit(auto);
		}
	}

	/**
	 * The assign language for this connection, if any.
	 *
	 * @return language tag ("en") or null
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Assigns a language to this connection.
	 * @param lang such as "en"
	 */
	public void setLanguage(String lang) {
		this.language = lang;
	}

	/**
	 * Access to the ObjectFactory used with this connection.
	 *
	 * @return ObjectFactory bound to this connection.
	 */
	public ObjectFactory getObjectFactory() {
		return of;
	}

	/**
	 * Imports the instance into the RDF store if not previously imported. If an
	 * object with the same Resource identifier has already been imported into
	 * the store during through this connection, the Resource identifier is
	 * returned and the object is not imported.
	 *
	 * @see #addObject(Resource, Object)
	 * @return the given instance's {@link Resource} identifier or {@link Literal}
	 *         representation
	 */
	public Value addObject(Object instance) throws RepositoryException {
		if (instance instanceof RDFObjectBehaviour) {
			RDFObjectBehaviour support = (RDFObjectBehaviour) instance;
			Object entity = support.getBehaviourDelegate();
			if (entity != instance)
				return addObject(entity);
		}
		if (instance instanceof RDFObject) {
			if (((RDFObject) instance).getObjectConnection() == this)
				return ((RDFObject) instance).getResource();
		} else {
			if (of.isDatatype(instance.getClass()))
				return of.createLiteral(instance);
		}
		Class<?> type = instance.getClass();
		if (RDFObject.class.isAssignableFrom(type) || isEntity(type)) {
			Resource resource = assignResource(instance);
			if (!isAlreadyMerged(resource)) {
				addObject(resource, instance);
			}
			return resource;
		}
		return of.createLiteral(instance);
	}

	/**
	 * Imports the entity into the RDF store using the given URI.
	 */
	public void addObject(String uri, Object entity)
			throws RepositoryException {
		addObject(getValueFactory().createURI(uri), entity);
	}

	/**
	 * Imports the entity into the RDF store using the given handle.
	 */
	public void addObject(Resource resource, Object entity)
			throws RepositoryException {
		if (entity instanceof RDFObjectBehaviour) {
			RDFObjectBehaviour support = (RDFObjectBehaviour) entity;
			Object delegate = support.getBehaviourDelegate();
			if (delegate != entity) {
				addObject(resource, delegate);
				return;
			}
		}
		synchronized (merged) {
			merged.add(resource);
		}
		boolean autoCommit = isAutoCommit();
		if (autoCommit) {
			setAutoCommit(false);
		}
		try {
			Class<?> proxy = entity.getClass();
			Set<URI> list = getTypes(proxy, new HashSet<URI>(4));
			for (URI type : list) {
				types.addTypeStatement(resource, type);
			}
			Object result = of.createObject(resource, list);
			if (result instanceof Mergeable) {
				((Mergeable) result).merge(entity);
			}
			if (autoCommit) {
				setAutoCommit(true);
			}
			cachedObjects.remove(resource);
		} finally {
			if (autoCommit && !isAutoCommit()) {
				rollback();
				setAutoCommit(true);
			}
		}
	}

	/**
	 * Explicitly adds the concept to the entity.
	 *
	 * @return the entity with new composed concept
	 */
	public <T> T addDesignation(Object entity, Class<T> concept)
			throws RepositoryException {
		if (entity instanceof RDFObjectBehaviour) {
			RDFObjectBehaviour support = (RDFObjectBehaviour) entity;
			Object delegate = support.getBehaviourDelegate();
			if (delegate != entity) {
				return addDesignation(delegate, concept);
			}
		}
		Resource resource = findResource(entity);
		Set<URI> types = new HashSet<URI>(4);
		getTypes(entity.getClass(), types);
		addConcept(resource, concept, types);
		RDFObject bean = of.createObject(resource, types);
		assert assertConceptRecorded(bean, concept);
		return (T) cache(bean);
	}

	/**
	 * Explicitly adds the type to the entity.
	 *
	 * @return the entity with new composed types
	 */
	public Object addDesignation(Object entity, String uri) throws RepositoryException {
		return addDesignations(entity, getValueFactory().createURI(uri));
	}

	/**
	 * Explicitly adds the type to the entity.
	 *
	 * @return the entity with new composed type
	 */
	public Object addDesignation(Object entity, URI type)
			throws RepositoryException {
		return addDesignations(entity, type);
	}

	/**
	 * Explicitly adds the types to the entity.
	 *
	 * @return the entity with new composed types
	 */
	public Object addDesignations(Object entity, String... uris) throws RepositoryException {
		URI[] types = new URI[uris.length];
		for (int i=0;i<uris.length;i++) {
			types[i] = getValueFactory().createURI(uris[i]);
		}
		return addDesignations(entity, types);
	}

	/**
	 * Explicitly adds the types to the entity.
	 *
	 * @return the entity with new composed types
	 */
	public Object addDesignations(Object entity, URI... types)
			throws RepositoryException {
		if (entity instanceof RDFObjectBehaviour) {
			RDFObjectBehaviour support = (RDFObjectBehaviour) entity;
			Object delegate = support.getBehaviourDelegate();
			if (delegate != entity) {
				return addDesignations(delegate, types);
			}
		}
		assert types != null && types.length > 0;
		Resource resource = findResource(entity);
		Set<URI> list = new HashSet<URI>(4);
		getTypes(entity.getClass(), list);
		boolean autoCommit = isAutoCommit();
		if (autoCommit) {
			setAutoCommit(false);
		}
		try {
			for (URI type : types) {
				this.types.addTypeStatement(resource, type);
				list.add(type);
			}
			if (autoCommit) {
				setAutoCommit(true);
			}
		} finally {
			if (autoCommit && !isAutoCommit()) {
				rollback();
				setAutoCommit(true);
			}
		}
		return cache(of.createObject(resource, list));
	}

	/**
	 * Explicitly removes the concept from the entity.
	 */
	public void removeDesignation(Object entity, Class<?> concept)
			throws RepositoryException {
		Resource resource = findResource(entity);
		URI type = of.getNameOf(concept);
		if (type == null) {
			throw new ObjectPersistException(
					"Concept is anonymous or is not registered: "
							+ concept.getSimpleName());
		}
		types.removeTypeStatement(resource, type);
		cachedObjects.remove(resource);
	}

	/**
	 * Explicitly removes the type from the entity.
	 */
	public void removeDesignation(Object entity, String uri) throws RepositoryException {
		removeDesignations(entity, getValueFactory().createURI(uri));
	}

	/**
	 * Explicitly removes the type from the entity.
	 */
	public void removeDesignation(Object entity, URI type)
			throws RepositoryException {
		removeDesignations(entity, type);
	}

	/**
	 * Explicitly removes the types from the entity.
	 */
	public void removeDesignations(Object entity, String... uris) throws RepositoryException {
		URI[] types = new URI[uris.length];
		for (int i=0;i<uris.length;i++) {
			types[i] = getValueFactory().createURI(uris[i]);
		}
		removeDesignations(entity, types);
	}

	/**
	 * Explicitly removes the types from the entity.
	 */
	public void removeDesignations(Object entity, URI... types)
			throws RepositoryException {
		assert types != null && types.length > 0;
		boolean autoCommit = isAutoCommit();
		if (autoCommit) {
			setAutoCommit(false);
		}
		try {
			Resource resource = findResource(entity);
			for (URI type : types) {
				this.types.removeTypeStatement(resource, type);
			}
			if (autoCommit) {
				setAutoCommit(true);
			}
			cachedObjects.remove(resource);
		} finally {
			if (autoCommit && !isAutoCommit()) {
				rollback();
				setAutoCommit(true);
			}
		}
	}

	/**
	 * Loads a single Object by URI in String form.
	 */
	public Object getObject(String uri) throws RepositoryException {
		assert uri != null;
		return getObject(getValueFactory().createURI(uri));
	}

	/**
	 * Loads a single Object or converts the literal into an Object.
	 */
	public Object getObject(Value value) throws RepositoryException {
		assert value != null;
		if (value instanceof Literal)
			return of.createObject((Literal) value);
		Resource resource = (Resource) value;
		RDFObject cached = cached(resource);
		if (cached != null)
			return cached;
		return cache(of.createObject(resource, types.getTypes(resource)));
	}

	/**
	 * Loads a single Object that is assumed to be of the given concept.
	 */
	public <T> T getObject(Class<T> concept, String uri)
			throws RepositoryException, QueryEvaluationException {
		assert uri != null;
		return getObject(concept, getValueFactory().createURI(uri));
	}

	/**
	 * Loads a single Object that is assumed to be of the given concept.
	 */
	public <T> T getObject(Class<T> concept, Resource resource)
			throws RepositoryException, QueryEvaluationException {
		RDFObject cached = cached(resource);
		if (concept.isInstance(cached))
			return concept.cast(cached);
		return getObjects(concept, resource).singleResult();
	}

	/**
	 * Returns a single object that is presumed to have the given rdf:types.
	 */
	public Object getObject(Set<URI> types, Resource resource) {
		Class<?> proxy = of.getObjectClass(resource, types);
		RDFObject cached = cached(resource);
		if (cached != null && cached.getClass().equals(proxy))
			return cached;
		return cache(of.createBean(resource, proxy));
	}

	/**
	 * Matches objects that have the given concept rdf:type. This method will
	 * include all objects that implement the given concept or a subclass of the
	 * concept. The concept must be a named concept and cannot be mapped to
	 * rdfs:Resource. The result of this method is not guaranteed to be unique
	 * and may continue duplicates. Use the {@link Result#asSet()} method to
	 * ensure uniqueness.
	 *
	 * @see #addDesignation(Object, Class)
	 */
	public synchronized <T> Result<T> getObjects(Class<T> concept)
			throws RepositoryException,
			QueryEvaluationException {
		try {
			return getObjectQuery(concept, 0).evaluate(concept);
		} catch (MalformedQueryException e) {
			throw new AssertionError(e);
		}
	}

	public <T> Result<T> getObjects(Class<T> concept, String... uris)
			throws RepositoryException, QueryEvaluationException {
		ValueFactory vf = getValueFactory();
		Resource[] resources = new Resource[uris.length];
		for (int i = 0; i < uris.length; i++) {
			resources[i] = vf.createURI(uris[i]);
		}
		return getObjects(concept, resources);
	}

	/**
	 * Loads the list of resources assumed to implement the given concept. The
	 * concept must be a named concept and cannot be mapped to rdfs:Resource.
	 */
	public synchronized <T> Result<T> getObjects(final Class<T> concept,
			Resource... resources) throws RepositoryException,
			QueryEvaluationException {
		try {
			int size = resources.length;
			ObjectQuery query = getObjectQuery(concept, size);
			if (size == 1) {
				query.setBinding(ObjectFactory.VAR_PREFIX, resources[0]);
			} else if (size > 1) {
				for (int i = 0; i < size; i++) {
					query.setBinding(ObjectFactory.VAR_PREFIX + i, resources[i]);
				}
			}
			final List<Resource> list = new ArrayList<Resource>(size);
			list.addAll(Arrays.asList(resources));
			CloseableIteration<T, QueryEvaluationException> iter;
			final Result<T> result = query.evaluate(concept);
			iter = new LookAheadIteration<T, QueryEvaluationException>() {
				@Override
				protected T getNextElement() throws QueryEvaluationException {
					T next = result.next();
					if (next != null) {
						list.remove(((RDFObject) next).getResource());
						return next;
					}
					if (!list.isEmpty())
						return (T) cache(of.createObject(list.remove(0)));
					return null;
				}
			};
			return new ResultImpl<T>(iter);
		} catch (MalformedQueryException e) {
			throw new AssertionError(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T refresh(T object) throws RepositoryException {
		Resource resource = findResource(object);
		if (object instanceof Refreshable) {
			((Refreshable) object).refresh();
		}
		Set<URI> types = this.types.getTypes(resource);
		Class<?> proxy = of.getObjectClass(resource, types);
		RDFObject cached = cached(resource);
		if (cached != null && cached != object && cached instanceof Refreshable) {
			((Refreshable) cached).refresh();
		}
		if (cached != null && cached.getClass().equals(proxy))
			return (T) cached;
		return (T) cache(of.createBean(resource, proxy));
	}

	public synchronized BlobObject getBlobObject(final String uri)
			throws RepositoryException {
		if (blobs == null)
			throw new RepositoryException("No configured blob store");
		try {
			if (blobVersion == null && isAutoCommit()) {
				return blobs.open(uri);
			} else if (blobVersion == null) {
				URI version = getVersionBundle();
				if (version == null) {
					blobVersion = blobs.newVersion();
				} else {
					blobVersion = blobs.newVersion(version.stringValue());
				}
				return blobVersion.open(uri);
			} else {
				return blobVersion.open(uri);
			}
		} catch (IOException exc) {
			throw new RepositoryException(exc);
		}
	}

	public BlobObject getBlobObject(URI uri) throws RepositoryException {
		return getBlobObject(uri.stringValue());
	}

	/**
	 * Creates a new query that returns object(s).
	 */
	public ObjectQuery prepareObjectQuery(QueryLanguage ql, String query,
			String baseURI) throws MalformedQueryException, RepositoryException {
		return createObjectQuery(prepareTupleQuery(ql, query, baseURI));
	}

	/**
	 * Creates a new query that returns object(s).
	 */
	public ObjectQuery prepareObjectQuery(QueryLanguage ql, String query)
			throws MalformedQueryException, RepositoryException {
		return createObjectQuery(prepareTupleQuery(ql, query));
	}

	/**
	 * Creates a new query that returns object(s).
	 */
	public ObjectQuery prepareObjectQuery(String query)
			throws MalformedQueryException, RepositoryException {
		return createObjectQuery(prepareTupleQuery(query));
	}

	RDFObject cache(RDFObject object) {
		cachedObjects.put(object.getResource(), object);
		return object;
	}

	RDFObject cached(Resource resource) {
		return cachedObjects.get(resource);
	}

	/** method and result synchronised on this */
	private <T> ObjectQuery getObjectQuery(Class<T> concept,
			int length) throws MalformedQueryException,
			RepositoryException {
		if (queries.containsKey(concept)
				&& queries.get(concept).containsKey(length)) {
			return queries.get(concept).get(length);
		} else {
			String sparql = of.createObjectQuery(concept, length);
			ObjectQuery query = prepareObjectQuery(SPARQL, sparql);
			Map<Integer, ObjectQuery> map = queries.get(concept);
			if (map == null) {
				queries.put(concept, map = new HashMap<Integer, ObjectQuery>());
			}
			map.put(length, query);
			return query;
		}
	}

	private ObjectQuery createObjectQuery(TupleQuery query) {
		return new ObjectQuery(this, query);
	}

	private Resource findResource(Object object) {
		if (object instanceof RDFObject)
			return ((RDFObject) object).getResource();
		throw new ObjectPersistException(
				"Object not created by this ObjectFactory: "
						+ object.getClass().getSimpleName());
	}

	private boolean isEntity(Class<?> type) {
		if (type == null)
			return false;
		for (Class<?> face : type.getInterfaces()) {
			if (of.isNamedConcept(face))
				return true;
		}
		if (of.isNamedConcept(type))
			return true;
		return isEntity(type.getSuperclass());
	}

	private boolean assertConceptRecorded(Object bean, Class<?> concept) {
		assert !concept.isInterface()
				|| concept.isAssignableFrom(bean.getClass()) : "Concept is Anonymous or has not bean recorded: "
				+ concept.getSimpleName();
		return true;
	}

	private Resource assignResource(Object bean) {
		synchronized (assigned) {
			if (assigned.containsKey(bean))
				return assigned.get(bean);
			Resource resource = null;
			if (bean instanceof RDFObject) {
				resource = ((RDFObject) bean).getResource();
			}
			if (resource == null) {
				resource = getValueFactory().createBNode();
			}
			assigned.put(bean, resource);
			return resource;
		}
	}

	private boolean isAlreadyMerged(Resource resource) {
		synchronized (merged) {
			return merged.contains(resource);
		}
	}

	private <C extends Collection<URI>> C getTypes(Class<?> role, C set)
			throws RepositoryException {
		URI type = of.getNameOf(role);
		if (type == null) {
			Class<?> superclass = role.getSuperclass();
			if (superclass != null) {
				getTypes(superclass, set);
			}
			Class<?>[] interfaces = role.getInterfaces();
			for (int i = 0, n = interfaces.length; i < n; i++) {
				getTypes(interfaces[i], set);
			}
		} else {
			set.add(type);
		}
		return set;
	}

	private <C extends Collection<URI>> C addConcept(Resource resource,
			Class<?> role, C set) throws RepositoryException {
		URI type = of.getNameOf(role);
		if (type == null) {
			throw new ObjectPersistException(
					"Concept is anonymous or is not registered: "
							+ role.getSimpleName());
		}
		types.addTypeStatement(resource, type);
		set.add(type);
		return set;
	}
}
