/*
 * Copyright (c) 2009, James Leigh All rights reserved.
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
package org.openrdf.repository.object.managers;

import static java.util.Locale.ENGLISH;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.openrdf.annotations.Iri;
import org.openrdf.model.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads in property mapping files and determines which properties should be
 * eagarly loaded.
 * 
 * @author James Leigh
 * 
 */
public class PropertyMapper {

	private static final String PROPERTIES = "META-INF/org.openrdf.properties";
	private static final String GET_PREFIX = "get";
	private static final String SET_PREFIX = "set";
	private static final String IS_PREFIX = "is";

	private Logger logger = LoggerFactory
			.getLogger(PropertyMapper.class);
	private boolean readTypes;
	private Properties properties = new Properties();

	public PropertyMapper(ClassLoader cl, boolean readTypes) {
		loadProperties(cl);
		this.readTypes = readTypes;
	}

	public boolean isReadingTypes() {
		return readTypes;
	}

	public Collection<Field> findFields(Class<?> concept) {
		List<Field> fields = new ArrayList<Field>();
		for (Field field : concept.getDeclaredFields()) {
			if (isMappedField(field)) {
				fields.add(field);
			}
		}
		return fields;
	}

	public Collection<PropertyDescriptor> findProperties(Class<?> concept) {
		List<PropertyDescriptor> properties = new ArrayList<PropertyDescriptor>();
		for (Method method : concept.getDeclaredMethods()) {
			if (isMappedGetter(method)) {
				properties.add(createPropertyDescriptor(method));
			}
		}
		return properties;
	}

	public String findPredicate(Field field) {
		Class<?> dc = field.getDeclaringClass();
		String key = dc.getName() + "#" + field.getName();
		if (properties.containsKey(key))
			return (String) properties.get(key);
		Iri rdf = field.getAnnotation(Iri.class);
		if (rdf == null)
			return null;
		return rdf.value();
	}

	public String findPredicate(PropertyDescriptor pd) {
		Method method = pd.getReadMethod();
		Class<?> dc = method.getDeclaringClass();
		String key = dc.getName() + "." + getPropertyName(method);
		if (properties.containsKey(key))
			return (String) properties.get(key);
		Method getter = method;
		Iri rdf = getter.getAnnotation(Iri.class);
		if (rdf == null)
			return null;
		return rdf.value();
	}

	public Collection<PropertyDescriptor> findFunctionalProperties(Class<?> type) {
		Map<String, PropertyDescriptor> properties = new HashMap<String, PropertyDescriptor>();
		findFunctionalProperties(type, properties);
		return properties.values();
	}

	public Collection<Field> findFunctionalFields(Class<?> type) {
		if (type.isInterface())
			return Collections.emptySet();
		Map<String, Field> properties = new HashMap<String, Field>();
		findFunctionalFields(type, properties);
		return properties.values();
	}

	/** @return map of name to uri */
	public Map<String, String> findEagerProperties(Class<?> type) {
		Map<String, String> properties = new HashMap<String, String>();
		findEagerProperties(type, properties);
		if (properties.isEmpty())
			return null;
		if (readTypes) {
			properties.put("class", RDF.TYPE.stringValue());
		}
		return properties;
	}

	public boolean isMappedField(Field field) {
		if (field.isAnnotationPresent(Iri.class))
			return true;
		if (properties.isEmpty())
			return false;
		String name = field.getDeclaringClass().getName();
		String key = name + "#" + field.getName();
		return properties.containsKey(key);
	}

	private void findFunctionalProperties(Class<?> concept,
			Map<String, PropertyDescriptor> properties) {
		for (PropertyDescriptor pd : findProperties(concept)) {
			Class<?> type = pd.getPropertyType();
			if (Set.class.equals(type))
				continue;
			properties.put(pd.getName(), pd);
		}
		for (Class<?> face : concept.getInterfaces()) {
			findFunctionalProperties(face, properties);
		}
		if (concept.getSuperclass() != null)
			findFunctionalProperties(concept.getSuperclass(), properties);
	}

