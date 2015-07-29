/*
 * Copyright (c) 2009-2010, James Leigh and Zepheira LLC Some rights reserved.
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
package org.openrdf.repository.object.composition;

import static java.lang.reflect.Modifier.isAbstract;
import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isProtected;
import static org.openrdf.repository.object.traits.RDFObjectBehaviour.GET_ENTITY_METHOD;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.repository.object.composition.helpers.BehaviourConstructor;
import org.openrdf.repository.object.exceptions.ObjectCompositionException;
import org.openrdf.repository.object.managers.PropertyMapper;
import org.openrdf.repository.object.traits.RDFObjectBehaviour;

/**
 * Creates subclasses of abstract behaviours that can be instaniated.
 * 
 * @author James Leigh
 * 
 */
public class AbstractClassFactory implements BehaviourProvider {

	private static final String BEAN_FIELD_NAME = "_$bean";
	private static final String CLASS_PREFIX = "object.behaviours.";
	private ClassFactory cp;
	private Set<Class<?>> bases;

	public void setClassDefiner(ClassFactory definer) {
		this.cp = definer;
	}

	public void setBaseClasses(Set<Class<?>> bases) {
		this.bases = bases;
	}

	public void setPropertyMapper(PropertyMapper mapper) {
		// don't need it
	}

	public Collection<? extends BehaviourFactory> getBehaviourFactories(
		Collection<Class<?>> classes) throws ObjectCompositionException {
		Set<Class<?>> faces = new HashSet<Class<?>>();
		for (Class<?> i : classes) {
			faces.add(i);
			faces = getImplementingClasses(i, faces);
		}
		List<BehaviourFactory> result = new ArrayList<BehaviourFactory>(faces.size());
		for (Class<?> concept : faces) {
			result.addAll(getBehaviourFactories(concept));
		}
		return result;
	}

	private Collection<BehaviourFactory> getBehaviourFactories(Class<?> concept) throws ObjectCompositionException {
		try {
			List<BehaviourFactory> result = new ArrayList<BehaviourFactory>();
			if (isEnhanceable(concept)) {
				for (Class<?> mapper : findImplementations(concept)) {
					result.add(new BehaviourConstructor(mapper));
				}
			}
			return result;
		} catch (ObjectCompositionException e) {
			throw e;
		} catch (Exception e) {
			throw new ObjectCompositionException(e);
		}
	}

	private Collection<? extends Class<?>> findImplementations(
			Class<?> concept) throws Exception {
		return Collections.singleton(findBehaviour(concept));
	}

	private boolean isBaseClass(Class<?> role) {
		return bases != null && bases.contains(role);
	}

	private final Class<?> findBehaviour(Class<?> concept) throws Exception {
		String className = getJavaClassName(concept);
		synchronized (cp) {
			try {
				return cp.classForName(className);
			} catch (ClassNotFoundException e2) {
				return implement(className, concept);
			}
		}
	}

	private ClassTemplate createBehaviourTemplate(String className,
			Class<?> concept) {
		ClassTemplate cc = createClassTemplate(className, concept);
		cc.addInterface(RDFObjectBehaviour.class);
		addNewConstructor(cc, concept);
		addRDFObjectBehaviourMethod(cc);
		return cc;
	}

	private boolean isOverridden(Method m) {
		if (m.getParameterTypes().length > 0)
			return false;
		if (RDFObjectBehaviour.GET_ENTITY_METHOD.equals(m.getName()))
			return true;
		return false;
	}

	private String getJavaClassName(Class<?> concept) {
		String suffix = getClass().getSimpleName().replaceAll("Factory$", "");
		return CLASS_PREFIX + concept.getName() + suffix;
	}

	private Class<?> implement(String className, Class<?> concept)
			throws Exception {
		ClassTemplate cc = createBehaviourTemplate(className, concept);
		enhance(cc, concept);
		return cp.createClass(cc);
	}

	private void addNewConstructor(ClassTemplate cc, Class<?> concept) {
		if (!concept.isInterface()) {
			try {
				concept.getConstructor(); // must have a default constructor
			} catch (NoSuchMethodException e) {
				throw new ObjectCompositionException(concept.getSimpleName()
						+ " must have a default constructor");
			}
		}
		cc.createField(Object.class, BEAN_FIELD_NAME);
		cc.addConstructor(new Class<?>[] { Object.class },
				BEAN_FIELD_NAME + " = $1;");
	}

