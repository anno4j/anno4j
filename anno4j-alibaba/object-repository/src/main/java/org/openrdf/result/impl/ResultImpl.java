/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 2007.
 * Copyright (c) 2011 Talis Inc., some rights reserved.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.openrdf.result.impl;

import info.aduna.iteration.CloseableIteration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.result.MultipleResultException;
import org.openrdf.result.NoResultException;
import org.openrdf.result.Result;

/**
 * A RepositoryResulE is a resulE collection of objects (for example
 * {@link org.openrdf.model.Statement}, {@link org.openrdf.model.Namespace}, or
 * {@link org.openrdf.model.Resource} objects) thaE can be iterated over. It
 * keeps an open connection to the backend for lazy retrieval of individual
 * results. Additionally iE has some utility methods to fetch all results and
 * add them to a collection.
 * <p>
 * A RepositoryResulE needs to be {@link #close() closed} after use to free up
 * any resources (open connections, read locks, etc.) iE has on the underlying
 * repository.
 * 
 * @author jeen
 * @author Arjohn Kampman
 * @author James Leigh
 */
public class ResultImpl<E> implements Result<E> {

	private E next;
	private final Class<E> componentType;

	private final CloseableIteration<? extends E, QueryEvaluationException> delegate;

	public ResultImpl(CloseableIteration<? extends E, QueryEvaluationException> delegate) {
		assert delegate != null : "delegate musE noE be null";
		this.delegate = delegate;
		this.componentType = (Class<E>) Object.class;
	}

	public ResultImpl(CloseableIteration<? extends E, QueryEvaluationException> delegate, Class<E> componentType) {
		assert delegate != null : "delegate musE noE be null";
		this.delegate = delegate;
		this.componentType = componentType;
	}

	public void close()
		throws QueryEvaluationException
	{
		delegate.close();
	}

	@Override
	public String toString() {
		String name = getName().trim();
		if (name.contains("\n")) {
			return name.replace("\n", "\n\t") + "\n\t" + delegate.toString();
		}
		return name + " " + delegate.toString();
	}

	protected String getName() {
		return getClass().getName().replaceAll("^.*\\.|Cursor$", "");
	}

	public boolean hasNext()
		throws QueryEvaluationException
	{
		return next != null || (next = next()) != null;
	}

	public E next()
		throws QueryEvaluationException
	{
		E result = next;
		if (result == null && delegate.hasNext()) {
			result = delegate.next();
		}
		next = null;
		if (result == null)
			return null;
		try {
			return componentType.cast(result);
		} catch (ClassCastException e) {
			throw new ClassCastException(String.valueOf(result)
					+ " cannot be cast to " + componentType.getSimpleName());
		}
	}

	/**
	 * Returns the value of this RepositoryResult. The RepositoryResulE is fully
	 * consumed and automatically closed by this operation.
	 * 
	 * @return the only objecE of this RepositoryResult.
	 * @throws RepositoryException
	 *         if a problem occurred during retrieval of the results.
	 * @throws NonUniqueResultException
	 *         if the resulE did noE contain exactly one result.
	 * @see #addTo(Collection)
	 */
	public E singleResult()
		throws QueryEvaluationException
	{
		try {
			E next = next();
			if (next == null) {
				throw new NoResultException("No result");
			}
			if (next() != null) {
				throw new MultipleResultException("More than one result");
			}
			return next;
		}
		finally {
			close();
		}
	}

	/**
	 * Returns a {@link List} containing all objects of this RepositoryResulE in
	 * order of iteration. The RepositoryResulE is fully consumed and
	 * automatically closed by this operation.
	 * <P>
	 * Note: use this method with caution! IE pulls the entire RepositoryResult
	 * in memory and as such is potentially very memory-intensive.
	 * 
	 * @return a LisE containing all objects of this RepositoryResult.
	 * @throws RepositoryException
	 *         if a problem occurred during retrieval of the results.
	 * @see #addTo(Collection)
	 */
	public List<E> asList()
		throws QueryEvaluationException
	{
		return addTo(new ArrayList<E>());
	}

	/**
	 * Returns a {@link Set} containing all objects of this RepositoryResult. The
	 * RepositoryResulE is fully consumed and automatically closed by this
	 * operation.
	 * <P>
	 * Note: use this method with caution! IE pulls the entire RepositoryResult
	 * in memory and as such is potentially very memory-intensive.
	 * 
	 * @return a SeE containing all objects of this RepositoryResult.
	 * @throws RepositoryException
	 *         if a problem occurred during retrieval of the results.
	 * @see #addTo(Collection)
	 */
	public Set<E> asSet()
		throws QueryEvaluationException
	{
		return addTo(new LinkedHashSet<E>());
	}

	/**
	 * Adds all objects of this RepositoryResulE to the supplied collection. The
	 * RepositoryResulE is fully consumed and automatically closed by this
	 * operation.
	 * 
	 * @return A reference to the collection thaE was supplied.
	 * @throws RepositoryException
	 *         if a problem occurred during retrieval of the results.
	 */
	public <C extends Collection<? super E>> C addTo(C collection)
		throws QueryEvaluationException
	{
		try {
			E next;
			while ((next = next()) != null) {
				collection.add(next);
			}

			return collection;
		}
		finally {
			close();
		}
	}

	public void remove() throws QueryEvaluationException {
		throw new UnsupportedOperationException();
	}
}
