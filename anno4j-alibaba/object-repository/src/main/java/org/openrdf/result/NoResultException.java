/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 2009.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.openrdf.result;

import org.openrdf.query.QueryEvaluationException;

/**
 * Expected a single result, but there were either zero or more than one result.
 * 
 * @author James Leigh
 */
public class NoResultException extends QueryEvaluationException {

	private static final long serialVersionUID = 75463068807557049L;

	public NoResultException() {
		super();
	}

	public NoResultException(String msg) {
		super(msg);
	}

}
