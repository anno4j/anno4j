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
package org.openrdf.repository.object.advisers.helpers;

import static org.openrdf.query.QueryLanguage.SPARQL;
import info.aduna.xml.XMLWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
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
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.query.BindingSet;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.Operation;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryResultUtil;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.query.Update;
import org.openrdf.query.resultio.sparqlxml.SPARQLBooleanXMLWriter;
import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectQuery;
import org.openrdf.repository.object.advisers.SparqlQuery;
import org.openrdf.result.MultipleResultException;
import org.openrdf.result.Result;
import org.openrdf.rio.helpers.StatementCollector;
import org.openrdf.rio.rdfxml.RDFXMLWriter;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class SparqlEvaluator {
	private static final Pattern ILLEGAL_VAR = Pattern.compile("\\s|\\?");
	private static final XMLInputFactory inFactory;
	private static final DocumentBuilderFactory documentBuilderFactory;
	static {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		factory.setProperty(XMLInputFactory.IS_VALIDATING, false);
		factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
		inFactory = factory;
	}
	static {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true);
		factory.setIgnoringComments(false);
		factory.setIgnoringElementContentWhitespace(false);
		documentBuilderFactory = factory;
	}

	public class SparqlBuilder {
		private ObjectConnection con;
		private SparqlQuery query;
		private Map<String, Value> bindings = new HashMap<String, Value>();
		private List<String> bindingNames = new ArrayList<String>();
		private List<List<Value>> bindingValues = new ArrayList<List<Value>>();
		private org.openrdf.repository.object.ObjectFactory of;

		public SparqlBuilder(ObjectConnection con, SparqlQuery query) {
			assert con != null;
			assert query != null;
			this.con = con;
			this.query = query;
			of = con.getObjectFactory();
		}

		@Override
		public String toString() {
			return bindMultiples(query.toString());
		}

		public SparqlBuilder with(String name, Set values) {
			boolean illegal = ILLEGAL_VAR.matcher(name).find();
			if (illegal && values != null && !values.isEmpty()) {
				throw new IllegalArgumentException(
						"Invalide SPARQL variable name: '" + name + "'");
			}
			List<List<Value>> list = new ArrayList<List<Value>>();
			for (Object value : values) {
				for (List<Value> bindings : bindingValues) {
					List<Value> set = new ArrayList<Value>(bindings.size() + 1);
					set.addAll(bindings);
					if (value == null) {
						set.add(null);
					} else if (value instanceof Value) {
						set.add((Value) value);
					} else {
						set.add(of.createValue(value));
					}
					list.add(set);
				}
				if (bindingValues.isEmpty()) {
					List<Value> set = new ArrayList<Value>(1);
					if (value == null) {
						set.add(null);
					} else if (value instanceof Value) {
						set.add((Value) value);
					} else {
						set.add(of.createValue(value));
					}
					list.add(set);
				}
			}
			bindingValues = list;
			bindingNames.add(name);
			return this;
		}

		public SparqlBuilder with(String name, Object value) {
			if (value == null) {
				bindings.remove(name);
			} else if (value instanceof Value) {
				bindings.put(name, (Value) value);
			} else {
				bindings.put(name, of.createValue(value));
			}
			return this;
		}

		public SparqlBuilder with(String name, boolean value) {
			bindings.put(name, con.getValueFactory().createLiteral(value));
			return this;
		}

		public SparqlBuilder with(String name, char value) {
			bindings.put(name, con.getValueFactory().createLiteral(value));
			return this;
		}

		public SparqlBuilder with(String name, byte value) {
			bindings.put(name, con.getValueFactory().createLiteral(value));
			return this;
		}

		public SparqlBuilder with(String name, short value) {
			bindings.put(name, con.getValueFactory().createLiteral(value));
			return this;
		}

		public SparqlBuilder with(String name, int value) {
			bindings.put(name, con.getValueFactory().createLiteral(value));
			return this;
		}

		public SparqlBuilder with(String name, long value) {
			bindings.put(name, con.getValueFactory().createLiteral(value));
			return this;
		}

		public SparqlBuilder with(String name, float value) {
			bindings.put(name, con.getValueFactory().createLiteral(value));
			return this;
		}

		public SparqlBuilder with(String name, double value) {
			bindings.put(name, con.getValueFactory().createLiteral(value));
			return this;
		}

		public Model asModel() throws OpenRDFException {
			GraphQuery qry = prepareGraphQuery();
			Model model = new LinkedHashModel();
			qry.evaluate(new StatementCollector(model));
			return model;
		}

		public Statement asStatement() throws OpenRDFException {
			GraphQueryResult result = asGraphQueryResult();
			try {
				if (result.hasNext()) {
					Statement stmt = result.next();
					if (result.hasNext())
						throw new MultipleResultException();
					return stmt;
				}
				return null;
			} finally {
				result.close();
			}
		}

		public URI asURI() throws OpenRDFException {
			return (URI) asResource();
		}

		public BNode asBNode() throws OpenRDFException {
			return (BNode) asResource();
		}

		public Resource asResource() throws OpenRDFException {
			return (Resource) asValue();
		}

		public Literal asLiteral() throws OpenRDFException {
			return (Literal) asValue();
		}

		public Value asValue() throws OpenRDFException {
			BindingSet bs = asBindingSet();
			if (bs == null)
				return null;
			return bs.getValue(bs.getBindingNames().iterator().next());
		}

		public BindingSet asBindingSet() throws OpenRDFException {
			TupleQueryResult result = asTupleQueryResult();
			try {
				if (result.hasNext()) {
					BindingSet bindings = result.next();
					if (result.hasNext())
						throw new MultipleResultException();
					return bindings;
				}
				return null;
			} finally {
				result.close();
			}
		}

		public TupleQueryResult asTupleQueryResult() throws OpenRDFException {
			return prepareTupleQuery().evaluate();
		}

		public GraphQueryResult asGraphQueryResult() throws OpenRDFException {
			return prepareGraphQuery().evaluate();
		}

		public boolean asBoolean() throws OpenRDFException {
			if (query.isBooleanQuery())
				return prepareBooleanQuery().evaluate();
			return asResult(Boolean.class).singleResult().booleanValue();
		}

		public char asChar() throws OpenRDFException {
			return asResult(Character.class).singleResult().charValue();
		}

		public byte asByte() throws OpenRDFException {
			return asResult(Byte.class).singleResult().byteValue();
		}

		public short asShort() throws OpenRDFException {
			return asResult(Short.class).singleResult().shortValue();
		}

		public int asInt() throws OpenRDFException {
			return asResult(Integer.class).singleResult().intValue();
		}

		public long asLong() throws OpenRDFException {
			return asResult(Long.class).singleResult().longValue();
		}

		public float asFloat() throws OpenRDFException {
			return asResult(Float.class).singleResult().floatValue();
		}

		public double asDouble() throws OpenRDFException {
			return asResult(Double.class).singleResult().doubleValue();
		}

		public String asString() throws OpenRDFException {
			Result<String> result = asResult(String.class);
			if (result.hasNext())
				return result.singleResult();
			return null;
		}

		public CharSequence asCharSequence() throws OpenRDFException {
			Result<CharSequence> result = asResult(CharSequence.class);
			if (result.hasNext())
				return result.singleResult();
			return null;
		}

		public byte[] asByteArray() throws OpenRDFException {
			Result<byte[]> result = asResult(byte[].class);
			if (result.hasNext())
				return result.singleResult();
			return null;
		}

		public Set<? extends Value> asSetOfValues() throws OpenRDFException {
			Set<Value> set = new LinkedHashSet<Value>();
			TupleQueryResult result = asTupleQueryResult();
			try {
				if (result.getBindingNames().isEmpty())
					return null;
				String name = result.getBindingNames().iterator().next();
				while (result.hasNext()) {
					set.add(result.next().getValue(name));
				}
				return set;
			} finally {
				result.close();
			}
		}

		public Set asSet() throws OpenRDFException {
			return asResult().asSet();
		}

		public Result asResult() throws OpenRDFException {
			ObjectQuery qry = prepareObjectQuery(Object.class);
			return qry.evaluate();
		}

		public <T> Result<T> asResult(Class<T> of) throws OpenRDFException {
			if (of == null || Object.class.equals(of))
				return asResult();
			ObjectQuery qry = prepareObjectQuery(of);
			return qry.evaluate(of);
		}

		public <T> Set<T> asSet(Class<T> of) throws OpenRDFException {
			if (of == null || Object.class.equals(of))
				return asSet();
			if (BindingSet.class.equals(of)) {
				TupleQueryResult result = asTupleQueryResult();
				try {
					List<BindingSet> list = new ArrayList<BindingSet>();
					while (result.hasNext()) {
						list.add(result.next());
					}
					return (Set<T>) list;
				} finally {
					result.close();
				}
			}
			if (Value.class.isAssignableFrom(of))
				return (Set<T>) asSetOfValues();
			if (Statement.class.equals(of))
				return (Set<T>) asModel();
			return asResult(of).asSet();
		}

		public <T> List<T> asList(Class<T> of) throws OpenRDFException {
			if (of == null || Object.class.equals(of))
				return asList();
			return asResult(of).asList();
		}

		public <T> T as(Class<T> of) throws OpenRDFException {
			Result<T> result = asResult(of);
			if (result.hasNext())
				return result.singleResult();
			return null;
		}

		public List asList() throws OpenRDFException {
			return asResult().asList();
		}

		public Document asDocument() throws OpenRDFException,
				TransformerException, IOException, ParserConfigurationException {
			DocumentBuilder builder = documentBuilderFactory
					.newDocumentBuilder();
			InputStream in = asInputStream();
			try {
				try {
					if (systemId == null)
						return builder.parse(in);
					return builder.parse(in, systemId);
				} catch (SAXException e) {
					throw new TransformerException(e);
				} finally {
					in.close();
				}
			} catch (IOException e) {
				throw new TransformerException(e);
			}
		}

		public DocumentFragment asDocumentFragment() throws OpenRDFException,
				TransformerException, IOException, ParserConfigurationException {
			Document doc = asDocument();
			DocumentFragment frag = doc.createDocumentFragment();
			frag.appendChild(doc.getDocumentElement());
			return frag;
		}

		public Element asElement() throws OpenRDFException,
				TransformerException, IOException, ParserConfigurationException {
			return asDocument().getDocumentElement();
		}

		public Node asNode() throws OpenRDFException, TransformerException,
				IOException, ParserConfigurationException {
			return asDocument();
		}

		public XMLEventReader asXMLEventReader() throws OpenRDFException,
				TransformerException, IOException,
				ParserConfigurationException, XMLStreamException {
			InputStream in = asInputStream();
			try {
				if (systemId == null)
					return inFactory.createXMLEventReader(in);
				return inFactory.createXMLEventReader(systemId, in);
			} catch (XMLStreamException e) {
				throw new TransformerException(e);
			}
		}

		public ReadableByteChannel asReadableByteChannel()
				throws OpenRDFException, TransformerException, IOException,
				ParserConfigurationException, XMLStreamException {
			return Channels.newChannel(asInputStream());
		}

		public ByteArrayOutputStream asByteArrayOutputStream()
				throws OpenRDFException, TransformerException, IOException {
			ByteArrayOutputStream output = new ByteArrayOutputStream(8192);
			try {
				try {
					toOutputStream(output);
				} catch (TupleQueryResultHandlerException e) {
					throw new TransformerException(e);
				} catch (QueryEvaluationException e) {
					throw new TransformerException(e);
				} finally {
					output.close();
				}
			} catch (IOException e) {
				throw new TransformerException(e);
			}
			return output;
		}

		public InputStream asInputStream() throws OpenRDFException,
				TransformerException, IOException {
			return new ByteArrayInputStream(asByteArrayOutputStream()
					.toByteArray());
		}

		public Reader asReader() throws OpenRDFException, TransformerException,
				IOException {
			return new StringReader(asCharArrayWriter().toString());
		}

		public Readable asReadable() throws TransformerException,
				OpenRDFException, IOException {
			return asReader();
		}

		public CharArrayWriter asCharArrayWriter() throws OpenRDFException,
				TransformerException, IOException {
			CharArrayWriter writer = new CharArrayWriter(8192);
			toWriter(writer);
			return writer;
		}

		public void asUpdate() throws OpenRDFException {
			String base = query.getBaseURI();
			String sparql = bindMultiples(query.toString());
			Update qry = bindSingles(con.prepareUpdate(SPARQL, sparql, base));
			qry.execute();
		}

		public void toOutputStream(OutputStream output)
				throws OpenRDFException, TransformerException, IOException {
			if (query.isGraphQuery()) {
				QueryResultUtil.report(asGraphQueryResult(), new RDFXMLWriter(
						output));
			} else if (query.isTupleQuery()) {
				QueryResultUtil.report(asTupleQueryResult(),
						new SPARQLResultsXMLWriter(output));
			} else if (query.isBooleanQuery()) {
				new SPARQLBooleanXMLWriter(output).write(asBoolean());
			} else {
				throw new AssertionError("Unknown query type");
			}
		}

		public void toWriter(Writer writer) throws OpenRDFException,
				TransformerException, IOException {
			if (query.isGraphQuery()) {
				QueryResultUtil.report(asGraphQueryResult(), new RDFXMLWriter(
						writer));
			} else if (query.isTupleQuery()) {
				QueryResultUtil.report(asTupleQueryResult(),
						new SPARQLResultsXMLWriter(new XMLWriter(writer)));
			} else if (query.isBooleanQuery()) {
				new SPARQLBooleanXMLWriter(new XMLWriter(writer))
						.write(asBoolean());
			} else {
				throw new AssertionError("Unknown query type");
			}
		}

		private GraphQuery prepareGraphQuery() throws MalformedQueryException,
				RepositoryException {
			String sparql = bindMultiples(query.toString());
			String base = query.getBaseURI();
			return bindSingles(con.prepareGraphQuery(SPARQL, sparql, base));
		}

		private TupleQuery prepareTupleQuery() throws MalformedQueryException,
				RepositoryException {
			String base = query.getBaseURI();
			String sparql = bindMultiples(query.toString());
			return bindSingles(con.prepareTupleQuery(SPARQL, sparql, base));
		}

		private BooleanQuery prepareBooleanQuery()
				throws MalformedQueryException, RepositoryException {
			String base = query.getBaseURI();
			String sparql = bindMultiples(query.toString());
			return bindSingles(con.prepareBooleanQuery(SPARQL, sparql, base));
		}

		private ObjectQuery prepareObjectQuery(Class<?> concept)
				throws MalformedQueryException, RepositoryException {
			String base = query.getBaseURI();
			String sparql = bindMultiples(query.toObjectString(concept));
			return bindSingles(con.prepareObjectQuery(SPARQL, sparql, base));
		}

		private String bindMultiples(String sparql) {
			if (bindingNames.isEmpty())
				return sparql;
			StringBuilder sb = new StringBuilder(sparql);
			sb.append("\nBINDINGS");
			for (String name : bindingNames) {
				sb.append(" ?").append(name);
			}
			sb.append(" {\n");
			for (List<Value> values : bindingValues) {
				sb.append("\t(");
				for (Value value : values) {
					if (value == null) {
						sb.append("UNDEF");
					} else if (value instanceof URI) {
						writeURI(sb, value);
					} else if (value instanceof BNode) {
						writeBNode(sb, value);
					} else if (value instanceof Literal) {
						writeLiteral(sb, value);
					} else {
						throw new AssertionError();
					}
					sb.append(" ");
				}
				sb.append(")\n");
			}
			sb.append("}\n");
			return sb.toString();
		}

		private void writeBNode(StringBuilder sb, Value value) {
			sb.append("_:").append(value.stringValue());
		}

		private void writeLiteral(StringBuilder sb, Value value) {
			Literal lit = (Literal) value;
			sb.append("\"");
			String label = value.stringValue();
			sb.append(encodeString(label));
			sb.append("\"");
			if (lit.getDatatype() != null) {
				// Append the literal's datatype (possibly written as an
				// abbreviated URI)
				sb.append("^^");
				writeURI(sb, lit.getDatatype());
			}
			if (lit.getLanguage() != null) {
				// Append the literal's language
				sb.append("@");
				sb.append(lit.getLanguage());
			}
		}

		private void writeURI(StringBuilder sb, Value value) {
			sb.append("<");
			String uri = value.stringValue();
			sb.append(encodeURIString(uri));
			sb.append(">");
		}

		private <T extends Operation> T bindSingles(T qry) {
			for (Map.Entry<String, Value> binding : bindings.entrySet()) {
				qry.setBinding(binding.getKey(), binding.getValue());
			}
			return qry;
		}

		private String encodeString(String label) {
			label = label.replace("\\", "\\\\");
			label = label.replace("\t", "\\t");
			label = label.replace("\n", "\\n");
			label = label.replace("\r", "\\r");
			label = label.replace("\"", "\\\"");
			return label;
		}

		private String encodeURIString(String uri) {
			uri = uri.replace("\\", "\\\\");
			uri = uri.replace(">", "\\>");
			return uri;
		}
	}

	private final SparqlQuery sparql;
	private final String systemId;

	public SparqlEvaluator(SparqlQuery query) throws MalformedURLException,
			MalformedQueryException, IOException {
		this.systemId = query.getBaseURI();
		sparql = query;
	}

	@Override
	public String toString() {
		return sparql.toString();
	}

	public SparqlBuilder prepare(ObjectConnection con) {
		return new SparqlBuilder(con, getSparqlQuery());
	}

	private SparqlQuery getSparqlQuery() {
		return sparql;
	}

}
