/*
 * Copyright (c) 2007-2009, James Leigh All rights reserved.
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
package org.openrdf.repository.object.behaviours;

import org.openrdf.annotations.ParameterTypes;
import org.openrdf.model.Resource;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.RDFObject;
import org.openrdf.repository.object.advisers.helpers.ObjectQueryFactory;
import org.openrdf.repository.object.traits.ManagedRDFObject;
import org.openrdf.repository.object.traits.ObjectMessage;
import org.openrdf.repository.object.traits.RDFObjectBehaviour;

/**
 * Stores the resource and manager for a bean and implements equals, hashCode,
 * and toString.
 * 
 * @author James Leigh
 * 
 */
public class RDFObjectImpl implements ManagedRDFObject, RDFObject {
	private ObjectConnection manager;
	private ObjectQueryFactory factory;
	private Resource resource;

	public void initRDFObject(Resource resource, ObjectQueryFactory factory, ObjectConnection manager) {
		this.manager = manager;
		this.factory = factory;
		this.resource = resource;
	}

	public ObjectConnection getObjectConnection() {
		return manager;
	}

	public ObjectQueryFactory getObjectQueryFactory() {
		return factory;
	}

	public Resource getResource() {
		return resource;
	}

	@Override
	public boolean equals(Object obj) {
		if (resource == null)
			return false;
		if (obj instanceof RDFObjectBehaviour)
			return equals(((RDFObjectBehaviour) obj).getBehaviourDelegate());
		return obj instanceof RDFObject
				&& resource.equals(((RDFObject) obj).getResource());
	}

	@Override
	public int hashCode() {
		if (resource == null)
			return 0;
		return resource.hashCode();
	}

	@ParameterTypes({})
	public String toString(ObjectMessage msg) throws Exception {
		Object ret = msg.proceed();
		if (ret == null && resource != null)
			return resource.toString();
		return ret.toString();
	}
}
