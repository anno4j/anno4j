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
package org.openrdf.repository.object.advice.behaviour;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.openrdf.repository.object.advice.Advice;
import org.openrdf.repository.object.advice.AdviceFactory;
import org.openrdf.repository.object.advice.AdviceService;
import org.openrdf.repository.object.composition.BehaviourFactory;
import org.openrdf.repository.object.composition.BehaviourProvider;
import org.openrdf.repository.object.composition.ClassFactory;
import org.openrdf.repository.object.exceptions.ObjectCompositionException;
import org.openrdf.repository.object.managers.PropertyMapper;

/**
 * Searhes a class' methods for annotations that have advice.
 */
public class AdviceBehaviourProvider implements BehaviourProvider {
	private final AdviceService service = AdviceService.newInstance();

	public void setClassDefiner(ClassFactory definer) {
		// TODO Auto-generated method stub

	}

	public void setBaseClasses(Set<Class<?>> bases) {
		// TODO Auto-generated method stub

	}

	public void setPropertyMapper(PropertyMapper mapper) {
		// TODO Auto-generated method stub

	}

	public Collection<? extends BehaviourFactory> getBehaviourFactories(
			Collection<Class<?>> classes) throws ObjectCompositionException {
		List<AdviceBehaviourFactory> list = new ArrayList<AdviceBehaviourFactory>();
		for (Class<?> cls : classes) {
			addAdvisers(cls, list);
		}
		return list;
	}

	private void addAdvisers(Class<?> cls, List<AdviceBehaviourFactory> list) {
		for (Method method : cls.getDeclaredMethods()) {
			if (isPublicOrProtected(method)) {
				addAdvisers(method, list);
			}
		}
		for (Class<?> face : cls.getInterfaces()) {
			addAdvisers(face, list);
		}
		if (cls.getSuperclass() != null) {
			addAdvisers(cls.getSuperclass(), list);
		}
	}

	private boolean isPublicOrProtected(Method method) {
		int modifiers = method.getModifiers();
		return Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers);
	}

	private void addAdvisers(Method method, List<AdviceBehaviourFactory> list) {
		for (Annotation ann : method.getAnnotations()) {
			Class<? extends Annotation> t = ann.annotationType();
			AdviceFactory f = service.getAdviserFactory(t);
			if (f != null) {
				Advice a = f.createAdvice(method);
				if (a != null) {
					list.add(new AdviceBehaviourFactory(a, method, t));
				}
			}
		}
	}

}
