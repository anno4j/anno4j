/*
 * Copyright (c) 2009, Zepheira All rights reserved.
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

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.object.exceptions.ObjectConversionException;
import org.openrdf.repository.object.managers.Marshall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Converts {@link DocumentFragment} to and from {@link Literal}.
 * 
 * @author James Leigh
 * 
 */
public class DocumentFragmentMarshall implements Marshall<DocumentFragment> {
	private static final String TAG_NAME = "rdf-wrapper";
	private static final String END_TAG = "</rdf-wrapper>";
	private static final String START_TAG = "<rdf-wrapper>";

	private static class ErrorCatcher implements ErrorListener {
		private Logger logger = LoggerFactory.getLogger(ErrorCatcher.class);
		private TransformerException fatal;

		public boolean isFatal() {
			return fatal != null;
		}

		public TransformerException getFatalError() {
			return fatal;
		}

		public void error(TransformerException exception) {
			logger.warn(exception.toString(), exception);
		}

		public void fatalError(TransformerException exception) {
			if (this.fatal == null) {
				this.fatal = exception;
			}
			logger.error(exception.toString(), exception);
		}

		public void warning(TransformerException exception) {
			logger.info(exception.toString(), exception);
		}
	}

	public static DocumentBuilderFactory newDocumentBuilderFactory() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true);
		factory.setIgnoringComments(false);
		factory.setIgnoringElementContentWhitespace(false);
		return factory;
	}

	private final DocumentBuilderFactory builder;
	private TransformerFactory factory = TransformerFactory.newInstance();
	private ValueFactory vf;
	private URI datatype = RDF.XMLLITERAL;

	private Class<? extends DocumentFragment> javaClass;

	public DocumentFragmentMarshall(ValueFactory vf)
			throws ParserConfigurationException {
		this.vf = vf;
		builder = newDocumentBuilderFactory();
		Document doc = builder.newDocumentBuilder().newDocument();
		javaClass = doc.createDocumentFragment().getClass();
	}

	public String getJavaClassName() {
		return javaClass.getName();
	}

	public URI getDatatype() {
		return datatype;
	}

	public void setDatatype(URI datatype) {
		this.datatype = datatype;
	}

	public DocumentFragment deserialize(Literal literal) {
		try {
			String wrapper = START_TAG + literal.getLabel() + END_TAG;
			Source source = new StreamSource(new StringReader(wrapper));
			Document doc = builder.newDocumentBuilder().newDocument();
			DOMResult result = new DOMResult(doc);
			Transformer transformer = factory.newTransformer();
			ErrorCatcher listener = new ErrorCatcher();
			transformer.setErrorListener(listener);
			transformer.transform(source, result);
			if (listener.isFatal())
				throw listener.getFatalError();
			DocumentFragment frag = doc.createDocumentFragment();
			Element element = doc.getDocumentElement();
			NodeList nodes = element.getChildNodes();
			int size = nodes.getLength();
			List<Node> list = new ArrayList<Node>(size);
			for (int i = 0, n = size; i < n; i++) {
				list.add(nodes.item(i));
			}
			for (Node node : list) {
				frag.appendChild(node);
			}
			return frag;
		} catch (TransformerConfigurationException e) {
			throw new ObjectConversionException(e);
		} catch (TransformerException e) {
			throw new ObjectConversionException(e);
		} catch (ParserConfigurationException e) {
			throw new ObjectConversionException(e);
		}
	}

	public Literal serialize(DocumentFragment object) {
		try {
			Document doc = builder.newDocumentBuilder().newDocument();
			Element wrapper = doc.createElement(TAG_NAME);
			wrapper.appendChild(doc.importNode(object, true));
			doc.appendChild(wrapper);
			Source source = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			Result result = new StreamResult(writer);
			Transformer transformer = factory.newTransformer();
			ErrorCatcher listener = new ErrorCatcher();
			transformer.setErrorListener(listener);
			transformer.transform(source, result);
			if (listener.isFatal())
				throw listener.getFatalError();
			String string = writer.toString();
			int l = START_TAG.length();
			int start = string.indexOf(START_TAG.substring(0, l - 1)) + l;
			int end = string.lastIndexOf(END_TAG);
			String label = string.substring(start, end);
			return vf.createLiteral(label, RDF.XMLLITERAL);
		} catch (TransformerConfigurationException e) {
			throw new ObjectConversionException(e);
		} catch (TransformerException e) {
			throw new ObjectConversionException(e);
		} catch (ParserConfigurationException e) {
			throw new ObjectConversionException(e);
		}
	}
}
