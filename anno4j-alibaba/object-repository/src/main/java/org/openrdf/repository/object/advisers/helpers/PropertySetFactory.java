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
package org.openrdf.repository.object.advisers.helpers;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

import org.openrdf.annotations.Iri;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.object.traits.ManagedRDFObject;

/**
 * Creates {@link PropertySet} objects for a given predicate.
 * 
 * @author James Leigh
 */
public class PropertySetFactory {
	private static ValueFactory vf = ValueFactoryImpl.getInstance();

	private String name;

	private Class<?> type;

	private URI predicate;

	private boolean readOnly;

	private PropertySetModifier modifier;

	public PropertySetFactory(Field field, String predicate) {
		Iri rdf = field.getAnnotation(Iri.class);
		if (predicate != null) {
			setPredicate(predicate);
		} else if (rdf != null && rdf.value() != null) {
			setPredicate(rdf.value());
		}
		assert this.predicate != null;
		name = field.getName();
		type = field.getType();
		if (Set.class.equals(type)) {
			Type t = field.getGenericType();
			if (t instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType) t;
				Type[] args = pt.getActualTypeArguments();
				if (args.length == 1 && args[0] instanceof Class) {
					type = (Class) args[0];
				}
			}
		}
	}

	public PropertySetFactory(PropertyDescriptor property, String predicate) {
		Method getter = property.getReadMethod();
		readOnly = property.getWriteMethod() == null;
		Iri rdf = getter.getAnnotation(Iri.class);
		if (predicate != null) {
			setPredicate(predicate);
		} else if (rdf != null && rdf.value() != null) {
			setPredicate(rdf.value());
		}
		assert this.predicate != null;
		name = property.getName();
		type = property.getPropertyType();
		if (Set.class.equals(type)) {
			Type t = property.getReadMethod().getGenericReturnType();
			if (t instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType) t;
				Type[] args = pt.getActualTypeArguments();
				if (args.length == 1 && args[0] instanceof Class) {
					type = (Class) args[0];
				}
			}
		}
	}

	public String getName() {
		return name;
	}

	public Class<?> getPropertyType() {
		return type;
	}

	public URI getPredicate() {
		return predicate;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public PropertySet createPropertySet(Object bean) {
		CachedPropertySet property = createCachedPropertySet((ManagedRDFObject) bean);
		property.setPropertySetFactory(this);
		if (readOnly)
			return new UnmodifiableProperty(property);
		return property;
	}

	private CachedPropertySet createCachedPropertySet(ManagedRDFObject bean) {
		return new CachedPropertySet(bean, modifier);
	}

	private void setPredicate(String uri) {
		predicate = vf.createURI(uri);
		modifier = new PropertySetModifier(predicate);
	}

}
