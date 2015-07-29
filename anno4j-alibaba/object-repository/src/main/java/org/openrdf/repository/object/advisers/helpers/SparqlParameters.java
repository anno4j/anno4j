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
package org.openrdf.repository.object.advisers.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.openrdf.annotations.Bind;
import org.openrdf.annotations.Iri;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectFactory;
import org.openrdf.repository.object.advisers.helpers.SparqlEvaluator.SparqlBuilder;

/**
 * Stores the binding names, type and default values for a method with @Sparql
 * annotation as read from the parameter annotations.
 */
public class SparqlParameters {
	private final Type[] ptypes;
	private final String[][] bindingNames;
	private final String[] defaults;

	public SparqlParameters(Method m) {
		this.ptypes = m.getGenericParameterTypes();
		this.bindingNames = getBindingNames(m.getParameterAnnotations());
		this.defaults = getDefaultValues(m.getParameterAnnotations());
	}

	public Class<?> getComponentClass(Class<?> cls, Type type) {
		return asClass(getComponentType(cls, type));
	}

	public SparqlBuilder populate(Object[] args, SparqlBuilder with, ObjectConnection con) {
		for (int i = 0; i < args.length && i < bindingNames.length; i++) {
			Object value = args[i];
			Type vtype = ptypes[i];
			Class<?> cvtype = asClass(vtype);
			String defaultValue = defaults[i];
			if (value == null && defaultValue != null && con != null) {
				value = getDefaultValue(defaultValue, vtype, con);
			}
			if (value == null)
				continue;
			if (Set.class.equals(cvtype)) {
				for (String name : bindingNames[i]) {
					with = with.with(name, (Set<?>) value);
				}
			} else {
				for (String name : bindingNames[i]) {
					with = with.with(name, value);
				}
			}
		}
		return with;
	}

	private Object getDefaultValue(String value, Type type, ObjectConnection con) {
		Class<?> ctype = asClass(type);
		if (Set.class.equals(ctype)) {
			Object v = getDefaultValue(value, getComponentType(ctype, type), con);
			if (v == null)
				return null;
			return Collections.singleton(v);
		}
		ValueFactory vf = con.getValueFactory();
		ObjectFactory of = con.getObjectFactory();
		if (of.isDatatype(ctype)) {
			URIImpl datatype = new URIImpl("java:" + ctype.getName());
			return of.createValue(of.createObject(new LiteralImpl(value, datatype)));
		}
		return vf.createURI(value);
	}

	private Type getComponentType(Class<?> cls, Type type) {
		if (cls.isArray())
			return cls.getComponentType();
		if (type instanceof ParameterizedType) {
			ParameterizedType ptype = (ParameterizedType) type;
			Type[] args = ptype.getActualTypeArguments();
			return args[args.length - 1];
		}
		if (Set.class.equals(cls) || Map.class.equals(cls))
			return Object.class;
		return null;
	}

	private Class<?> asClass(Type type) {
		if (type == null)
			return null;
		if (type instanceof Class<?>)
			return (Class<?>) type;
		if (type instanceof GenericArrayType) {
			GenericArrayType atype = (GenericArrayType) type;
			Class<?> componentType = asClass(atype.getGenericComponentType());
			return Array.newInstance(asClass(componentType), 0).getClass();
		}
		if (type instanceof ParameterizedType) {
			return asClass(((ParameterizedType) type).getRawType());
		}
		return Object.class; // wildcard
	}

	private String[][] getBindingNames(Annotation[][] anns) {
		String[][] bindingNames = new String[anns.length][];
		loop: for (int i=0; i<anns.length; i++) {
			bindingNames[i] = new String[0];
			for (Annotation ann : anns[i]) {
				if (Bind.class.equals(ann.annotationType())) {
					bindingNames[i] = ((Bind) ann).value();
					continue loop;
				} else if (Iri.class.equals(ann.annotationType())) {
					bindingNames[i] = new String[] { local(((Iri) ann).value()) };
				}
			}
		}
		return bindingNames;
	}

	private String local(String iri) {
		String string = iri;
		if (string.lastIndexOf('#') >= 0) {
			string = string.substring(string.lastIndexOf('#') + 1);
		}
		if (string.lastIndexOf('?') >= 0) {
			string = string.substring(string.lastIndexOf('?') + 1);
		}
		if (string.lastIndexOf('/') >= 0) {
			string = string.substring(string.lastIndexOf('/') + 1);
		}
		if (string.lastIndexOf(':') >= 0) {
			string = string.substring(string.lastIndexOf(':') + 1);
		}
		return string;
	}

	private String[] getDefaultValues(Annotation[][] anns) {
		String[] defaults = new String[anns.length];
		for (int i=0; i<anns.length; i++) {
			Object value = getDefaultValue(anns[i]);
			if (value != null) {
				defaults[i] = value.toString();
			}
		}
		return defaults;
	}

	private Object getDefaultValue(Annotation[] anns) {
		for (Annotation ann : anns) {
			for (Method m : ann.annotationType().getDeclaredMethods()) {
				Iri iri = m.getAnnotation(Iri.class);
				if (iri != null && OWL.HASVALUE.equals(iri.value()) && m.getParameterTypes().length == 0) {
					return invoke(m, ann);
				}
			}
		}
		return null;
	}

	private Object invoke(Method m, Object obj) {
		try {
			return m.invoke(obj);
		} catch (IllegalArgumentException e) {
			throw new AssertionError(e);
		} catch (IllegalAccessException e) {
			IllegalAccessError error = new IllegalAccessError(e.getMessage());
			error.initCause(e);
			throw error;
		} catch (InvocationTargetException e) {
			try {
				throw e.getCause();
			} catch (RuntimeException cause) {
				throw cause;
			} catch (Error cause) {
				throw cause;
			} catch (Throwable cause) {
				throw new UndeclaredThrowableException(cause);
			}
		}
	}
}
