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

import static javax.xml.XMLConstants.NULL_NS_URI;

import javax.xml.namespace.QName;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.repository.object.managers.Marshall;

/**
 * Converts QName to and from Literal.
 * 
 * @author James Leigh
 *
 */
public class QNameMarshall  implements Marshall<QName> {
	private ValueFactory vf;

	public QNameMarshall(ValueFactory vf) {
		this.vf = vf;
	}

	public String getJavaClassName() {
		return QName.class.getName();
	}

	public URI getDatatype() {
		return XMLSchema.QNAME;
	}

	public void setDatatype(URI datatype) {
		if (!datatype.equals(XMLSchema.QNAME))
			throw new IllegalArgumentException(datatype.toString());
	}

	public QName deserialize(Literal literal) {
		String label = literal.getLabel();
		int idx = label.indexOf(':');
		if (label.charAt(0) == '{' || idx < 0)
			return QName.valueOf(label);
		String prefix = label.substring(0, idx);
		return new QName(NULL_NS_URI, label.substring(idx + 1), prefix);
	}

	public Literal serialize(QName object) {
		if (object.getPrefix().length() == 0)
			return vf.createLiteral(object.toString());
		StringBuilder label = new StringBuilder();
		label.append(object.getPrefix());
		label.append(":");
		label.append(object.getLocalPart());
		return vf.createLiteral(label.toString());
	}

}
