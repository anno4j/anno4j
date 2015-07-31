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
package org.openrdf.repository.object.vocabulary;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 * Vocabulary of the Messaging Ontology.
 *
 * @author James Leigh
 **/
public class MSG {
	public static final String NAMESPACE = "http://www.openrdf.org/rdf/2011/messaging#";
	public static final URI LITERAL = new URIImpl(NAMESPACE + "literal");
	public static final URI LITERAL_SET = new URIImpl(NAMESPACE + "literalSet");
	public static final URI MATCHING = new URIImpl(NAMESPACE + "matching");
	public static final URI CLASSPATH = new URIImpl(NAMESPACE + "classpath");
	public static final URI MIXIN = new URIImpl(NAMESPACE + "mixin");
	public static final URI MESSAGE = new URIImpl(NAMESPACE + "Message");
	public static final URI OBJECT = new URIImpl(NAMESPACE + "object");
	public static final URI OBJECT_SET = new URIImpl(NAMESPACE + "objectSet");
	public static final URI TARGET = new URIImpl(NAMESPACE + "target");

	private MSG() {
		// prevent instantiation
	}

}
