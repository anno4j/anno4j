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
package org.openrdf.repository.object.config;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

/**
 * Static object configuration schema.
 * 
 * @author James Leigh
 *
 */
public class ObjectRepositorySchema {

	/**
	 * The ObjectRepository schema namespace (
	 * <tt>http://www.openrdf.org/config/repository/object#</tt>).
	 */
	public static final String NAMESPACE = "http://www.openrdf.org/config/repository/object#";

	/** <tt>http://www.openrdf.org/config/repository/object#datatype</tt> */
	public final static URI DATATYPE;

	/** <tt>http://www.openrdf.org/config/repository/object#concept</tt> */
	public final static URI CONCEPT;

	/** <tt>http://www.openrdf.org/config/repository/object#behaviour</tt> */
	public final static URI BEHAVIOUR;

	/** <tt>http://www.openrdf.org/config/repository/object#knownAs</tt> */
	public final static URI KNOWN_AS;

	/** <tt>http://www.openrdf.org/config/repository/object#conceptJar</tt> */
	public final static URI CONCEPT_JAR;

	/** <tt>http://www.openrdf.org/config/repository/object#behaviourJar</tt> */
	public final static URI BEHAVIOUR_JAR;

	/** <tt>http://www.openrdf.org/config/repository/object#blobStore</tt> */
	public final static URI BLOB_STORE;

	/** <tt>http://www.openrdf.org/config/repository/object#blobStoreParameter</tt> */
	public final static URI BLOB_STORE_PARAMETER;

	static {
		ValueFactory vf = ValueFactoryImpl.getInstance();
		DATATYPE = vf.createURI(NAMESPACE, "datatype");
		CONCEPT = vf.createURI(NAMESPACE, "concept");
		BEHAVIOUR = vf.createURI(NAMESPACE, "behaviour");
		KNOWN_AS = vf.createURI(NAMESPACE, "knownAs");
		CONCEPT_JAR = vf.createURI(NAMESPACE, "conceptJar");
		BEHAVIOUR_JAR = vf.createURI(NAMESPACE, "behaviourJar");
		BLOB_STORE = vf.createURI(NAMESPACE, "blobStore");
		BLOB_STORE_PARAMETER = vf.createURI(NAMESPACE, "blobStoreParameter");
	}
}
