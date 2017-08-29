/*
 * Copyright (c) 2008-2009, Zepheira All rights reserved.
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
package org.openrdf.repository.object.compiler.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.BNode;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.object.compiler.JavaNameResolver;
import org.openrdf.repository.object.compiler.RDFList;

/**
 * Utility class for accessing properties of a resource in a {@link Model}.
 * 
 * @author James Leigh
 *
 */
public class RDFEntity implements Comparable<RDFEntity> {
	protected Model model;
	protected Resource self;

	public RDFEntity(Model model, Resource self) {
		this.model = model;
		this.self = self;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((self == null) ? 0 : self.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RDFEntity other = (RDFEntity) obj;
		if (self == null) {
			if (other.self != null)
				return false;
		} else if (!self.equals(other.self))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return self.toString();
	}

	public int compareTo(RDFEntity o) {
		return self.stringValue().compareTo(o.self.stringValue());
	}

	public Model getModel() {
		return model;
	}

	public Resource getResource() {
		return self;
	}

	public URI getURI() {
		if (self instanceof URI)
			return (URI) self;
		return null;
	}

	public boolean isA(URI type) {
		return model.contains(self, RDF.TYPE, type);
	}

	public Set<? extends Statement> getStatements(Collection<URI> preds) {
		Set<Statement> result = new HashSet<Statement>();
		for (URI pred : preds) {
			result.addAll(model.filter(self, pred, null));
		}
		return result;
	}

	public Set<? extends Value> getValues(URI pred) {
		return model.filter(self, pred, null).objects();
	}

	public Collection<String> getStrings(URI pred) {
		Collection<String> set = new ArrayList<String>();
		for (Value value : getValues(pred)) {
			if (value instanceof BNode) {
				for (Value v : new RDFList(model, (BNode) value).asList()) {
					if (v instanceof BNode)
						return Collections.EMPTY_SET;
					set.add(v.stringValue());
				}
			} else {
				set.add(value.stringValue());
			}
		}
		return set;
	}

	public String getString(URI pred) {
		return model.filter(self, pred, null).objectString();
	}

	public RDFClass getRDFClass(URI pred) {
		Resource subj = model.filter(self, pred, null).objectResource();
		if (subj == null)
			return null;
		return new RDFClass(model, subj);
	}

	public Collection<RDFClass> getRDFClasses(URI pred) {
		final Collection<RDFClass> set = new ArrayList<RDFClass>();
		for (Value value : getValues(pred)) {
			if (value instanceof Resource) {
				Resource subj = (Resource) value;
				if (model.contains(subj, RDF.TYPE, RDF.LIST)) {
					for (Value v : new RDFList(model, subj).asList()) {
						if (v instanceof Resource) {
							set.add(new RDFClass(model, (Resource) v));
						}
					}
				} else {
					set.add(new RDFClass(model, subj));
				}
			}
		}
		return set;
	}

	public Set<RDFProperty> getRDFProperties(URI pred) {
		Set<RDFProperty> set = new HashSet<RDFProperty>();
		for (Value value : model.filter(self, pred, null).objects()) {
			if (value instanceof Resource) {
				Resource subj = (Resource) value;
				set.add(new RDFProperty(model, subj));
			}
		}
		return set;
	}

	public Set<RDFProperty> getRDFProperties() {
		Set<RDFProperty> set = new HashSet<RDFProperty>();
		for (URI pred : model.filter(self, null, null).predicates()) {
			set.add(new RDFProperty(model, pred));
		}
		return set;
	}

	protected File createSourceFile(File dir, JavaNameResolver resolver) {
		String pkg = resolver.getPackageName(getURI());
		String simple = resolver.getSimpleName(getURI());
		File folder = dir;
		if (pkg != null) {
			folder = new File(dir, pkg.replace('.', '/'));
		}
		folder.mkdirs();
		return new File(folder, simple + ".java");
	}
}
