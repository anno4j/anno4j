/*
 * Copyright (c) 2007-2009, James Leigh All rights reserved.
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
package org.openrdf.repository.object.managers.converters;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.object.exceptions.ObjectConversionException;
import org.openrdf.repository.object.managers.Marshall;

/**
 * Converts objects with a valueOf method to and from {@link Literal}.
 * 
 * @author James Leigh
 */
public class ValueOfMarshall<T> implements Marshall<T> {

	private ValueFactory vf;

	private Method valueOfMethod;

	private URI datatype;

	public ValueOfMarshall(ValueFactory vf, Class<T> type)
			throws NoSuchMethodException {
		this.vf = vf;
		ValueFactoryImpl uf = ValueFactoryImpl.getInstance();
		this.datatype = uf.createURI("java:", type.getName());
		try {
			this.valueOfMethod = type.getDeclaredMethod("valueOf",
					new Class[] { String.class });
			if (!Modifier.isStatic(valueOfMethod.getModifiers()))
				throw new NoSuchMethodException("valueOf Method is not static");
			if (!type.equals(valueOfMethod.getReturnType()))
				throw new NoSuchMethodException("Invalid return type");
		} catch (NoSuchMethodException e) {
			try {
				this.valueOfMethod = type.getDeclaredMethod("getInstance",
						new Class[] { String.class });
				if (!Modifier.isStatic(valueOfMethod.getModifiers()))
					throw new NoSuchMethodException(
							"getInstance Method is not static");
				if (!type.equals(valueOfMethod.getReturnType()))
					throw new NoSuchMethodException("Invalid return type");
			} catch (NoSuchMethodException e2) {
				throw e;
			}
		}
	}

	public String getJavaClassName() {
		return valueOfMethod.getDeclaringClass().getName();
	}

	public URI getDatatype() {
		return datatype;
	}

	public void setDatatype(URI datatype) {
		this.datatype = datatype;
	}

	@SuppressWarnings("unchecked")
	public T deserialize(Literal literal) {
		try {
			return (T) valueOfMethod.invoke(null, new Object[] { literal
					.getLabel() });
		} catch (Exception e) {
			throw new ObjectConversionException(e);
		}
	}

	public Literal serialize(T object) {
		return vf.createLiteral(object.toString(), datatype);
	}

}
