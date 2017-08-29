/*
 * Copyright (c) 2008-2010, Zepheira LLC Some rights reserved.
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
package org.openrdf.repository.object.compiler.source;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.openrdf.annotations.Iri;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.repository.object.compiler.JavaNameResolver;
import org.openrdf.repository.object.compiler.model.RDFClass;
import org.openrdf.repository.object.compiler.model.RDFProperty;
import org.openrdf.repository.object.exceptions.ObjectStoreConfigException;

/**
 * Adds methods for implementing messages.
 * 
 * @author James Leigh
 * 
 */
public class JavaMessageBuilder extends JavaAnnotationBuilder {

	public JavaMessageBuilder(File source, JavaNameResolver resolver)
			throws FileNotFoundException {
		super(source, resolver);
	}

	public void message(RDFClass msg) throws ObjectStoreConfigException {
		String methodName = getMessageName(msg);
		if (methodName == null)
			return; // anonymous super class
		URI uri = msg.getURI();
		if (isBeanProperty(msg, methodName)) {
			uri = null;
		}
		JavaMethodBuilder code = method(methodName, true);
		comment(code, msg);
		annotationProperties(code, msg);
		URI rdfType = resolver.getType(uri);
		if (rdfType != null) {
			code.annotateURI(Iri.class, "value", rdfType);
		}
		RDFProperty response = msg.getResponseProperty();
		String range = getResponseClassName(msg, response);
		if (msg.isFunctional(response)) {
			code.returnType(range);
		} else {
			code.returnSetOf(range);
		}
		for (RDFProperty param : msg.getParameters()) {
			String type = getParameterClassName(msg, param);
			URI pred = param.getURI();
			URI rdf = resolver.getType(pred);
			annotationProperties(code, param);
			for (RDFClass c : msg.getRestrictions()) {
				RDFProperty property = c.getRDFProperty(OWL.ONPROPERTY);
				if (param.equals(property)) {
					annotationProperties(code, c);
				}
			}
			if (rdf != null) {
				code.annotateURI(Iri.class, "value", rdf);
			}
			if (msg.isFunctional(param)) {
				String name = resolver.getSingleParameterName(pred);
				code.param(type, name);
			} else {
				String name = resolver.getPluralParameterName(pred);
				code.paramSetOf(type, name);
			}
		}
		code.end();
	}

	private String getMessageName(RDFClass msg) {
		List<? extends Value> list = msg.getList(OWL.INTERSECTIONOF);
		if (list != null) {
			for (Value value : list) {
				if (value instanceof URI) {
					RDFClass rc = new RDFClass(msg.getModel(), (URI) value);
					if (rc.isMessageClass()) {
						String name = getMessageName(rc);
						if (name != null)
							return name;
					}
				}
			}
		}
		if (resolver.isAnonymous(msg.getURI()))
			return null;
		return resolver.getMethodName(msg.getURI());
	}

	private boolean isBeanProperty(RDFClass code, String methodName)
			throws ObjectStoreConfigException {
		if (methodName.startsWith("get") && code.getParameters().isEmpty()) {
			return true;
		}
		if (methodName.startsWith("set") && code.getParameters().size() == 1) {
			return true;
		}
		if (methodName.startsWith("is") && code.getParameters().isEmpty()) {
			RDFProperty response = code.getResponseProperty();
			String range = getResponseClassName(code, response);
			if ("boolean".equals(range))
				return true;
		}
		return false;
	}
}
