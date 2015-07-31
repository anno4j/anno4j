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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.exceptions.MultipleObjectResultException;
import org.openrdf.repository.object.exceptions.NoObjectResultException;
import org.openrdf.repository.object.exceptions.ObjectPersistException;
import org.openrdf.repository.object.exceptions.ObjectStoreException;
import org.openrdf.repository.object.exceptions.RDFObjectException;

/**
 * A general purpose iteration wrapping Sesame's iterations. This class converts
 * the results, converts the Exceptions into {@link RDFObjectException}s,
 * and ensures that the iteration is closed when all values have been read (on {
 * {@link #next()}).
 * 
 * @author James Leigh
 * 
 * @param <S>
 *            Type of the delegate (Statement)
 * @param <E>
 *            Type of the result
 */
public abstract class ObjectIterator<S, E> implements Iterator<E> {

	public static void close(Iterator<?> iter) {
		if (iter instanceof ObjectIterator)
			((ObjectIterator) iter).close();
	}

	private CloseableIteration<? extends S, ?> delegate;

	private S element;

	private S next;

	public ObjectIterator(CloseableIteration<? extends S, ?> delegate) {
		this.delegate = delegate;
		try {
			if (!hasNext()) {
				close();
			}
		} catch (RuntimeException e) {
			close();
			throw e;
		}
	}

	public boolean hasNext() {
		try {
			return next != null || (next = delegateNext()) != null;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new ObjectStoreException(e);
		}
	}

	public E next() {
		try {
			S next = element = delegateNext();
			if (next == null) {
				close();
				return null;
			} else if (!hasNext()) {
				close();
			}
			return convert(next);
		} catch (Exception e) {
			throw new ObjectStoreException(e);
		}
	}

	public void remove() {
		try {
			remove(element);
		} catch (RepositoryException e) {
			throw new ObjectPersistException(e);
		}
	}

	public void close() {
		try {
			delegate.close();
		} catch (Exception e) {
			throw new ObjectStoreException(e);
		}
	}

	public E singleResult() throws RepositoryException {
		try {
			E next = next();
			if (next == null)
				throw new NoObjectResultException("No result");
			if (next() != null)
				throw new MultipleObjectResultException("More than one result");
			return next;
		} finally {
			close();
		}
	}

	public List<E> asList() throws RepositoryException {
		return addTo(new ArrayList<E>());
	}

	public Set<E> asSet() throws RepositoryException {
		return addTo(new HashSet<E>());
	}

	public <C extends Collection<? super E>> C addTo(C collection)
			throws RepositoryException {
		try {
			E next;
			while ((next = next()) != null) {
				collection.add(next);
			}

			return collection;
		} finally {
			close();
		}
	}

	protected E convert(S element) throws RepositoryException {
		return (E) element;
	}

	protected void remove(S element) throws RepositoryException {
		throw new UnsupportedOperationException();
	}

	private S delegateNext() throws Exception {
		S result = next;
		if (result == null && delegate.hasNext()) {
			return delegate.next();
		}
		next = null;
		return result;
	}
}
