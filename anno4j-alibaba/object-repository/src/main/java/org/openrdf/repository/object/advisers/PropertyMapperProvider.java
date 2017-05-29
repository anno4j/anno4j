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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javassist.NotFoundException;

import org.openrdf.repository.object.advice.Advice;
import org.openrdf.repository.object.advisers.base.AbstractBehaviourFactory;
import org.openrdf.repository.object.advisers.helpers.PropertySetFactory;
import org.openrdf.repository.object.composition.BehaviourFactory;
import org.openrdf.repository.object.composition.BehaviourProvider;
import org.openrdf.repository.object.composition.ClassFactory;
import org.openrdf.repository.object.composition.ClassTemplate;
import org.openrdf.repository.object.exceptions.ObjectCompositionException;
import org.openrdf.repository.object.managers.PropertyMapper;
import org.openrdf.repository.object.traits.ObjectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates BehaviourFactories to modify classes and changed their @Iri annotated
 * properties to read/write to an RDF store.
 */
public class PropertyMapperProvider implements BehaviourProvider {
	private static final Method intercept;
	static {
		try {
			intercept = Advice.class.getMethod("intercept",
					ObjectMessage.class);
		} catch (NoSuchMethodException e) {
			throw new AssertionError(e);
		}
	}

	private final Logger logger = LoggerFactory.getLogger(PropertyMapperProvider.class);
	private ClassFactory cp;
	private PropertyMapper properties;

	public void setClassDefiner(ClassFactory definer) {
		this.cp = definer;
	}

	public void setBaseClasses(Set<Class<?>> bases) {
		// TODO Auto-generated method stub

	}

	public void setPropertyMapper(PropertyMapper mapper) {
		this.properties = mapper;
	}

	public Collection<? extends BehaviourFactory> getBehaviourFactories(
			Collection<Class<?>> classes) throws ObjectCompositionException {
		Set<Class<?>> faces = new HashSet<Class<?>>();
		for (Class<?> i : classes) {
			faces.add(i);
			faces = getSuperClasses(i, faces);
		}
		List<BehaviourFactory> result;
		result = new ArrayList<BehaviourFactory>(faces.size());
		for (Class<?> concept : faces) {
			result.addAll(getBehaviourFactories(concept));
		}
		return result;
	}

	private Set<Class<?>> getSuperClasses(Class<?> role, Set<Class<?>> set) {
		for (Class<?> face : role.getInterfaces()) {
			if (!set.contains(face)) {
				set.add(face);
				getSuperClasses(face, set);
			}
		}
		Class<?> superclass = role.getSuperclass();
		if (superclass != null) {
			set.add(superclass);
			getSuperClasses(superclass, set);
		}
		return set;
	}

	private Collection<? extends BehaviourFactory> getBehaviourFactories(
			Class<?> concept) {
		List<BehaviourFactory> result = new ArrayList<BehaviourFactory>();
		for (Field field : findAllFields(concept, new ArrayList<Field>())) {
			result.add(createFieldAdviser(concept, field));
		}
		for (PropertyDescriptor pd : properties.findProperties(concept)) {
			result.add(createPropertyAdviser(pd));
		}
		return result;
	}

	private Collection<Field> findAllFields(Class<?> concept, Collection<Field> fields) {
		fields.addAll(properties.findFields(concept));
		Class<?> superclass = concept.getSuperclass();
		if (superclass != null) {
			findAllFields(superclass, fields);
		}
		return fields;
	}

	private BehaviourFactory createFieldAdviser(final Class<?> role, final Field field) {
		final PropertySetFactory factory = new PropertySetFactory(
				field, properties.findPredicate(field));
		List<Method> methods = new ArrayList<Method>();
		for (Method method : role.getDeclaredMethods()) {
			ClassTemplate t = cp.loadClassTemplate(role);
			try {
				Set<Field> fieldsRead = getMappedFieldsRead(t, method);
				Set<Field> fieldsWriten = getMappedFieldsWritten(t, method);
				if (fieldsRead.contains(field) || fieldsWriten.contains(field)) {
					methods.add(method);
				}
			} catch (NotFoundException e) {
				logger.warn(e.toString(), e);
			}
		}
		Class<?>[] interfaces = PropertyBehaviour.class.getInterfaces();
		final Method[] ar = methods.toArray(new Method[methods.size()]);
		return new AbstractBehaviourFactory(FieldBehaviour.class, interfaces,
				intercept, ar) {
			public boolean precedes(Method in, BehaviourFactory factory,
					Method to) {
				for (Method m : ar) {
					if (m.getName().equals(to.getName())
							&& Arrays.equals(m.getParameterTypes(),
									to.getParameterTypes()))
						return role
								.isAssignableFrom(factory.getBehaviourType());
				}
				return false;
			}

			public FieldBehaviour newInstance(Object proxy) throws Throwable {
				return new FieldBehaviour(factory.createPropertySet(proxy), field, proxy);
			}

			public String getName() {
				return field.getName();
			}
		};
	}

	private Set<Field> getMappedFieldsRead(ClassTemplate t, Method method)
			throws NotFoundException {
		Set<Field> fields = t.getFieldsRead(method);
		Iterator<Field> iter = fields.iterator();
		while (iter.hasNext()) {
			Field field = iter.next();
			if (!properties.isMappedField(field)) {
				iter.remove();
			}
		}
		return fields;
	}

	private Set<Field> getMappedFieldsWritten(ClassTemplate t, Method method)
			throws NotFoundException {
		Set<Field> fields = t.getFieldsWritten(method);
		Iterator<Field> iter = fields.iterator();
		while (iter.hasNext()) {
			Field field = iter.next();
			if (!properties.isMappedField(field)) {
				iter.remove();
			}
		}
		return fields;
	}

	private BehaviourFactory createPropertyAdviser(final PropertyDescriptor pd) {
		final PropertySetFactory factory = new PropertySetFactory(pd,
				properties.findPredicate(pd));
		List<Method> two = new ArrayList<Method>();
		if (pd.getReadMethod() != null) {
			two.add(pd.getReadMethod());
		}
		if (pd.getWriteMethod() != null) {
			two.add(pd.getWriteMethod());
		}
		Class<?>[] interfaces = PropertyBehaviour.class.getInterfaces();
		Method[] methods = two.toArray(new Method[two.size()]);
		return new AbstractBehaviourFactory(PropertyBehaviour.class,
				interfaces, intercept, methods) {
			public boolean precedes(Method in, BehaviourFactory factory, Method to) {
				Method r = pd.getReadMethod();
				if (r.getName().equals(to.getName())) {
					return Arrays.equals(r.getParameterTypes(),
							to.getParameterTypes());
				}
				Method w = pd.getWriteMethod();
				if (w == null || !w.getName().equals(to.getName()))
					return false;
				return Arrays.equals(w.getParameterTypes(),
						to.getParameterTypes());
			}

			public PropertyBehaviour newInstance(Object proxy) throws Throwable {
				return new PropertyBehaviour(factory.createPropertySet(proxy),
						pd);
			}

			public String getName() {
				return pd.getName();
			}
		};
	}

}
