/*
 * Copyright (c) 2008-2009, James Leigh All rights reserved.
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

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.repository.object.managers.Marshall;

/**
 * Converts Duration to and from Literal.
 * 
 * @author James Leigh
 *
 */
public class DurationMarshall implements Marshall<Duration> {
	private ValueFactory vf;

	private DatatypeFactory factory;

	private Class<? extends Duration> javaClass;

	private URI datatype;

	public DurationMarshall(ValueFactory vf)
			throws DatatypeConfigurationException {
		this.vf = vf;
		factory = DatatypeFactory.newInstance();
		javaClass = factory.newDuration(0).getClass();
		datatype = XMLSchema.DURATION;
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

	public Duration deserialize(Literal literal) {
		return factory.newDuration(literal.getLabel());
	}

	public Literal serialize(Duration object) {
		return vf.createLiteral(object.toString(), datatype);
	}
}
