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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.regex.Pattern;

import org.openrdf.annotations.Iri;
import org.openrdf.annotations.Sparql;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.repository.object.advice.Advice;
import org.openrdf.repository.object.advice.AdviceFactory;
import org.openrdf.repository.object.advice.AdviceProvider;
import org.openrdf.repository.object.advisers.helpers.SparqlEvaluator;

/**
 * Creates Advice for a given method @Sparql annotation.
 */
public class SparqlAdviceFactory implements AdviceFactory, AdviceProvider {
	private static final Pattern NOT_URI = Pattern.compile("\\s|\\}|\\]|\\>|\"");

	private SparqlQueryResolver resolver = SparqlQueryResolver.getInstance();

	public AdviceFactory getAdviserFactory(Class<?> annotationType) {
		if (Sparql.class.equals(annotationType))
			return this;
		return null;
	}

	public Advice createAdvice(Method m) {
		SparqlEvaluator evaluator = createSparqlEvaluator(m);
		return new SparqlAdvice(evaluator, m);
	}

	private SparqlEvaluator createSparqlEvaluator(Method m) {
		String systemId = getSystemId(m);
		String sparql = getSparqlQuery(m);
		try {
			if (NOT_URI.matcher(sparql).find())
				return create(new StringReader(sparql), systemId);
			if (URI.create(sparql).isAbsolute())
				return create(sparql);
			URL url = m.getDeclaringClass().getResource(sparql);
			if (url != null)
				return create(url.toExternalForm());
			String uri = URI.create(systemId).resolve(sparql).toASCIIString();
			return create(uri);
		} catch (IOException e) {
			throw new ExceptionInInitializerError(e);
		} catch (MalformedQueryException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private SparqlEvaluator create(Reader reader, String systemId)
			throws MalformedQueryException, IOException {
		try {
			SparqlQuery query = new SparqlQuery(reader, systemId);
			return new SparqlEvaluator(query);
		} finally {
			reader.close();
		}
	}

	private SparqlEvaluator create(String systemId)
			throws MalformedQueryException, IOException {
		return new SparqlEvaluator(resolver.resolve(systemId));
	}

	private String getSparqlQuery(Method m) {
		return m.getAnnotation(Sparql.class).value();
	}

	private String getSystemId(Method m) {
		if (m.isAnnotationPresent(Iri.class))
			return m.getAnnotation(Iri.class).value();
		Class<?> dclass = m.getDeclaringClass();
		String mame = m.getName();
		if (dclass.isAnnotationPresent(Iri.class)) {
			String url = dclass.getAnnotation(Iri.class).value();
			if (url.indexOf('#') >= 0)
				return url.substring(0, url.indexOf('#') + 1) + mame;
			return url + "#" + mame;
		}
		String name = dclass.getSimpleName() + ".class";
		URL url = dclass.getResource(name);
		if (url != null)
			return url.toExternalForm() + "#" + mame;
		return "java:" + dclass.getName() + "#" + mame;
	}

}
