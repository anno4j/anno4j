/*
 * Copyright (c) 2009, James Leigh All rights reserved.
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
package org.openrdf.repository.object.compiler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFParserRegistry;
import org.openrdf.rio.helpers.StatementCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads ontologies and schemas into memory from remote sources.
 * 
 * @author James Leigh
 *
 */
public class OntologyLoader {

	private Logger logger = LoggerFactory.getLogger(OntologyLoader.class);
	Model model;
	/** context -&gt; prefix -&gt; namespace */
	Map<URI, Map<String, String>> namespaces = new HashMap<URI, Map<String,String>>();
	private Map<URL, RDFFormat> imported = new LinkedHashMap<URL, RDFFormat>();
	private ValueFactory vf = ValueFactoryImpl.getInstance();

	public OntologyLoader() {
		this(new LinkedHashModel());
	}

	public OntologyLoader(Model model) {
		this.model = model;
	}

	public Collection<URL> getImported() {
		return imported.keySet();
	}

	public Map<URL, RDFFormat> getImportedFormats() {
		return imported;
	}

	public Model getModel() {
		return model;
	}

	/** context -&gt; prefix -&gt; namespace */
	public Map<URI, Map<String, String>> getNamespaces() {
		return namespaces;
	}

	public void loadOntologies(Iterable<URL> urls) throws RDFParseException,
			IOException {
		for (URL url : urls) {
			loadOntology(url);
		}
	}

	public void loadOntology(URL url) throws RDFParseException, IOException {
		URI graph = vf.createURI(url.toExternalForm());
		RDFFormat format = loadOntology(url, null, graph);
		imported.put(url, format);
	}

	public void followImports() throws RDFParseException, IOException {
		List<URL> urls = new ArrayList<URL>();
		for (Value obj : model.filter(null, OWL.IMPORTS, null).objects()) {
			if (obj instanceof URI) {
				URI uri = (URI) obj;
				if (!model.contains(null, null, null, uri)
						&& !model.contains(uri, RDF.TYPE, OWL.ONTOLOGY)) {
					URL url = new URL(uri.stringValue());
					if (!imported.containsKey(url)) {
						urls.add(url);
					}
				}
			}
		}
		if (!urls.isEmpty()) {
			for (URL url : urls) {
				String uri = url.toExternalForm();
				RDFFormat format = loadOntology(url, null, vf.createURI(uri));
				imported.put(url, format);
			}
			followImports();
		}
	}

	private RDFFormat loadOntology(URL url, RDFFormat override, final URI uri)
			throws IOException, RDFParseException {
		try {
			URLConnection conn = url.openConnection();
			if (override == null) {
				conn.setRequestProperty("Accept", getAcceptHeader());
			} else {
				conn.setRequestProperty("Accept", override.getDefaultMIMEType());
			}
			RDFFormat format = override;
			if (format == null) {
				String path = conn.getURL().toExternalForm();
				String contentType = conn.getContentType();
				format = forFileName(path, RDFFormat.RDFXML);
				String scheme = java.net.URI.create(conn.getURL().toExternalForm()).getScheme();
				if (contentType != null && !"file".equals(scheme)) {
					format = forMIMEType(contentType, format);
				}
			}
			RDFParserRegistry registry = RDFParserRegistry.getInstance();
			RDFParser parser = registry.get(format).getParser();
			parser.setRDFHandler(new StatementCollector(model) {
				@Override
				public void handleStatement(Statement st) {
					Resource s = st.getSubject();
					URI p = st.getPredicate();
					Value o = st.getObject();
					super.handleStatement(new ContextStatementImpl(s, p, o, uri));
				}

				@Override
				public void handleNamespace(String prefix, String ns)
						throws RDFHandlerException {
					Map<String, String> map = namespaces.get(uri);
					if (map == null) {
						namespaces
								.put(uri, map = new HashMap<String, String>());
					}
					map.put(prefix, ns);
					if (model.getNamespace(prefix) == null) {
						model.setNamespace(prefix, ns);
					}
				}
			});
			InputStream in = conn.getInputStream();
			try {
				parser.parse(in, url.toExternalForm());
				return format;
			} catch (RDFHandlerException e) {
				throw new AssertionError(e);
			} catch (RDFParseException e) {
				if (override == null && format.equals(RDFFormat.NTRIPLES)) {
					// sometimes text/plain is used for rdf+xml
					return loadOntology(url, RDFFormat.RDFXML, uri);
				} else {
					throw e;
				}
			} finally {
				in.close();
			}
		} catch (RDFParseException e) {
			logger.warn("Could not load {} {}", url, e.getMessage());
			String msg = e.getMessage() + " in " + url;
			throw new RDFParseException(msg, e.getLineNumber(), e.getColumnNumber());
		} catch (IOException e) {
			logger.warn("Could not load {} {}", url, e.getMessage());
			return null;
		} catch (SecurityException e) {
			logger.warn("Could not load {} {}", url, e.getMessage());
			return null;
		}
	}

	private RDFFormat forFileName(String path, RDFFormat fallback) {
		RDFFormat format = RDFFormat.forFileName(path);
		RDFParserRegistry registry = RDFParserRegistry.getInstance();
		if (format != null && registry.has(format))
			return format;
		return fallback;
	}

	private RDFFormat forMIMEType(String contentType, RDFFormat fallback) {
		RDFFormat format = RDFFormat.forMIMEType(contentType);
		RDFParserRegistry registry = RDFParserRegistry.getInstance();
		if (format != null && registry.has(format))
			return format;
		return fallback;
	}

	private String getAcceptHeader() {
		StringBuilder sb = new StringBuilder();
		String preferred = RDFFormat.RDFXML.getDefaultMIMEType();
		sb.append(preferred).append(";q=0.2");
		Set<RDFFormat> rdfFormats = RDFParserRegistry.getInstance().getKeys();
		for (RDFFormat format : rdfFormats) {
			for (String type : format.getMIMETypes()) {
				if (!preferred.equals(type)) {
					sb.append(", ").append(type);
				}
			}
		}
		return sb.toString();
	}
}
