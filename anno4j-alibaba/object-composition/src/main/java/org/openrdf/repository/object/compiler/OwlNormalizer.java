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
package org.openrdf.repository.object.compiler;

import info.aduna.net.ParsedURI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.repository.object.vocabulary.MSG;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Applies a series of rules against the ontology, making it easier to convert
 * into Java classes. This includes applying some OWL reasoning on properties,
 * renaming anonymous and foreign classes.
 * 
 * @author James Leigh
 * 
 */
public class OwlNormalizer {
	private final Logger logger = LoggerFactory.getLogger(OwlNormalizer.class);
	private RDFDataSource ds;
	private Set<URI> anonymousClasses = new HashSet<URI>();
	private Map<URI, URI> aliases = new HashMap<URI, URI>();
	private Map<String, String> implNames = new HashMap<String, String>();
	private Set<String> commonNS = new HashSet<String>(Arrays.asList(
			RDF.NAMESPACE, RDFS.NAMESPACE, OWL.NAMESPACE));

	public OwlNormalizer(RDFDataSource ds) {
		this.ds = ds;
	}

	public URI getOriginal(URI alias) {
		if (anonymousClasses.contains(alias))
			return null;
		if (aliases.containsKey(alias))
			return aliases.get(alias);
		return alias;
	}

	public Map<URI, URI> getAliases() {
		return aliases;
	}

	public Set<URI> getAnonymousClasses() {
		return anonymousClasses;
	}

	public Map<String, String> getImplNames() {
		return implNames;
	}

	public void normalize() {
		infer();
		createJavaAnnotations();
		checkPropertyDomains();
		checkPropertyRanges();
		subClassIntersectionOf();
		hasValueFromList();
		subClassOneOf();
		distributeEquivalentClasses();
		renameAnonymousClasses();
		mergeUnionClasses();
		distributeSubMessage();
		checkMessageTargets();
	}

