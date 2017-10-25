/*
 * Copyright (c) 2009, James Leigh All rights reserved.
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
package org.openrdf.repository.object.managers.helpers;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import javassist.bytecode.AccessFlag;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.annotation.Annotation;

import org.openrdf.annotations.Iri;
import org.openrdf.annotations.Matching;

/**
 * Filter for detecting @rdf annotated class files.
 * 
 * @author James Leigh
 *
 */
public class CheckForConcept {

	protected ClassLoader cl;

	public CheckForConcept(ClassLoader cl) {
		this.cl = cl;
	}

	public String getName() {
		return "concepts";
	}

	public String getClassName(String name, InputStream stream) throws IOException {
		DataInputStream dstream = new DataInputStream(stream);
		try {
			ClassFile cf = new ClassFile(dstream);
			if (checkAccessFlags(cf.getAccessFlags())) {
				if (isAnnotationPresent(cf))
					return cf.getName();
			}
		} finally {
			dstream.close();
		}
		return null;
	}

	protected boolean checkAccessFlags(int flags) {
		return (flags & AccessFlag.ANNOTATION) == 0;
	}

	protected boolean isAnnotationPresent(ClassFile cf) {
		// concept with an annotation
		AnnotationsAttribute attr = (AnnotationsAttribute) cf
				.getAttribute(AnnotationsAttribute.visibleTag);
		return isAnnotationPresent(attr);
	}

	protected boolean isAnnotationPresent(AnnotationsAttribute attr) {
		if (attr != null) {
			Annotation[] annotations = attr.getAnnotations();
			if (annotations != null) {
				for (Annotation ann : annotations) {
					if (ann.getTypeName().equals(Iri.class.getName()))
						return true;
					if (ann.getTypeName().equals(Matching.class.getName()))
						return true;
				}
			}
		}
		return false;
	}
}