	private void findFunctionalFields(Class<?> concept,
			Map<String, Field> properties) {
		for (Field field : findFields(concept)) {
			Class<?> type = field.getType();
			if (Set.class.equals(type))
				continue;
			properties.put(field.getName(), field);
		}
		if (concept.getSuperclass() != null)
			findFunctionalFields(concept.getSuperclass(), properties);
	}

	private Map<String, String> findEagerProperties(Class<?> concept,
			Map<String, String> properties) {
		for (PropertyDescriptor pd : findProperties(concept)) {
			Class<?> type = pd.getPropertyType();
			Type generic = pd.getReadMethod().getGenericReturnType();
			if (!isEagerPropertyType(generic, type))
				continue;
			properties.put(pd.getName(), findPredicate(pd));
		}
		for (Field field : findFields(concept)) {
			Class<?> type = field.getType();
			if (!isEagerPropertyType(field.getGenericType(), type))
				continue;
			properties.put(field.getName(), findPredicate(field));
		}
		for (Class<?> face : concept.getInterfaces()) {
			findEagerProperties(face, properties);
		}
		if (concept.getSuperclass() == null)
			return properties;
		return findEagerProperties(concept.getSuperclass(), properties);
	}

	private boolean isEagerPropertyType(Type t, Class<?> type) {
		if (Set.class.equals(type))
			return false;
		if (!readTypes)
			return true;
		if (type.isInterface())
			return false;
		if (Object.class.equals(type))
			return false;
		if (type.isAnnotationPresent(Iri.class))
			return false;
		return true;
	}

	private void loadProperties(ClassLoader cl) {
		try {
			Enumeration<URL> resources = cl.getResources(PROPERTIES);
			while (resources.hasMoreElements()) {
				try {
					InputStream stream = resources.nextElement().openStream();
					try {
						properties.load(stream);
					} finally {
						stream.close();
					}
				} catch (IOException e) {
					logger.warn(e.toString(), e);
				}
			}
		} catch (IOException e) {
			logger.warn(e.toString(), e);
		}
	}

	private boolean isMappedGetter(Method method) {
		if (method.getParameterTypes().length != 0)
			return false;
		if (getPropertyName(method) == null)
			return false;
		if (method.isAnnotationPresent(Iri.class))
			return true;
		if (properties.isEmpty())
			return false;
		String name = method.getDeclaringClass().getName();
		String key = name + "." + getPropertyName(method);
		return properties.containsKey(key);
	}

	private PropertyDescriptor createPropertyDescriptor(Method method) {
		String property = getPropertyName(method);
		Method setter = getSetterMethod(property, method);
		try {
			return new PropertyDescriptor(property, method, setter);
		} catch (IntrospectionException e) {
			// property name is bad
			throw new AssertionError(e);
		}
	}

	private String getPropertyName(Method method) {
		String name = method.getName();
		boolean bool = method.getReturnType() == boolean.class;
		if (name.startsWith(GET_PREFIX) && name.length() > 3) {
			// Simple getter
			return decapitalize(name.substring(3));
		} else if (bool && name.startsWith(IS_PREFIX) && name.length() > 2) {
			// Boolean getter
			return decapitalize(name.substring(2));
		}
		return null;
	}

	private static String decapitalize(String name) {
		if (name == null || name.length() == 0) {
			return name;
		}
		if (name.length() > 1 && Character.isUpperCase(name.charAt(1))
				&& Character.isUpperCase(name.charAt(0))) {
			return name;
		}
		char chars[] = name.toCharArray();
		chars[0] = Character.toLowerCase(chars[0]);
		return new String(chars);
	}

	private Method getSetterMethod(String property, Method getter) {
		try {
			Class<?> dc = getter.getDeclaringClass();
			Class<?> rt = getter.getReturnType();
			String setter = SET_PREFIX + capitalize(property);
			return dc.getDeclaredMethod(setter, rt);
		} catch (NoSuchMethodException exc) {
			return null;
		}
	}

	private static String capitalize(String name) {
		if (name == null || name.length() == 0) {
			return name;
		}
		return name.substring(0, 1).toUpperCase(ENGLISH) + name.substring(1);
	}
}
