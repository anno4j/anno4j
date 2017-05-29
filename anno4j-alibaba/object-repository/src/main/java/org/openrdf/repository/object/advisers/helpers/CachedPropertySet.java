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
package org.openrdf.repository.object.advisers.helpers;

import static java.util.Collections.EMPTY_LIST;
import info.aduna.iteration.CloseableIteration;
import info.aduna.iteration.CloseableIteratorIteration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectQuery;
import org.openrdf.repository.object.RDFObject;
import org.openrdf.repository.object.exceptions.ObjectPersistException;
import org.openrdf.repository.object.result.ObjectCursor;
import org.openrdf.repository.object.result.ObjectIterator;
import org.openrdf.repository.object.traits.ManagedRDFObject;
import org.openrdf.repository.object.traits.PropertyConsumer;

/**
 * A set for a given getResource(), predicate.
 * 
 * @author James Leigh
 */
public class CachedPropertySet extends RemotePropertySet implements
		PropertyConsumer {
	private static final int CACHE_LIMIT = 10;
	List<Object> cache;
	boolean cached;
	private ObjectQueryFactory factory;
	private PropertySetFactory creator;
	private String binding;
	private List<BindingSet> bindings;
	private boolean merged;

	public CachedPropertySet(ManagedRDFObject bean, PropertySetModifier property) {
		super(bean, property);
		this.factory = bean.getObjectQueryFactory();
	}

	public void setPropertySetFactory(PropertySetFactory creator) {
		this.creator = creator;
	}

	public synchronized void usePropertyBindings(String binding, List<BindingSet> bindings) {
		this.binding = binding;
		this.bindings = bindings;
	}

	@Override
	public synchronized void refresh() {
		super.refresh();
		cached = false;
		cache = null;
		binding = null;
		bindings = null;
	}

	@Override
	public void clear() {
		if (isCacheComplete() && !cache.isEmpty()) {
			ObjectConnection conn = getObjectConnection();
			try {
				boolean autoCommit = conn.isAutoCommit();
				if (autoCommit)
					conn.setAutoCommit(false);
				try {
					for (Object o : cache)
						remove(o);
					if (autoCommit)
						conn.setAutoCommit(true);
				} finally {
					if (autoCommit && !conn.isAutoCommit()) {
						conn.rollback();
						conn.setAutoCommit(true);
					}
				}
			} catch (RepositoryException e) {
				throw new ObjectPersistException(e);
			}
			refreshCache();
		} else if (!cached || !cache.isEmpty()) {
			super.clear();
			refreshCache();
		}
		cache = Collections.EMPTY_LIST;
		cached = true;
	}

	@Override
	public void setSingle(Object o) {
		if (!cached || !cache.isEmpty()) {
			super.setSingle(o);
		} else if (o != null) {
			add(o);
		}
		if (!merged) {
			cache = o == null ? EMPTY_LIST : Collections.singletonList(o);
			cached = true;
		}
	}

	@Override
	public void setAll(Set<?> set) {
		if (!cached || !cache.isEmpty()) {
			super.setAll(set);
		} else if (!set.isEmpty()) {
			addAll(set);
		}
		if (!merged) {
			cache = set == null ? EMPTY_LIST : new ArrayList<Object>(set);
			cached = true;
		}
	}

	@Override
	public boolean contains(Object o) {
		if (isCacheComplete())
			return cache.contains(o);
		if (cached && cache.contains(o))
			return true;
		return super.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		if (isCacheComplete())
			return cache.containsAll(c);
		if (cached && cache.containsAll(c))
			return true;
		return super.containsAll(c);
	}

	@Override
	public Object getSingle() {
		if (cached && cache.isEmpty())
			return null;
		if (cached)
			return cache.get(0);
		return super.getSingle();
	}

	@Override
	public boolean isEmpty() {
		if (cached)
			return cache.isEmpty();
		return super.isEmpty();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		refreshCache();
		return super.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		refreshCache();
		return super.retainAll(c);
	}

	@Override
	public int size() {
		if (isCacheComplete())
			return cache.size();
		return super.size();
	}

	@Override
	public Iterator<Object> iterator() {
		if (isCacheComplete()) {
			final Iterator<Object> iter = cache.iterator();
			return new Iterator<Object>() {
				private Object e;

				public boolean hasNext() {
					return iter.hasNext();
				}

				public Object next() {
					return e = iter.next();
				}

				public void remove() {
					CachedPropertySet.this.remove(e);
				}
			};
		}
		return super.iterator();
	}

	@Override
	public Object[] toArray() {
		if (isCacheComplete())
			return cache.toArray();
		return super.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		if (isCacheComplete())
			return cache.toArray(a);
		return super.toArray(a);
	}

	protected void refreshCache() {
		if (cached) {
			for (Object e : cache) {
				refresh(e);
			}
		}
	}

	private boolean isCacheComplete() {
		return cached && cache.size() < CACHE_LIMIT;
	}

	@Override
	protected Value getValue(Object instance) throws RepositoryException {
		Value value = super.getValue(instance);
		if (!merged && value instanceof Resource && !isManaged(instance)) {
			merged = true;
		}
		return value;
	}

	private boolean isManaged(Object instance) {
		return instance instanceof RDFObject
				&& ((RDFObject) instance).getObjectConnection() == getObjectConnection();
	}

	@Override
	protected synchronized CloseableIteration<?, ?> getObjects() throws RepositoryException,
			QueryEvaluationException {
		if (creator == null || factory == null) {
			return super.getObjects();
		} else if (binding == null) {
			ObjectQuery query = factory.createQuery(creator);
			if (query == null)
				return super.getObjects();
			try {
				query.setBinding("self", getResource());
				return query.evaluate(creator.getPropertyType());
			} finally {
				factory.returnQuery(creator, query);
			}
		} else {
			CloseableIteratorIteration<BindingSet, QueryEvaluationException> result;
			result = new CloseableIteratorIteration<BindingSet, QueryEvaluationException>(
					bindings.iterator());
			return new ObjectCursor(getObjectConnection(), result, binding);
		}
	}

	@Override
	protected ObjectIterator<?, Object> getObjectIterator() {
		try {
			return new ObjectIterator<Object, Object>(getObjects()) {
				private List<Object> list = new ArrayList<Object>(CACHE_LIMIT);

				@Override
				protected Object convert(Object instance)
						throws RepositoryException {
					if (list != null && list.size() < CACHE_LIMIT)
						list.add(instance);
					return instance;
				}

				@Override
				protected void remove(Object instance) {
					list = null;
					CachedPropertySet.this.remove(instance);
				}

				@Override
				public void close() {
					try {
						if (list != null
								&& (!hasNext() || list.size() == CACHE_LIMIT)) {
							cache = list;
							cached = true;
						}
					} finally {
						super.close();
					}
				}
			};
		} catch (RepositoryException e) {
			throw new ObjectPersistException(e);
		} catch (QueryEvaluationException e) {
			throw new ObjectPersistException(e);
		}
	}

}
