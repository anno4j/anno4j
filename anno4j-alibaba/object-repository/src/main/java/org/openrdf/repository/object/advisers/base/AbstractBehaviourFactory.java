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
package org.openrdf.repository.object.advisers.base;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.annotations.ParameterTypes;
import org.openrdf.repository.object.composition.BehaviourFactory;

/**
 * Implements common methods for BehaviourFactory.
 */
public abstract class AbstractBehaviourFactory implements BehaviourFactory {
	private final Class<?> behaviourType;
	private final Class<?>[] interfaces;
	private final Method intercept;
	private final Map<String, List<Method>> intercepting = new HashMap<String, List<Method>>();

	public AbstractBehaviourFactory(Class<?> behaviourType, Class<?>[] interfaces, Method intercept, Method[] intercepting) {
		assert behaviourType != null;
		assert interfaces != null;
		assert intercept != null;
		assert intercepting != null;
		this.behaviourType = behaviourType;
		this.interfaces = interfaces;
		this.intercept = intercept;
		for (Method m : intercepting) {
			List<Method> list = this.intercepting.get(m.getName());
			if (list == null) {
				list = new ArrayList<Method>();
				this.intercepting.put(m.getName(), list);
			}
			list.add(m);
		}
	}

	public String toString() {
		return getName();
	}

	public String getName() {
		return getBehaviourType().getSimpleName();
	}

	public Class<?> getBehaviourType() {
		return behaviourType;
	}

	public Class<?>[] getInterfaces() {
		return interfaces;
	}

	public Method[] getMethods() {
		List<Method> list = new ArrayList<Method>();
		for (Class<?> face : interfaces) {
			for (Method m : face.getMethods()) {
				if (!m.equals(intercept)) {
					list.add(m);
				}
			}
		}
		for (List<Method> methods : intercepting.values()) {
			for (Method m : methods) {
				list.add(m);
			}
		}
		return list.toArray(new Method[list.size()]);
	}

	public synchronized Method getInvocation(Method method) {
		if (method.getDeclaringClass().isAssignableFrom(behaviourType))
			return method;
		if (intercepting.containsKey(method.getName())) {
			Class<?>[] ptypes = method.getParameterTypes();
			for (Method m : intercepting.get(method.getName())) {
				if (Arrays.equals(ptypes, m.getParameterTypes()))
					return intercept;
			}
			if (method.isAnnotationPresent(ParameterTypes.class)){
				Class<?>[] aptypes = method.getAnnotation(ParameterTypes.class).value();
				for (Method m : intercepting.get(method.getName())) {
					if (Arrays.equals(aptypes, m.getParameterTypes()))
						return intercept;
				}
			}
		}
		return null;
	}

	public boolean isSingleton() {
		return false;
	}

	public Object getSingleton() {
		return null;
	}

}
