/*
 * Copyright (c) 2012, 3 Round Stones Inc. Some rights reserved.
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

import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.object.LangString;
import org.openrdf.repository.object.managers.Marshall;

/**
 * Converts {@link LangString} to and from {@link Literal}.
 * 
 * @author James Leigh
 *
 */
public class LangStringMarshall<T> implements Marshall<T> {
	private final ValueFactory vf;
	private final Class<T> type;
	private URI datatype;

	public LangStringMarshall(ValueFactory vf, Class<T> type) {
		this(vf, type, vf.createURI(RDF.NAMESPACE + "langString"));
	}

	public LangStringMarshall(ValueFactory vf, Class<T> type, URI datatype) {
		assert type.isAssignableFrom(LangString.class);
		this.vf = vf;
		this.type = type;
		this.datatype = datatype;
	}

	public String getJavaClassName() {
		return type.getName();
	}

	public URI getDatatype() {
		return datatype;
	}

	public void setDatatype(URI datatype) {
		this.datatype = datatype;
	}

	public T deserialize(Literal literal) {
		String lang = literal.getLanguage();
		if (lang == null)
			return type.cast(literal.getLabel());
		return type.cast(new LangString(literal.getLabel(), lang));
	}

	public Literal serialize(T text) {
		if (text instanceof LangString)
			return vf.createLiteral(text.toString(), ((LangString)text).getLang());
		return vf.createLiteral(text.toString());
	}

}
