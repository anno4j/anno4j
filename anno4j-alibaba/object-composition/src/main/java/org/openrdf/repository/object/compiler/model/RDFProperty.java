/*
 * Copyright (c) 2008-2010, Zepheira LLC Some rights reserved.
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
package org.openrdf.repository.object.compiler.model;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.openrdf.annotations.Iri;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.repository.object.compiler.JavaNameResolver;
import org.openrdf.repository.object.compiler.source.JavaMessageBuilder;
import org.openrdf.repository.object.exceptions.ObjectStoreConfigException;

/**
 * Utility class for working with an rdf:Property in a {@link Model}.
 * 
 * @author James Leigh
 *
 */
public class RDFProperty extends RDFEntity {

	public RDFProperty(Model model, Resource self) {
		super(model, self);
	}

	public File generateAnnotationCode(File dir, JavaNameResolver resolver)
			throws IOException, ObjectStoreConfigException {
		File source = createSourceFile(dir, resolver);
		JavaMessageBuilder builder = new JavaMessageBuilder(source, resolver);
		annotationHeader(builder);
		builder.close();
		return source;
	}

	private void annotationHeader(JavaMessageBuilder builder)
			throws ObjectStoreConfigException {
		String pkg = builder.getPackageName(this.getURI());
		String simple = builder.getSimpleName(this.getURI());
		if (pkg == null) {
			builder.imports(simple);
		} else {
			builder.pkg(pkg);
			builder.imports(pkg + '.' + simple);
		}
		builder.comment(this);
		if (this.isA(OWL.DEPRECATEDPROPERTY)) {
			builder.annotate(Deprecated.class);
		}
		builder.annotateEnum(Retention.class, "value", RetentionPolicy.class, "RUNTIME");
		builder.annotateEnums(Target.class, "value", ElementType.class, "TYPE", "METHOD",
					"PARAMETER", "ANNOTATION_TYPE", "PACKAGE");
		builder.annotationName(simple);
		builder.annotationProperties(this);
		builder.annotateURI(Iri.class, "value", builder.getType(this.getURI()));
		if (this.isA(OWL.FUNCTIONALPROPERTY)) {
			builder.method("value", true).returnType(builder.imports(String.class)).end();
		} else {
			builder.method("value", true).returnType(builder.imports(String.class) + "[]")
					.end();
		}
	}

}
