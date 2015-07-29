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

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.channels.ReadableByteChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;

import org.openrdf.OpenRDFException;
import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.RDFObject;
import org.openrdf.repository.object.advice.Advice;
import org.openrdf.repository.object.advisers.helpers.SparqlEvaluator;
import org.openrdf.repository.object.advisers.helpers.SparqlParameters;
import org.openrdf.repository.object.advisers.helpers.SparqlEvaluator.SparqlBuilder;
import org.openrdf.repository.object.traits.ObjectMessage;
import org.openrdf.repository.object.traits.Refreshable;
import org.openrdf.result.Result;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Executes a SPARQL query/update operation when a method with @Sparql is called.
 */
public class SparqlAdvice implements Advice {
	private final SparqlEvaluator evaluator;
	private final Class<?> returnClass;
	private final Class<?> componentClass;
	private final Map<Method, SparqlParameters> map = new HashMap<Method, SparqlParameters>();

	public SparqlAdvice(SparqlEvaluator evaluator, Method m) {
		this.evaluator = evaluator;
		this.returnClass = m.getReturnType();
		Type rtype = m.getGenericReturnType();
		SparqlParameters parameters = new SparqlParameters(m);
		this.componentClass = parameters.getComponentClass(returnClass, rtype);
		map.put(m, parameters);
	}

	@Override
	public String toString() {
		return evaluator.toString();
	}

	public Object intercept(ObjectMessage message) throws Exception {
		Object target = message.getTarget();
		ObjectConnection con = ((RDFObject) target).getObjectConnection();
		Resource self = ((RDFObject) target).getResource();
		SparqlBuilder with = evaluator.prepare(con).with("this", self);
		Object[] args = message.getParameters();
		getParameters(message.getMethod()).populate(args, with, con);
		if (isUpdate()) {
			with.asUpdate();
			if (target instanceof Refreshable) {
				((Refreshable) target).refresh();
			}
			return message.proceed();
		} else {
			Object result = cast(with, returnClass, componentClass);
			if (result == null)
				return message.proceed();
			if (returnClass.isPrimitive() && result.equals(nil(returnClass)))
				return message.proceed();
			return result;
		}
	}

	private synchronized SparqlParameters getParameters(Method m) {
		if (map.containsKey(m))
			return map.get(m);
		SparqlParameters parameters = new SparqlParameters(m);
		map.put(m, parameters);
		return parameters;
	}

	private boolean isUpdate() {
		return Void.class.equals(returnClass) || Void.TYPE.equals(returnClass);
	}

	private Object cast(SparqlBuilder result, Class<?> rclass,
			Class<?> componentClass) throws OpenRDFException,
			TransformerException, IOException, ParserConfigurationException,
			SAXException, XMLStreamException {
		if (TupleQueryResult.class.equals(rclass)) {
			return result.asTupleQueryResult();
		} else if (GraphQueryResult.class.equals(rclass)) {
			return result.asGraphQueryResult();
		} else if (Result.class.equals(rclass)) {
			return result.asResult(componentClass);
		} else if (Set.class.equals(rclass)) {
			return result.asSet(componentClass);
		} else if (List.class.equals(rclass)) {
			return result.asList(componentClass);

		} else if (byte[].class.equals(rclass)) {
			return result.asByteArray();
		} else if (CharSequence.class.equals(rclass)) {
			return result.asCharSequence();
		} else if (Readable.class.equals(rclass)) {
			return result.asReadable();
		} else if (String.class.equals(rclass)) {
			return result.asString();

		} else if (Void.class.equals(rclass) || Void.TYPE.equals(rclass)) {
			result.asUpdate();
			return null;
		} else if (Boolean.class.equals(rclass) || Boolean.TYPE.equals(rclass)) {
			return result.asBoolean();
		} else if (Byte.class.equals(rclass) || Byte.TYPE.equals(rclass)) {
			return result.asByte();
		} else if (Character.class.equals(rclass)
				|| Character.TYPE.equals(rclass)) {
			return result.asChar();
		} else if (Double.class.equals(rclass) || Double.TYPE.equals(rclass)) {
			return result.asDouble();
		} else if (Float.class.equals(rclass) || Float.TYPE.equals(rclass)) {
			return result.asFloat();
		} else if (Integer.class.equals(rclass) || Integer.TYPE.equals(rclass)) {
			return result.asInt();
		} else if (Long.class.equals(rclass) || Long.TYPE.equals(rclass)) {
			return result.asLong();
		} else if (Short.class.equals(rclass) || Short.TYPE.equals(rclass)) {
			return result.asShort();

		} else if (Model.class.equals(rclass)) {
			return result.asModel();
		} else if (Statement.class.equals(rclass)) {
			return result.asStatement();
		} else if (BindingSet.class.equals(rclass)) {
			return result.asBindingSet();
		} else if (URI.class.equals(rclass)) {
			return result.asURI();
		} else if (BNode.class.equals(rclass)) {
			return result.asBNode();
		} else if (Literal.class.equals(rclass)) {
			return result.asLiteral();
		} else if (Resource.class.equals(rclass)) {
			return result.asResource();
		} else if (Value.class.equals(rclass)) {
			return result.asValue();

		} else if (Document.class.equals(rclass)) {
			return result.asDocument();
		} else if (DocumentFragment.class.equals(rclass)) {
			return result.asDocumentFragment();
		} else if (Element.class.equals(rclass)) {
			return result.asElement();
		} else if (Node.class.equals(rclass)) {
			return result.asNode();

		} else if (Reader.class.equals(rclass)) {
			return result.asReader();
		} else if (CharArrayWriter.class.equals(rclass)) {
			return result.asCharArrayWriter();
		} else if (ByteArrayOutputStream.class.equals(rclass)) {
			return result.asByteArrayOutputStream();
		} else if (ReadableByteChannel.class.equals(rclass)) {
			return result.asReadableByteChannel();
		} else if (InputStream.class.equals(rclass)) {
			return result.asInputStream();
		} else if (XMLEventReader.class.equals(rclass)) {
			return result.asXMLEventReader();

		} else {
			return result.as(rclass);
		}
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

}
