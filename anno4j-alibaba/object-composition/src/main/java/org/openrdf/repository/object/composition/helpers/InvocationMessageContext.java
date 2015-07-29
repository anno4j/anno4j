/*
 * Copyright (c) 2007-2009, James Leigh All rights reserved.
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
package org.openrdf.repository.object.composition.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.exceptions.BehaviourException;
import org.openrdf.repository.object.traits.BooleanMessage;
import org.openrdf.repository.object.traits.ByteMessage;
import org.openrdf.repository.object.traits.CharacterMessage;
import org.openrdf.repository.object.traits.DoubleMessage;
import org.openrdf.repository.object.traits.FloatMessage;
import org.openrdf.repository.object.traits.IntegerMessage;
import org.openrdf.repository.object.traits.LongMessage;
import org.openrdf.repository.object.traits.MessageContext;
import org.openrdf.repository.object.traits.ObjectMessage;
import org.openrdf.repository.object.traits.ShortMessage;
import org.openrdf.repository.object.traits.VoidMessage;

/**
 * Implements the Message interface(s) through an InvocationHandler.
 * 
 * @author James Leigh
 * 
 */
public class InvocationMessageContext implements ObjectMessage {

	private static class Invocation implements MessageContext {
		protected final ObjectMessage delegate;

		public Invocation(ObjectMessage delegate) {
			this.delegate = delegate;
		}

		public Object getTarget() {
			return delegate.getTarget();
		}

		public Method getMethod() {
			return delegate.getMethod();
		}

		public Object[] getParameters() {
			return delegate.getParameters();
		}

		public void setParameters(Object[] parameters) {
			delegate.setParameters(parameters);
		}
	}

	private static class BooleanInvocation extends Invocation implements
			BooleanMessage {
		public BooleanInvocation(ObjectMessage delegate) {
			super(delegate);
		}

		public boolean proceed() throws Exception {
			return (Boolean) delegate.proceed();
		}
	}

	private static class ByteInvocation extends Invocation implements
			ByteMessage {
		public ByteInvocation(ObjectMessage delegate) {
			super(delegate);
		}

		public byte proceed() throws Exception {
			return (Byte) delegate.proceed();
		}
	}

	private static class CharacterInvocation extends Invocation implements
			CharacterMessage {
		public CharacterInvocation(ObjectMessage delegate) {
			super(delegate);
		}

		public char proceed() throws Exception {
			return (Character) delegate.proceed();
		}
	}

	private static class DoubleInvocation extends Invocation implements
			DoubleMessage {
		public DoubleInvocation(ObjectMessage delegate) {
			super(delegate);
		}

		public double proceed() throws Exception {
			return (Double) delegate.proceed();
		}
	}

	private static class FloatInvocation extends Invocation implements
			FloatMessage {
		public FloatInvocation(ObjectMessage delegate) {
			super(delegate);
		}

		public float proceed() throws Exception {
			return (Float) delegate.proceed();
		}
	}

	private static class IntegerInvocation extends Invocation implements
			IntegerMessage {
		public IntegerInvocation(ObjectMessage delegate) {
			super(delegate);
		}

		public int proceed() throws Exception {
			return (Integer) delegate.proceed();
		}
	}

	private static class LongInvocation extends Invocation implements
			LongMessage {
		public LongInvocation(ObjectMessage delegate) {
			super(delegate);
		}

		public long proceed() throws Exception {
			return (Long) delegate.proceed();
		}
	}

	private static class ShortInvocation extends Invocation implements
			ShortMessage {
		public ShortInvocation(ObjectMessage delegate) {
			super(delegate);
		}

		public short proceed() throws Exception {
			return (Short) delegate.proceed();
		}
	}

	private static class VoidInvocation extends Invocation implements
			VoidMessage {
		public VoidInvocation(ObjectMessage delegate) {
			super(delegate);
		}

		public void proceed() throws Exception {
			delegate.proceed();
		}
	}

	private final Object target;

	private final Method method;

	private Object[] parameters;

	private final List<Object> invokeTarget = new ArrayList<Object>();

	private final List<Method> invokeMethod = new ArrayList<Method>();

	private int count;

	public InvocationMessageContext(Object target, Method method,
			Object[] parameters) {
		this.target = target;
		this.method = method;
		this.parameters = parameters;
	}

	public synchronized InvocationMessageContext appendInvocation(Object target,
			Method method) {
		invokeTarget.add(target);
		invokeMethod.add(method);
		return this;
	}

	@Override
	public synchronized String toString() {
		String params = Arrays.asList(parameters).toString();
		String values = params.substring(1, params.length() - 1);
		return method.getName() + "(" + values + ")";
	}

	public Method getMethod() {
		return method;
	}

	public synchronized Object[] getParameters() {
		return parameters;
	}

