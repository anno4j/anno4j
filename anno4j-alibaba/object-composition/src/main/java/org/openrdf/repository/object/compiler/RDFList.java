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
package org.openrdf.repository.object.compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;

/**
 * Utility class for working with rdf:List in a {@link Model}.
 * 
 * @author James Leigh
 *
 */
public class RDFList {

	private ValueFactory vf = ValueFactoryImpl.getInstance();

	private RDFDataSource triples;

	private Value start;

	public RDFList(Model model, Value start) {
		this(new RDFDataSource(model), start);
	}

	public RDFList(RDFDataSource triples, Value start) {
		this.triples = triples;
		this.start = start;
	}

	public List<? extends Value> asList() {
		if (start == null)
			return Collections.emptyList();
		List<Value> list = new ArrayList<Value>();
		return copyTo(start, list);
	}

	public void addAllOthers(RDFList list) {
		List<? extends Value> l = list.asList();
		l.removeAll(asList());
		addAll(l);
	}

	private void addAll(List<? extends Value> list) {
		if (start == null) {
			start = vf.createBNode();
		}
		for (Value element : list) {
			addTo((Resource) start, element);
		}
	}

	private List<Value> copyTo(Value node, List<Value> list) {
		Value first = triples.match(node, RDF.FIRST, null).objectValue();
		Resource rest = triples.match(node, RDF.REST, null).objectResource();
		if (first == null)
			return list;
		list.add(first);
		return copyTo(rest, list);
	}

	private void addTo(Resource node, Value element) {
		if (triples.contains(node, RDF.FIRST, null)) {
			Resource rest = triples.match(node, RDF.REST, null).objectResource();
			if (rest == null || rest.equals(RDF.NIL)) {
				rest = vf.createBNode();
				triples.remove(node, RDF.REST, null);
				triples.add(node, RDF.REST, rest);
			}
			addTo(rest, element);
		} else {
			triples.add(node, RDF.FIRST, element);
			triples.add(node, RDF.REST, RDF.NIL);
		}
	}

}
