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
package org.openrdf.repository.object.behaviours;

import info.aduna.iteration.CloseableIteration;
import info.aduna.iteration.ConvertingIteration;

import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.openrdf.annotations.Precedes;
import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.RDFObject;
import org.openrdf.repository.object.exceptions.ObjectPersistException;
import org.openrdf.repository.object.exceptions.ObjectStoreException;
import org.openrdf.repository.object.traits.Mergeable;
import org.openrdf.repository.object.traits.Refreshable;

/**
 * Java instance for rdf:List as a familiar interface to manipulate this List.
 * This implemention can only be modified when in autoCommit (autoFlush), or
 * when read uncommitted is supported.
 * 
 * @author James Leigh
 */
@Precedes(RDFObjectImpl.class)
public abstract class RDFList extends AbstractSequentialList<Object> implements
		Refreshable, Mergeable, RDFObject {

	private int _size = -1;

	private RDFList parent;

	public void refresh() {
		_size = -1;
		if (parent != null)
			parent.refresh();
	}

	public void merge(Object source) {
		if (source instanceof java.util.List) {
			clear();
			addAll((java.util.List) source);
		}
	}

	ValueFactory getValueFactory() {
		RepositoryConnection conn = getObjectConnection();
		return conn.getValueFactory();
	}

	private CloseableIteration<Value, RepositoryException> getValues(Resource subj, URI pred, Value obj) {
		try {
			RepositoryResult<Statement> stmts;
			ObjectConnection conn = getObjectConnection();
			stmts = conn.getStatements(subj, pred, obj);
			return new ConvertingIteration<Statement, Value, RepositoryException>(stmts) {
				@Override
				protected Value convert(Statement stmt) throws RepositoryException {
					return stmt.getObject();
				}
			};
		} catch (RepositoryException e) {
			throw new ObjectStoreException(e);
		}
	}

	void addStatement(Resource subj, URI pred, Value obj) {
		if (obj == null)
			return;
		try {
			ObjectConnection conn = getObjectConnection();
			conn.add(subj, pred, obj);
		} catch (RepositoryException e) {
			throw new ObjectPersistException(e);
		}
	}

	void removeStatements(Resource subj, URI pred, Value obj) {
		try {
			ObjectConnection conn = getObjectConnection();
			conn.remove(subj, pred, obj);
		} catch (RepositoryException e) {
			throw new ObjectPersistException(e);
		}
	}

	@Override
	public int size() {
		if (_size < 0) {
			synchronized (this) {
				if (_size < 0) {
					Resource list = getResource();
					int size;
					for (size = 0; list != null && !list.equals(RDF.NIL); size++) {
						Resource nlist = getRest(list);
						if (nlist == null && getFirst(list) == null)
							break;
						list = nlist;
					}
					_size = size;
				}
			}
		}
		return _size;
	}

	@Override
	public ListIterator<Object> listIterator(final int index) {
		return new ListIterator<Object>() {
			private ArrayList<Resource> prevLists = new ArrayList<Resource>();

			private boolean removed;

			Resource list;
			{
				for (int i = 0; i < index; i++) {
					next();
				}
			}

			public void add(Object o) {
				ObjectConnection conn = getObjectConnection();
				try {
					boolean autoCommit = conn.isAutoCommit();
					if (autoCommit)
						conn.setAutoCommit(false);
					try {
						if (getResource().equals(RDF.NIL)) {
							// size == 0
							throw new ObjectPersistException(
									"cannot add a value to the nil list");
							/*
							 * list = _id = getValueFactory().createBNode();
							 * addStatement(list, RDF.FIRST,
							 * SesameProperty.createValue(List.this, o));
							 * addStatement(list, RDF.REST, RDF.NIL);
							 */
						}
						Value value = o == null ? null : getObjectConnection()
								.addObject(o);
						if (getFirst(getResource()) == null) {
							// size == 0
							list = getResource();
							addStatement(list, RDF.FIRST, value);
							addStatement(list, RDF.REST, RDF.NIL);
						} else if (list == null) {
							// index = 0
							Value first = getFirst(getResource());
							Resource rest = getRest(getResource());
							BNode newList = getValueFactory().createBNode();
							addStatement(newList, RDF.FIRST, first);
							addStatement(newList, RDF.REST, rest);
							removeStatements(getResource(), RDF.FIRST, first);
							removeStatements(getResource(), RDF.REST, rest);
							addStatement(getResource(), RDF.FIRST, value);
							addStatement(getResource(), RDF.REST, newList);
						} else if (!list.equals(RDF.NIL)) {
							Resource rest = getRest(list);
							BNode newList = getValueFactory().createBNode();
							removeStatements(list, RDF.REST, rest);
							addStatement(list, RDF.REST, newList);
							addStatement(newList, RDF.FIRST, value);
							addStatement(newList, RDF.REST, rest);
						} else {
							// index == size
							throw new NoSuchElementException();
						}
						if (autoCommit)
							conn.setAutoCommit(true);
						refresh();
					} finally {
						if (autoCommit && !conn.isAutoCommit()) {
							conn.rollback();
							conn.setAutoCommit(true);
						}
					}
				} catch (RepositoryException e) {
					throw new ObjectPersistException(e);
				}
			}

			public void set(Object o) {
				ObjectConnection conn = getObjectConnection();
				try {
					boolean autoCommit = conn.isAutoCommit();
					if (autoCommit)
						conn.setAutoCommit(false);
					try {
						if (getResource().equals(RDF.NIL)) {
							// size == 0
							throw new NoSuchElementException();
						} else if (list.equals(RDF.NIL)) {
							// index = size
							throw new NoSuchElementException();
						} else {
							Value first = getFirst(list);
							removeStatements(list, RDF.FIRST, first);
							if (o != null) {
								Value obj = getObjectConnection().addObject(o);
								addStatement(list, RDF.FIRST, obj);
							}
						}
						if (autoCommit)
							conn.setAutoCommit(true);
					} finally {
						if (autoCommit && !conn.isAutoCommit()) {
							conn.rollback();
							conn.setAutoCommit(true);
						}
					}
					refresh();
				} catch (RepositoryException e) {
					throw new ObjectPersistException(e);
				}
			}

			public void remove() {
				ObjectConnection conn = getObjectConnection();
				try {
					boolean autoCommit = conn.isAutoCommit();
					if (autoCommit)
						conn.setAutoCommit(false);
					try {
						if (prevLists.size() < 1) {
							// remove index == 0
							Value first = getFirst(list);
							removeStatements(list, RDF.FIRST, first);
							Resource next = getRest(list);
							first = getFirst(next);
							Resource rest = getRest(next);
							removeStatements(list, RDF.REST, next);
							if (first != null) {
								removeStatements(next, RDF.FIRST, first);
								addStatement(list, RDF.FIRST, first);
							}
							if (rest != null) {
								removeStatements(next, RDF.REST, rest);
								addStatement(list, RDF.REST, rest);
							}
						} else {
							// remove index > 0
							Resource removedList = list;
							list = prevLists.remove(prevLists.size() - 1);
							Value first = getFirst(removedList);
							Resource rest = getRest(removedList);
							removeStatements(removedList, RDF.FIRST, first);
							removeStatements(removedList, RDF.REST, rest);
							removeStatements(list, RDF.REST, removedList);
							addStatement(list, RDF.REST, rest);
						}
						if (autoCommit)
							conn.setAutoCommit(true);
						removed = true;
						refresh();
					} finally {
						if (autoCommit && !conn.isAutoCommit()) {
							conn.rollback();
							conn.setAutoCommit(true);
						}
					}
				} catch (RepositoryException e) {
					throw new ObjectStoreException(e);
				}
			}

			public boolean hasNext() {
				Resource next;
				if (list == null) {
					next = getResource();
				} else {
					next = getRest(list);
				}
				return getFirst(next) != null;
			}

			public Object next() {
				if (list == null) {
					list = getResource();
				} else if (!removed) {
					prevLists.add(list);
					list = getRest(list);
				} else {
					removed = false;
				}
				Value first = getFirst(list);
				if (first == null)
					throw new NoSuchElementException();
				return createInstance(first);
			}

			public int nextIndex() {
				if (list == null)
					return 0;
				return prevLists.size() + 1;
			}

			public int previousIndex() {
				return prevLists.size() - 1;
			}

			public boolean hasPrevious() {
				return prevLists.size() > 0;
			}

			public Object previous() {
				list = prevLists.remove(prevLists.size() - 1);
				removed = false;
				Value first = getFirst(list);
				if (first == null)
					throw new NoSuchElementException();
				return createInstance(first);
			}

			private Object createInstance(Value first) {
				try {
					if (first instanceof Resource)
						return getObjectConnection()
								.getObject((Resource) first);
					return getObjectConnection().getObjectFactory()
							.createObject(((Literal) first));
				} catch (RepositoryException e) {
					throw new ObjectStoreException(e);
				}
			}
		};
	}

	@Override
	public String toString() {
		return super.toString();
	}

	Value getFirst(Resource list) {
		if (list == null)
			return null;
		try {
			CloseableIteration<Value, RepositoryException> stmts;
			stmts = getValues(list, RDF.FIRST, null);
			try {
				if (stmts.hasNext())
					return stmts.next();
				return null;
			} finally {
				stmts.close();
			}
		} catch (RepositoryException e) {
			throw new ObjectStoreException(e);
		}
	}

	Resource getRest(Resource list) {
		if (list == null)
			return null;
		try {
			CloseableIteration<Value, RepositoryException> stmts;
			stmts = getValues(list, RDF.REST, null);
			try {
				if (stmts.hasNext())
					return (Resource) stmts.next();
				return null;
			} finally {
				stmts.close();
			}
		} catch (RepositoryException e) {
			throw new ObjectStoreException(e);
		}
	}
}