	private void addRDFObjectBehaviourMethod(ClassTemplate cc) {
		cc.createMethod(Object.class,
				RDFObjectBehaviour.GET_ENTITY_METHOD).code("return ").code(
				BEAN_FIELD_NAME).code(";").end();
	}

	private Set<Class<?>> getImplementingClasses(Class<?> role,
			Set<Class<?>> implementations) {
		// don't consider interfaces or super classes
		return implementations;
	}

	private ClassTemplate createClassTemplate(String className, Class<?> role) {
		ClassTemplate cc = cp.createClassTemplate(className, role);
		cc.copyAnnotationsFrom(role);
		return cc;
	}

	private boolean isEnhanceable(Class<?> role) {
		return !role.isInterface() && isAbstract(role.getModifiers())
				&& !isBaseClass(role);
	}

	private void enhance(ClassTemplate cc, Class<?> c) throws Exception {
		if (Object.class.equals(c.getMethod("toString").getDeclaringClass())) {
			overrideToStringMethod(cc);
		}
		if (Object.class.equals(c.getMethod("equals", Object.class)
				.getDeclaringClass())
				&& Object.class.equals(c.getMethod("hashCode")
						.getDeclaringClass())) {
			overrideEqualsMethod(cc);
		}
		for (Method m : getMethods(c)) {
			if (isFinal(m.getModifiers()))
				continue;
			if (!isAbstract(m.getModifiers()))
				continue;
			if (isOverridden(m))
				continue;
			Class<?> r = m.getReturnType();
			Class<?>[] types = m.getParameterTypes();
			CodeBuilder code = cc.createInstancePrivateMethod(m);
			boolean isInterface = m.getDeclaringClass().isInterface();
			if (!isInterface) {
				code.code("try {");
			}
			if (!Void.TYPE.equals(r)) {
				code.code("return ($r) ");
			}
			if (isInterface) {
				code.code("(").castObject(m.getDeclaringClass()).code(BEAN_FIELD_NAME);
				code.code(").").code(m.getName()).code("($$);");
			} else {
				code.code(BEAN_FIELD_NAME).code(".getClass().getMethod(");
				code.insert(m.getName()).code(", ").insert(types).code(")")
						.code(".invoke(");
				code.code(BEAN_FIELD_NAME).code(", $args);");
			}
			if (!isInterface) {
				code.code("} catch (").code(
						InvocationTargetException.class.getName());
				code.code(" e) {throw e.getCause();}");
			}
			code.end();
		}
	}

	private Collection<Method> getMethods(Class<?> c) {
		List<Method> methods = new ArrayList<Method>();
		methods.addAll(Arrays.asList(c.getMethods()));
		HashMap<Object, Method> map = new HashMap<Object, Method>();
		Map<Object, Method> pms = getProtectedMethods(c, map);
		methods.addAll(pms.values());
		return methods;
	}

	private Map<Object, Method> getProtectedMethods(Class<?> c,
			Map<Object, Method> methods) {
		if (c == null)
			return methods;
		for (Method m : c.getDeclaredMethods()) {
			if (isProtected(m.getModifiers())) {
				Object types = Arrays.asList(m.getParameterTypes());
				Object key = Arrays.asList(m.getName(), types);
				if (!methods.containsKey(key)) {
					methods.put(key, m);
				}
			}
		}
		return getProtectedMethods(c.getSuperclass(), methods);
	}

	private void overrideToStringMethod(ClassTemplate cc) {
		try {
			Method toString = Object.class.getMethod("toString");
			MethodBuilder m = cc.createInstancePrivateMethod(toString);
			m.code("return ").code(GET_ENTITY_METHOD);
			m.code("().toString()").semi().end();
		} catch (NoSuchMethodException e) {
			throw new AssertionError(e);
		}
	}

	private void overrideEqualsMethod(ClassTemplate cc) {
		try {
			Method hashCode = Object.class.getMethod("hashCode");
			MethodBuilder m = cc.createInstancePrivateMethod(hashCode);
			m.code("return ").code(GET_ENTITY_METHOD);
			m.code("().hashCode()").semi().end();
			Method equals = Object.class.getMethod("equals", Object.class);
			m = cc.createInstancePrivateMethod(equals);
			m.code("return ").code(GET_ENTITY_METHOD);
			m.code("().equals($1)").semi().end();
		} catch (NoSuchMethodException e) {
			throw new AssertionError(e);
		}
	}

}