	public synchronized void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

	public synchronized Object proceed() throws Exception {
		try {
			while (true) {
				Class<?> responseType = method.getReturnType();
				if (count >= invokeTarget.size()) {
					return nil(responseType);
				}
				Method im = invokeMethod.get(count);
				Object it = invokeTarget.get(count);
				count++;
				Class<?>[] param = im.getParameterTypes();
				Class<?> resultType = im.getReturnType();
				if (param.length == 1
						&& MessageContext.class.isAssignableFrom(param[0])) {
					Object result = im.invoke(it, returns(resultType));
					return cast(result, resultType, responseType);
				} else {
					Object result = im.invoke(it, getParameters(im));
					if (isNil(result, resultType))
						continue;
					return cast(result, resultType, responseType);
				}
			}
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			if (cause instanceof Exception)
				throw (Exception) cause;
			if (cause instanceof Error)
				throw (Error) cause;
			throw new BehaviourException(cause);
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (IllegalAccessException e) {
			IllegalAccessError error = new IllegalAccessError(e.getMessage());
			error.initCause(e);
			throw error;
		}
	}

	public Object getTarget() {
		return target;
	}

	private MessageContext returns(Class<?> returnType) {
		if (!returnType.isPrimitive())
			return this;
		if (Boolean.TYPE.equals(returnType))
			return new BooleanInvocation(this);
		if (Byte.TYPE.equals(returnType))
			return new ByteInvocation(this);
		if (Character.TYPE.equals(returnType))
			return new CharacterInvocation(this);
		if (Double.TYPE.equals(returnType))
			return new DoubleInvocation(this);
		if (Float.TYPE.equals(returnType))
			return new FloatInvocation(this);
		if (Integer.TYPE.equals(returnType))
			return new IntegerInvocation(this);
		if (Long.TYPE.equals(returnType))
			return new LongInvocation(this);
		if (Short.TYPE.equals(returnType))
			return new ShortInvocation(this);
		if (Void.TYPE.equals(returnType))
			return new VoidInvocation(this);
		throw new AssertionError("Unknown primitive: " + returnType);
	}

	private Object cast(Object result, Class<?> resultType,
			Class<?> responseType) {
		if (isNil(result, resultType))
			return nil(responseType);
		if (resultType.equals(responseType) || Object.class.equals(resultType))
			return result;
		if (responseType.equals(Set.class))
			return Collections.singleton(result);
		if (resultType.equals(Set.class)) {
			Set<?> set = (Set<?>) result;
			if (set.isEmpty())
				return nil(responseType);
			return set.iterator().next();
		}
		return result;
	}

	private boolean isNil(Object result, Class<?> type) {
		if (result == null)
			return true;
		if (!type.isPrimitive())
			return false;
		return result.equals(nil(type));
	}

	private Object nil(Class<?> type) {
		if (Set.class.equals(type))
			return Collections.emptySet();
		if (!type.isPrimitive())
			return null;
		if (Void.TYPE.equals(type))
			return null;
		if (Boolean.TYPE.equals(type))
			return Boolean.FALSE;
		if (Character.TYPE.equals(type))
			return Character.valueOf((char) 0);
		if (Byte.TYPE.equals(type))
			return Byte.valueOf((byte) 0);
		if (Short.TYPE.equals(type))
			return Short.valueOf((short) 0);
		if (Integer.TYPE.equals(type))
			return Integer.valueOf((int) 0);
		if (Long.TYPE.equals(type))
			return Long.valueOf((long) 0);
		if (Float.TYPE.equals(type))
			return Float.valueOf((float) 0);
		if (Double.TYPE.equals(type))
			return Double.valueOf((double) 0);
		throw new AssertionError();
	}

	private int getParameterIndex(String uri) {
		Annotation[][] anns = method.getParameterAnnotations();
		for (int i = 0; i < anns.length; i++) {
			for (int j = 0; j < anns[i].length; j++) {
				if (anns[i][j].annotationType().equals(Iri.class)) {
					if (((Iri) anns[i][j]).value().equals(uri)) {
						return i;
					}
				}
			}
		}
		throw new UnsupportedOperationException("Parameter not found: " + uri);
	}

	private Object[] getParameters(Method method) {
		Object[] parameters = getParameters();
		Annotation[][] anns = method.getParameterAnnotations();
		Object[] result = new Object[anns.length];
		for (int i = 0; i < anns.length; i++) {
			if (i < parameters.length) {
				// if no @rdf copy over parameter by position
				result[i] = parameters[i];
			}
			for (int j = 0; j < anns[i].length; j++) {
				if (anns[i][j].annotationType().equals(Iri.class)) {
					String uri = ((Iri) anns[i][j]).value();
					result[i] = parameters[getParameterIndex(uri)];
				}
			}
		}
		return result;
	}

}
