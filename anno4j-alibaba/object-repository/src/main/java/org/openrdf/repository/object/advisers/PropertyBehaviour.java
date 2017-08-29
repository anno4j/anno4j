/*
 * Copyright (c) 2012 3 Round Stones Inc., Some rights reserved.
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
package org.openrdf.repository.object.advisers;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;
import java.util.Set;

import org.openrdf.query.BindingSet;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.advice.Advice;
import org.openrdf.repository.object.advisers.helpers.PropertySet;
import org.openrdf.repository.object.traits.Mergeable;
import org.openrdf.repository.object.traits.ObjectMessage;
import org.openrdf.repository.object.traits.PropertyConsumer;
import org.openrdf.repository.object.traits.Refreshable;

/**
 * Reads and writes properties from an RDF store.
 */
public final class PropertyBehaviour implements Advice, Mergeable,
		Refreshable, PropertyConsumer {
	private final Class<?> concept;
	private final Class<?> type;
	private final PropertyDescriptor pd;
	private final PropertySet property;

	public PropertyBehaviour(PropertySet property, PropertyDescriptor pd) {
		assert pd != null;
		assert property != null;
		this.concept = pd.getReadMethod().getDeclaringClass();
		this.type = pd.getReadMethod().getReturnType();
		this.pd = pd;
		this.property = property;
	}

	@Override
	public String toString() {
		return pd.toString();
	}

	public void usePropertyBindings(String binding, List<BindingSet> results) {
		if (property instanceof PropertyConsumer) {
			String var = binding + "_" + pd.getName();
			if (results.get(0).getBindingNames().contains(var)) {
				PropertyConsumer pc = (PropertyConsumer) property;
				pc.usePropertyBindings(var, results);
			}
		}
	}

	public void refresh() {
		property.refresh();
	}

	public void merge(Object source) throws RepositoryException {
		if (concept.isAssignableFrom(source.getClass())) {
			try {
				Object value = pd.getReadMethod().invoke(source);
				if (value != null) {
					if (Set.class.equals(this.type)) {
						property.addAll((Set<?>) value);
					} else {
						property.add(value);
					}
				}
			} catch (IllegalArgumentException e) {
				throw new AssertionError(e);
			} catch (IllegalAccessException e) {
				IllegalAccessError error;
				error = new IllegalAccessError(e.getMessage());
				error.initCause(e);
				throw error;
			} catch (InvocationTargetException e) {
				try {
					throw e.getCause();
				} catch (Error error) {
					throw error;
				} catch (RuntimeException runtime) {
					throw runtime;
				} catch (RepositoryException repository) {
					throw repository;
				} catch (Throwable throwable) {
					throw new UndeclaredThrowableException(throwable);
				}
			}
		}
	}

	public Object intercept(ObjectMessage message) throws Exception {
		Class<?> type = message.getMethod().getReturnType();
		if (Void.TYPE.equals(type)) {
			if (Set.class.equals(message.getMethod().getParameterTypes()[0])) {
				property.setAll((Set<?>) message.getParameters()[0]);
			} else {
				property.setSingle(message.getParameters()[0]);
			}
			return message.proceed();
		} else if (Set.class.equals(type)) {
			return property.getAll();
		} else if (type.isPrimitive()) {
			Object result = property.getSingle();
			if (result == null)
				return message.proceed();
			return result;
		} else if (String.class.equals(type)) {
			Object result = property.getSingle();
			if (result == null)
				return message.proceed();
			return result.toString();
		} else {
			try {
				Object result = type.cast(property.getSingle());
				if (result == null)
					return message.proceed();
				return result;
			} catch (ClassCastException e) {
				throw new ClassCastException(property.getSingle() + " cannot be cast to " + type.getName());
			}
		}
	}
}
