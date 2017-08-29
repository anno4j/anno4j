/*
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

import java.lang.reflect.Method;

import org.openrdf.annotations.ParameterTypes;
import org.openrdf.annotations.Precedes;
import org.openrdf.repository.object.advice.Advice;
import org.openrdf.repository.object.composition.BehaviourFactory;

/**
 * Represents an aspect in a behaviour class.
 *
 * @author James Leigh
 **/
public class BehaviourMethod {
	private final BehaviourFactory factory;
	private final Method method;

	public BehaviourMethod(BehaviourFactory behaviour, Method method) {
		assert behaviour != null;
		assert method != null;
		this.factory = behaviour;
		this.method = method;
	}

	public BehaviourFactory getFactory() {
		return factory;
	}

	public Method getMethod() {
		return method;
	}

	public boolean isMessage() {
		return method.isAnnotationPresent(ParameterTypes.class)
				|| method.getDeclaringClass().equals(Advice.class);
	}

	public boolean isEmptyOverridesPresent() {
		Precedes ann = factory.getBehaviourType().getAnnotation(Precedes.class);
		if (ann == null)
			return false;
		Class<?>[] values = ann.value();
		return values != null && values.length == 0;
	}

	public boolean precedes(BehaviourMethod b1) {
		return factory.precedes(getMethod(), b1.getFactory(), b1.getMethod());
	}

	@Override
	public String toString() {
		return factory.toString();
	}
}