	/**
	 * Treat owl:complementOf, owl:intersectionOf, owl:oneOf, and owl:unionOf as
	 * annotations so they will be saved in the concept header.
	 */
	private void createJavaAnnotations() {
		if (ds.contains(RDFS.LITERAL, null, null)) {
			ds.add(RDFS.LITERAL, RDF.TYPE, RDFS.DATATYPE);
		}
		if (ds.contains(null, RDFS.SUBCLASSOF, null)) {
			ds.add(RDFS.SUBCLASSOF, RDF.TYPE, OWL.ANNOTATIONPROPERTY);
		}
		if (ds.contains(null, RDFS.SUBPROPERTYOF, null)) {
			ds.add(RDFS.SUBPROPERTYOF, RDF.TYPE, OWL.ANNOTATIONPROPERTY);
		}
		if (ds.contains(null, OWL.EQUIVALENTCLASS, null)) {
			ds.add(OWL.EQUIVALENTCLASS, RDF.TYPE, OWL.ANNOTATIONPROPERTY);
		}
		if (ds.contains(null, OWL.COMPLEMENTOF, null)) {
			ds.add(OWL.COMPLEMENTOF, RDF.TYPE, OWL.ANNOTATIONPROPERTY);
			ds.add(OWL.COMPLEMENTOF, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
			ds.add(OWL.COMPLEMENTOF, RDFS.RANGE, OWL.CLASS);
		}
		if (ds.contains(null, OWL.INTERSECTIONOF, null)) {
			ds.add(OWL.INTERSECTIONOF, RDF.TYPE, OWL.ANNOTATIONPROPERTY);
		}
		if (ds.contains(null, OWL.UNIONOF, null)) {
			ds.add(OWL.UNIONOF, RDF.TYPE, OWL.ANNOTATIONPROPERTY);
		}
		if (ds.contains(null, OWL.HASVALUE, null)) {
			ds.add(OWL.HASVALUE, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
			ds.add(OWL.HASVALUE, RDF.TYPE, OWL.ANNOTATIONPROPERTY);
			ds.add(OWL.ONEOF, RDF.TYPE, OWL.ANNOTATIONPROPERTY);
		} else if (ds.contains(null, OWL.ONEOF, null)) {
			ds.add(OWL.ONEOF, RDF.TYPE, OWL.ANNOTATIONPROPERTY);
		}
		if (ds.contains(null, OWL.SOMEVALUESFROM, null)) {
			ds.add(OWL.SOMEVALUESFROM, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
			ds.add(OWL.SOMEVALUESFROM, RDF.TYPE, OWL.ANNOTATIONPROPERTY);
			ds.add(OWL.SOMEVALUESFROM, RDFS.RANGE, OWL.CLASS);
		}
		if (ds.contains(null, OWL.MAXCARDINALITY, null)) {
			ds.add(OWL.MAXCARDINALITY, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
			ds.add(OWL.MAXCARDINALITY, RDF.TYPE, OWL.ANNOTATIONPROPERTY);
			ds.add(OWL.MAXCARDINALITY, RDFS.RANGE, XMLSchema.NON_NEGATIVE_INTEGER);
		}
		if (ds.contains(null, OWL.MINCARDINALITY, null)) {
			ds.add(OWL.MINCARDINALITY, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
			ds.add(OWL.MINCARDINALITY, RDF.TYPE, OWL.ANNOTATIONPROPERTY);
			ds.add(OWL.MINCARDINALITY, RDFS.RANGE, XMLSchema.NON_NEGATIVE_INTEGER);
		}
		if (ds.contains(null, OWL.CARDINALITY, null)) {
			ds.add(OWL.CARDINALITY, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
			ds.add(OWL.CARDINALITY, RDF.TYPE, OWL.ANNOTATIONPROPERTY);
			ds.add(OWL.CARDINALITY, RDFS.RANGE, XMLSchema.NON_NEGATIVE_INTEGER);
		}
	}

	private void infer() {
		logger.debug("inferring");
		ValueFactory vf = getValueFactory();
		propagateSubClassType(RDFS.CLASS);
		symmetric(OWL.INVERSEOF);
		symmetric(OWL.EQUIVALENTCLASS);
		symmetric(OWL.EQUIVALENTPROPERTY);
		symmetric(OWL.DISJOINTWITH);
		setSubjectType(RDF.FIRST, null, RDF.LIST);
		setSubjectType(RDF.REST, null, RDF.LIST);
		setSubjectType(RDFS.SUBCLASSOF, null, OWL.CLASS);
		setSubjectType(OWL.ONEOF, null, OWL.CLASS);
		setSubjectType(OWL.UNIONOF, null, OWL.CLASS);
		setSubjectType(OWL.DISJOINTWITH, null, OWL.CLASS);
		setSubjectType(OWL.COMPLEMENTOF, null, OWL.CLASS);
		setSubjectType(OWL.EQUIVALENTCLASS, null, OWL.CLASS);
		setSubjectType(OWL.INTERSECTIONOF, null, OWL.CLASS);
		setSubjectType(OWL.ONPROPERTY, null, OWL.RESTRICTION);
		setSubjectType(RDF.TYPE, RDFS.CLASS, OWL.CLASS);
		setSubjectType(RDF.TYPE, OWL.DEPRECATEDCLASS, OWL.CLASS);
		setSubjectType(RDF.TYPE, OWL.RESTRICTION, OWL.CLASS);
		setSubjectType(RDF.TYPE, OWL.ANNOTATIONPROPERTY, RDF.PROPERTY);
		setSubjectType(RDF.TYPE, OWL.DEPRECATEDPROPERTY, RDF.PROPERTY);
		setSubjectType(RDF.TYPE, OWL.OBJECTPROPERTY, RDF.PROPERTY);
		setSubjectType(RDF.TYPE, OWL.DATATYPEPROPERTY, RDF.PROPERTY);
		setSubjectType(RDF.TYPE, OWL.FUNCTIONALPROPERTY, RDF.PROPERTY);
		setObjectType(RDFS.SUBCLASSOF, OWL.CLASS);
		setObjectType(OWL.ALLVALUESFROM, OWL.CLASS);
		setObjectType(OWL.ONEOF, RDF.LIST);
		setObjectType(OWL.UNIONOF, RDF.LIST);
		setObjectType(OWL.INTERSECTIONOF, RDF.LIST);
		setObjectType(RDFS.ISDEFINEDBY, OWL.ONTOLOGY);
		setSubjectType(OWL.INVERSEOF, null, OWL.OBJECTPROPERTY);
		setObjectType(OWL.INVERSEOF, OWL.OBJECTPROPERTY);
		setObjectType(RDFS.RANGE, OWL.CLASS);
		setObjectType(RDFS.DOMAIN, OWL.CLASS);
		setSubjectType(RDFS.RANGE, null, RDF.PROPERTY);
		setSubjectType(RDFS.DOMAIN, null, RDF.PROPERTY);
		setSubjectType(RDFS.SUBPROPERTYOF, null, RDF.PROPERTY);
		setObjectType(RDFS.SUBPROPERTYOF, RDF.PROPERTY);
		setDatatype(vf, OWL.CARDINALITY, XMLSchema.NON_NEGATIVE_INTEGER);
		setDatatype(vf, OWL.MINCARDINALITY, XMLSchema.NON_NEGATIVE_INTEGER);
		setDatatype(vf, OWL.MAXCARDINALITY, XMLSchema.NON_NEGATIVE_INTEGER);
		setMemberType(OWL.UNIONOF, OWL.CLASS);
		setMemberType(OWL.INTERSECTIONOF, OWL.CLASS);
	}

	private void setMemberType(URI pred, URI type) {
		for (Value list : ds.match(null, pred, null).objects()) {
			if (list instanceof Resource) {
				RDFList members = new RDFList(ds, (Resource) list);
				for (Value member : members.asList()) {
					if (member instanceof Resource) {
						ds.add((Resource) member, RDF.TYPE, type);
					}
				}
			}
		}
	}

	private void propagateSubClassType(Resource classDef) {
		for (Resource c : findClasses(Collections.singleton(classDef))) {
			if (c.equals(RDFS.DATATYPE))
				continue;
			for (Statement stmt : ds.match(null, RDF.TYPE, c)) {
				Resource subj = stmt.getSubject();
				ds.add(subj, RDF.TYPE, classDef);
			}
		}
	}

	private Set<Resource> findClasses(Collection<Resource> classes) {
		Set<Resource> set = new HashSet<Resource>(classes);
		for (Resource c : classes) {
			for (Statement stmt : ds.match(null, RDFS.SUBCLASSOF, c)) {
				Resource subj = stmt.getSubject();
				set.add(subj);
			}
		}
		if (set.size() > classes.size()) {
			return findClasses(set);
		} else {
			return set;
		}
	}

	private void symmetric(URI pred) {
		for (Statement stmt : ds.match(null, pred, null)) {
			if (stmt.getObject() instanceof Resource) {
				Resource subj = (Resource) stmt.getObject();
				ds.add(subj, pred, stmt.getSubject());
			} else {
				logger.warn("Invalid statement {}", stmt);
			}
		}
	}

	private void setSubjectType(URI pred, Value obj, URI type) {
		for (Statement stmt : ds.match(null, pred, obj)) {
			ds.add(stmt.getSubject(), RDF.TYPE, type);
		}
	}

	private void setObjectType(URI pred, URI type) {
		for (Statement st : ds.match(null, pred, null)) {
			if (st.getObject() instanceof Resource) {
				Resource subj = (Resource) st.getObject();
				ds.add(subj, RDF.TYPE, type);
			} else {
				logger.warn("Invalid statement {}", st);
			}
		}
	}

	private void setDatatype(ValueFactory vf, URI pred, URI datatype) {
		for (Statement stmt : ds.match(null, pred, null)) {
			String label = ((Literal) stmt.getObject()).getLabel();
			Literal literal = vf.createLiteral(label, datatype);
			ds.remove(stmt.getSubject(), pred, stmt.getObject());
			ds.add(stmt.getSubject(), pred, literal);
		}
	}

	private void checkPropertyDomains() {
		loop: for (Statement st : ds.match(null, RDF.TYPE, RDF.PROPERTY)) {
			Resource p = st.getSubject();
			if (!ds.contains(p, RDFS.DOMAIN, null)) {
				for (Value sup : ds.match(p, RDFS.SUBPROPERTYOF, null).objects()) {
					for (Value obj : ds.match(sup, RDFS.DOMAIN, null).objects()) {
						ds.add(p, RDFS.DOMAIN, obj);
						continue loop;
					}
				}
				ds.add(p, RDFS.DOMAIN, RDFS.RESOURCE);
				if (!ds.contains(RDFS.RESOURCE, RDF.TYPE, OWL.CLASS)) {
					ds.add(RDFS.RESOURCE, RDF.TYPE, OWL.CLASS);
				}
			}
		}
	}

	private void checkPropertyRanges() {
		loop: for (Statement st : ds.match(null, RDF.TYPE, RDF.PROPERTY)) {
			Resource p = st.getSubject();
			if (!ds.contains(p, RDFS.RANGE, null)) {
				for (Value sup : ds.match(p, RDFS.SUBPROPERTYOF, null).objects()) {
					for (Value obj : ds.match(sup, RDFS.RANGE, null).objects()) {
						ds.add(p, RDFS.RANGE, obj);
						continue loop;
					}
				}
				ds.add(p, RDFS.RANGE, RDFS.RESOURCE);
			}
		}
	}

	private void distributeSubMessage() {
		boolean changed = false;
		for (Resource msg : ds.match(null, RDFS.SUBCLASSOF, MSG.MESSAGE)
				.subjects()) {
			for (Resource sub : ds.match(null, RDFS.SUBCLASSOF, msg).subjects()) {
				if (!ds.contains(sub, RDFS.SUBCLASSOF, MSG.MESSAGE)) {
					ds.add(sub, RDFS.SUBCLASSOF, MSG.MESSAGE);
					changed = true;
				}
			}
		}
		if (changed) {
			distributeSubMessage();
		}
	}

	private void checkMessageTargets() {
		for (Resource msg : ds.match(null, RDFS.SUBCLASSOF, MSG.MESSAGE)
				.subjects()) {
			getOrAddTargetRestriction(msg);
		}
	}

	private Value getOrAddTargetRestriction(Resource msg) {
		for (Value res : ds.match(msg, RDFS.SUBCLASSOF, null).objects()) {
			if (ds.contains(res, OWL.ONPROPERTY, MSG.TARGET)) {
				return res;
			}
		}
		Map<Value,Value> restrictions = new LinkedHashMap<Value, Value>();
		for (Value sup : ds.match(msg, RDFS.SUBCLASSOF, null).objects()) {
			if (sup instanceof URI) {
				Value res = getOrAddTargetRestriction((URI) sup);
				if (res != null) {
					restrictions.put(sup, res);
				}
			}
		}
		if (!restrictions.isEmpty()) {
			loop: for (Value sup1 : restrictions.keySet()) {
				for (Value sup2 : restrictions.keySet()) {
					if (sup1 != sup2
							&& ds.contains(sup2, RDFS.SUBCLASSOF, sup1)) {
						continue loop;
					}
				}
				Value res = restrictions.get(sup1);
				ds.add(msg, RDFS.SUBCLASSOF, res);
				return res;
			}
		}
		ValueFactory vf = getValueFactory();
		BNode res = vf.createBNode();
		ds.add(msg, RDFS.SUBCLASSOF, res);
		ds.add(res, RDF.TYPE, OWL.RESTRICTION);
		ds.add(res, OWL.ONPROPERTY, MSG.TARGET);
		ds.add(res, OWL.ALLVALUESFROM, RDFS.RESOURCE);
		ds.add(RDFS.RESOURCE, RDF.TYPE, OWL.CLASS);
		return res;
	}

	private ValueFactory getValueFactory() {
		return ValueFactoryImpl.getInstance();
	}

	private void hasValueFromList() {
		ValueFactory vf = getValueFactory();
		for (Statement st : ds.match(null, OWL.HASVALUE, null)) {
			Resource res = st.getSubject();
			Value obj = st.getObject();
			if (obj instanceof Resource) {
				BNode node = vf.createBNode();
				ds.add(res, OWL.ALLVALUESFROM, node);
				ds.add(node, RDF.TYPE, OWL.CLASS);
				BNode list = vf.createBNode();
				ds.add(node, OWL.ONEOF, list);
				ds.add(list, RDF.TYPE, RDF.LIST);
				ds.add(list, RDF.FIRST, obj);
				ds.add(list, RDF.REST, RDF.NIL);
				for (Value type : ds.match(obj, RDF.TYPE, null).objects()) {
					ds.add(node, RDFS.SUBCLASSOF, type);
				}
				for (Value prop : ds.match(res, OWL.ONPROPERTY, null).objects()) {
					for (Value range : ds.match(prop, RDFS.RANGE, null).objects()) {
						ds.add(node, RDFS.SUBCLASSOF, range);
					}
					for (Resource cls : ds.match(null, RDFS.SUBCLASSOF, res).subjects()) {
						for (Value sup : findSuperClasses(cls)) {
							if (!sup.equals(res) && !ds.match(sup, OWL.ONPROPERTY, prop).isEmpty()) {
								for (Value from : ds.match(sup, OWL.ALLVALUESFROM, null).objects()) {
									ds.add(node, RDFS.SUBCLASSOF, from);
								}
							}
						}
					}
				}
			}
		}
	}

	private void subClassOneOf() {
		for (Statement st : ds.match(null, OWL.ONEOF, null)) {
			Set<Value> common = null;
			for (Value of : new RDFList(ds, st.getObject()).asList()) {
				Set<Value> types = ds.match(of, RDF.TYPE, null).objects();
				if (types.isEmpty()) {
					common = Collections.emptySet();
				} else {
					Set<Value> supers = new HashSet<Value>();
					for (Value type : types) {
						if (type instanceof Resource) {
							supers.addAll(findSuperClasses((Resource) type));
						}
					}
					if (common == null) {
						common = new HashSet<Value>(supers);
					} else {
						common.retainAll(supers);
					}
				}
			}
			if (common != null) {
				for (Value s : common) {
					ds.add(st.getSubject(), RDFS.SUBCLASSOF, s);
					if (OWL.CLASS.equals(s)) {
						ds.add(OWL.CLASS, RDF.TYPE, OWL.CLASS);
					}
				}
			}
		}
	}

	private void subClassIntersectionOf() {
		for (Statement st : ds.match(null, OWL.INTERSECTIONOF, null)) {
			if (st.getObject() instanceof Resource) {
				RDFList list = new RDFList(ds, (Resource) st.getObject());
				for (Value member : list.asList()) {
					ds.add(st.getSubject(), RDFS.SUBCLASSOF, member);
				}
			}
		}
	}

	private void renameAnonymousClasses() {
		for (Resource res : ds.match(null, RDF.TYPE, OWL.CLASS).subjects()) {
			if (res instanceof URI)
				continue;
			// if not already moved
			nameAnonymous(res);
		}
	}

	private URI nameAnonymous(Resource clazz) {
		for (Value eq : ds.match(clazz, OWL.EQUIVALENTCLASS, null).objects()) {
			if (eq instanceof URI) {
				nameClass(clazz, (URI) eq);
				return (URI) eq;
			}
		}
		Resource unionOf = ds.match(clazz, OWL.UNIONOF, null).objectResource();
		if (unionOf != null) {
			return renameClass("", clazz, "Or", new RDFList(ds, unionOf)
					.asList());
		}
		Resource intersectionOf = ds.match(clazz, OWL.INTERSECTIONOF, null)
				.objectResource();
		if (intersectionOf != null) {
			return renameClass("", clazz, "And", new RDFList(ds,
					intersectionOf).asList());
		}
		Resource oneOf = ds.match(clazz, OWL.ONEOF, null).objectResource();
		if (oneOf != null) {
			return renameClass("Is", clazz, "Or", new RDFList(ds, oneOf)
					.asList());
		}
		Resource complement = ds.match(clazz, OWL.COMPLEMENTOF, null)
				.objectResource();
		if (complement != null) {
			URI comp = complement instanceof URI ? (URI) complement : null;
			if (comp == null) {
				comp = nameAnonymous(complement);
				if (comp == null)
					return null;
			}
			String name = "Not" + comp.getLocalName();
			URI uri = new URIImpl(comp.getNamespace() + name);
			nameClass(clazz, uri);
			return uri;
		}
		if (ds.contains(clazz, MSG.MATCHING, null)) {
			return renameClass("", clazz, "Or", ds.match(clazz, MSG.MATCHING, null)
					.objects());
		}
		return null;
	}

	private void distributeEquivalentClasses() {
		for (Statement st : ds.match(null, OWL.EQUIVALENTCLASS, null)) {
			Resource subj = st.getSubject();
			Value equiv = st.getObject();
			for (Value v : ds.match(equiv, OWL.EQUIVALENTCLASS, null).objects()) {
				ds.add(subj, OWL.EQUIVALENTCLASS, v);
			}
			ds.remove(subj, OWL.EQUIVALENTCLASS, subj);
		}
		for (Statement st : ds.match(null, OWL.EQUIVALENTCLASS, null)) {
			Resource subj = st.getSubject();
			Value e = st.getObject();
			if (!(subj instanceof URI))
				continue;
			for (Value d : ds.match(e, OWL.DISJOINTWITH, null).objects()) {
				ds.add(subj, OWL.DISJOINTWITH, d);
			}
			if (ds.contains(e, OWL.INTERSECTIONOF, null)) {
				Resource cinter = ds.match(subj, OWL.INTERSECTIONOF, null)
						.objectResource();
				Resource inter = ds.match(e, OWL.INTERSECTIONOF, null)
						.objectResource();
				if (cinter == null) {
					ds.add(subj, OWL.INTERSECTIONOF, inter);
				} else if (!inter.equals(cinter)) {
					new RDFList(ds, cinter)
							.addAllOthers(new RDFList(ds, inter));
				}
			}
			if (ds.contains(e, OWL.ONEOF, null)) {
				Resource co = ds.match(subj, OWL.ONEOF, null).objectResource();
				Resource eo = ds.match(e, OWL.ONEOF, null).objectResource();
				if (co == null) {
					ds.add(subj, OWL.ONEOF, ds.match(e, OWL.ONEOF, null)
							.objectResource());
				} else if (!eo.equals(co)) {
					new RDFList(ds, co).addAllOthers(new RDFList(ds, eo));
				}
			}
			if (ds.contains(e, OWL.UNIONOF, null)) {
				for (Value elist : ds.match(e, OWL.UNIONOF, null).objects()) {
					if (!ds.contains(subj, OWL.UNIONOF, null)) {
						ds.add(subj, OWL.UNIONOF, elist);
					} else if (!ds.contains(subj, OWL.UNIONOF, elist)) {
						for (Value clist : ds.match(subj, OWL.UNIONOF, null)
								.objects()) {
							new RDFList(ds, (Resource) clist)
									.addAllOthers(new RDFList(ds,
											(Resource) elist));
						}
					}
				}
			}
			if (ds.contains(e, OWL.COMPLEMENTOF, null)) {
				if (!ds.contains(subj, OWL.COMPLEMENTOF, null)) {
					Resource comp = ds.match(e, OWL.COMPLEMENTOF, null)
							.objectResource();
					ds.add(subj, OWL.COMPLEMENTOF, comp);
				}
			}
			if (ds.contains(e, OWL.DISJOINTWITH, null)) {
				for (Value d : ds.match(e, OWL.DISJOINTWITH, null).objects()) {
					ds.add(subj, OWL.DISJOINTWITH, d);
				}
			}
			if (ds.contains(e, RDFS.SUBCLASSOF, null)) {
				for (Value d : ds.match(e, RDFS.SUBCLASSOF, null).objects()) {
					ds.add(subj, RDFS.SUBCLASSOF, d);
				}
			}
			if (ds.contains(e, RDF.TYPE, OWL.RESTRICTION)) {
				ds.add(subj, RDFS.SUBCLASSOF, e);
			}
		}
	}

	private void mergeUnionClasses() {
		for (Resource subj : ds.match(null, RDF.TYPE, OWL.CLASS).subjects()) {
			List<Value> unionOf = new ArrayList<Value>();
			for (Value obj : ds.match(subj, OWL.UNIONOF, null).objects()) {
				if (obj instanceof Resource) {
					List<? extends Value> list = new RDFList(ds,
							(Resource) obj).asList();
					list.removeAll(unionOf);
					unionOf.addAll(list);
				}
			}
			if (!unionOf.isEmpty()) {
				Set<URI> common = findCommonSupers(unionOf);
				if (common.contains(subj)) {
					// if union contains itself then remove it
					ds.remove(subj, OWL.UNIONOF, null);
					continue;
				} else if (findCommon(common, unionOf) != null) {
					// if union includes the common super class then fold
					// together
					URI sup = findCommon(common, unionOf);
					ds.remove(subj, OWL.UNIONOF, null);
					nameClass(subj, sup);
					continue;
				}
				for (URI c : common) {
					ds.add(subj, RDFS.SUBCLASSOF, c);
				}
				for (Value ofValue : unionOf) {
					if (ds.contains(ofValue, RDF.TYPE, RDFS.DATATYPE)
							&& ofValue instanceof URI) {
						// don't use anonymous class for datatypes
						nameClass(subj, (URI) ofValue);
					} else {
						ds.add((Resource) ofValue, RDFS.SUBCLASSOF, subj);
					}
				}
			}
		}
	}

	private URI findCommon(Set<URI> common, Collection<? extends Value> unionOf) {
		URI result = null;
		for (Value e : unionOf) {
			if (common.contains(e)) {
				result = (URI) e;
			}
		}
		return result;
	}

	private Set<URI> findCommonSupers(List<? extends Value> unionOf) {
		Set<? extends Value> common = null;
		for (Value of : unionOf) {
			if (of instanceof Resource) {
				Set<Value> supers = findSuperClasses((Resource) of);
				if (common == null) {
					common = new HashSet<Value>(supers);
				} else {
					common.retainAll(supers);
				}
			}
		}
		if (common == null)
			return Collections.emptySet();
		Iterator<? extends Value> iter = common.iterator();
		while (iter.hasNext()) {
			if (!(iter.next() instanceof URI)) {
				iter.remove();
			}
		}
		return (Set<URI>) common;
	}

	private Set<Value> findSuperClasses(Resource of) {
		HashSet<Value> set = new HashSet<Value>();
		set.add(of);
		return findSuperClasses(of, set);
	}

	private Set<Value> findSuperClasses(Resource of, Set<Value> supers) {
		Set<Value> parent = ds.match(of, RDFS.SUBCLASSOF, null).objects();
		if (supers.addAll(parent)) {
			for (Value s : parent) {
				if (s instanceof Resource) {
					findSuperClasses((Resource) s, supers);
				}
			}
		}
		return supers;
	}

	private URI renameClass(String prefix, Resource clazz, String and,
			Collection<? extends Value> list) {
		String namespace = null;
		Set<String> names = new TreeSet<String>();
		Set<String> others = new TreeSet<String>();
		for (Value of : list) {
			URI uri = null;
			if (of instanceof URI) {
				uri = (URI) of;
			} else if (of instanceof Literal) {
				String label = of.stringValue();
				StringBuilder sb = new StringBuilder();
				if (!label.contains(":")) {
					sb.append(getMatchNamespace(clazz));
				}
				if (label.startsWith("*")) {
					sb.append(label.replace("*", "Star"));
				} else if (label.endsWith("*")) {
					sb.append(label, 0, label.length() - 1);
				} else {
					sb.append(label);
				}
				if (label.startsWith("/")) {
					sb.append("Path");
				}
				if (label.endsWith("*")) {
					sb.append("Prefix");
				} else if (label.startsWith("*")) {
					sb.append("Suffix");
				}
				uri = new URIImpl(sb.toString());
			} else if (ds.contains(of, RDF.TYPE, OWL.CLASS)) {
				uri = nameAnonymous((Resource) of);
			}
			if (uri != null && (namespace == null || commonNS.contains(namespace))) {
				namespace = uri.getNamespace();
			}
			if (uri == null) {
				others.add(of.stringValue());
			} else if (uri.getLocalName().length() > 0) {
				names.add(uri.getLocalName());
			} else {
				String str = uri.stringValue();
				Matcher m = Pattern.compile("\\b[a-zA-Z]\\w*\\b").matcher(str);
				while (m.find()) {
					str = m.group();
				}
				names.add(str);
			}
		}
		if (names.isEmpty())
			return null;
		StringBuilder sb = new StringBuilder();
		sb.append(prefix);
		for (String localPart : names) {
			sb.append(initcap(localPart));
			sb.append(and);
		}
		for (String localPart : others) {
			sb.append(initcap(localPart));
			sb.append(and);
		}
		sb.setLength(sb.length() - and.length());
		URIImpl dest = new URIImpl(namespace + sb.toString());
		nameClass(clazz, dest);
		return dest;
	}

	private CharSequence getMatchNamespace(Resource clazz) {
		for (Resource graph : ds.match(clazz, null, null).contexts()) {
			if (graph instanceof URI) {
				return getMatchNamespace(graph);
			}
		}
		// this shouldn't happen, but just in case
		return "urn:matches:";
	}

	private CharSequence getMatchNamespace(URI ontology) {
		StringBuilder sb = new StringBuilder();
		ParsedURI parsed = new ParsedURI(ontology.stringValue());
		if (parsed.getScheme() != null) {
			sb.append(parsed.getScheme());
			sb.append(':');
		}
		if (parsed.isOpaque()) {
			if (parsed.getSchemeSpecificPart() != null) {
				sb.append(parsed.getSchemeSpecificPart());
			}
		} else {
			if (parsed.getAuthority() != null) {
				sb.append("//");
				sb.append(parsed.getAuthority());
			}
			sb.append(parsed.getPath());
			sb.append("#");
		}
		return sb;
	}

	private void nameClass(Resource orig, URI dest) {
		if (ds.contains(dest, RDF.TYPE, OWL.CLASS)) {
			logger.debug("merging {} {}", orig, dest);
		} else {
			logger.debug("renaming {} {}", orig, dest);
			ds.add(dest, RDF.TYPE, OWL.CLASS);
			anonymousClasses.add(dest);
		}
		rename(orig, dest);
	}

	private void rename(Resource orig, Resource dest) {
		for (Statement stmt : ds.match(orig, null, null)) {
			ds.add(dest, stmt.getPredicate(), stmt.getObject());
		}
		ds.remove(orig, null, null);
		for (Statement stmt : ds.match(null, null, orig)) {
			ds.add(stmt.getSubject(), stmt.getPredicate(), dest);
		}
		ds.remove((Resource) null, null, orig);
	}

	private String initcap(String str) {
		if (str.length() < 2)
			return str.toUpperCase();
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
}
