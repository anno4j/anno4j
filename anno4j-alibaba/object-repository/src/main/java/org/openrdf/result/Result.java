/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2009.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.openrdf.result;

import info.aduna.iteration.CloseableIteration;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.openrdf.query.QueryEvaluationException;

/**
 * Super type of all query result types (TupleQueryResult, GraphQueryResult,
 * etc.).
 * 
 * @author Arjohn Kampman
 * @author James Leigh
 */
public interface Result<T> extends CloseableIteration<T, QueryEvaluationException> {

	/**
	 * Returns the next element from this cursor.
	 * 
	 * @return the next element from this cursor, or <tt>null</tt> if the cursor
	 *         has no more elements.
	 */
	public T next()
		throws QueryEvaluationException;

	/**
	 * Closes this cursor, freeing any resources that it is holding. If the
	 * cursor has already been closed then invoking this method has no effect.
	 * After closing a cursor, any subsequent calls to {@link #next()} will
	 * return <tt>null</tt>.
	 * <p>
	 * Note to implementors: this method is also used to abort long running
	 * evaluations. It should be implemented in such a way that it can be called
	 * concurrently with {@link #next()} and that it stops evaluation as soon as
	 * possible. Calls to {@link #next()} that are already in progress are
	 * allowed to still return results, but after returning from {@link #close()}
	 * , the cursor must not produce any more results.
	 */
	public void close()
		throws QueryEvaluationException;

	/**
	 * Describes this cursor (recursively for any wrapped cursors).
	 */
	public String toString();

	public boolean hasNext()
		throws QueryEvaluationException;

	public T singleResult()
		throws QueryEvaluationException, NoResultException, MultipleResultException;

	public List<T> asList()
		throws QueryEvaluationException;

	public Set<T> asSet()
		throws QueryEvaluationException;

	public <C extends Collection<? super T>> C addTo(C collection)
		throws QueryEvaluationException;
}
