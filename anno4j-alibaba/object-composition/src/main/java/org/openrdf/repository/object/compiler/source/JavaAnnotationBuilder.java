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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.object.compiler.JavaNameResolver;
import org.openrdf.repository.object.compiler.model.RDFClass;
import org.openrdf.repository.object.compiler.model.RDFEntity;
import org.openrdf.repository.object.compiler.model.RDFProperty;
import org.openrdf.repository.object.exceptions.ObjectStoreConfigException;
import org.openrdf.repository.object.vocabulary.MSG;

/**
 * Adds methods for comments and annotations.
 * 
 * @author James Leigh
 * 
 */
public class JavaAnnotationBuilder extends JavaClassBuilder {
	private static final URI NOTHING = new URIImpl(OWL.NAMESPACE + "Nothing");
	private static final URI DATARANGE = new URIImpl(OWL.NAMESPACE
			+ "DataRange");
	private static final URI RESOURCE = RDFS.RESOURCE;
	private static final URI LITERAL = RDFS.LITERAL;
	private static final String JAVA_NS = "java:";
	protected JavaNameResolver resolver;
	private final Set<String> properties = new HashSet<String>();

	public JavaAnnotationBuilder(File source, JavaNameResolver resolver)
			throws FileNotFoundException {
		super(source);
		assert resolver != null;
		this.resolver = resolver;
		for (String root : resolver.getRootPackages()) {
			imports.put(root, null);
		}
	}

	public String getMemberPrefix(String ns) {
		return resolver.getMemberPrefix(ns);
	}

	public String getPackageName(URI uri) {
		return resolver.getPackageName(uri);
	}

	public String getSimpleName(URI name) {
		return resolver.getSimpleName(name);
	}

	public URI getType(URI name) {
		return resolver.getType(name);
	}

	public boolean isAnonymous(URI name) {
		return resolver.isAnonymous(name);
	}

	public String getClassName(URI name) throws ObjectStoreConfigException {
		if (JAVA_NS.equals(name.getNamespace()))
			return name.getLocalName();
		return resolver.getClassName(name);
	}

	public void comment(RDFEntity concept) throws ObjectStoreConfigException {
		comment(this, concept);
	}

	public void comment(JavaSourceBuilder out, RDFEntity concept)
			throws ObjectStoreConfigException {
		StringBuilder sb = new StringBuilder();
		for (Value obj : concept.getValues(RDFS.COMMENT)) {
			sb.append(obj.stringValue()).append("\n");
		}
		JavaCommentBuilder comment = out.comment(sb.toString().trim());
		for (Value see : concept.getValues(RDFS.SEEALSO)) {
			Model model = concept.getModel();
			if (see instanceof URI
					&& model.contains((URI) see, RDF.TYPE, OWL.CLASS)) {
				comment.seeAlso(resolver.getClassName((URI) see));
			} else if (see instanceof URI
					&& model.contains((URI) see, RDF.TYPE, RDF.PROPERTY)) {
				RDFProperty property = new RDFProperty(model, (URI) see);
				for (RDFClass domain : property.getRDFClasses(RDFS.DOMAIN)) {
					RDFClass cc = (RDFClass) domain;
					String cn = resolver.getClassName(domain.getURI());
					String name = getPropertyName(cc, property);
					String range = getPropertyClassName(cc, property);
					if ("boolean".equals(range)) {
						comment.seeBooleanProperty(cn, name);
					} else {
						comment.seeProperty(cn, name);
					}
				}
			} else {
				comment.seeAlso(see.stringValue());
			}
		}
		if (concept instanceof RDFEntity) {
			for (Object version : ((RDFEntity) concept)
					.getStrings(OWL.VERSIONINFO)) {
				comment.version(version.toString());
			}
		}
		comment.end();
	}

	public void annotationProperties(RDFEntity entity)
			throws ObjectStoreConfigException {
		annotationProperties(this, entity);
	}

	public void annotationProperties(JavaSourceBuilder out, RDFEntity entity)
			throws ObjectStoreConfigException {
		loop: for (RDFProperty property : entity.getRDFProperties()) {
			URI iri = property.getURI();
			boolean isSubClass = RDFS.SUBCLASSOF.equals(iri);
			boolean compiled = resolver.isCompiledAnnotation(iri);
			if (property.isA(OWL.ANNOTATIONPROPERTY) || compiled) {
				URI uri = resolver.getType(iri);
				String ann = resolver.getClassName(uri);
				String attr = resolver.getAnnotationAttributeName(uri);
				boolean valueOfClass = resolver.isAnnotationOfClasses(uri);
				boolean functional = property.isA(OWL.FUNCTIONALPROPERTY);
				if (compiled && !functional) {
					functional = resolver.isCompiledAnnotationFunctional(iri);
				}
				if (valueOfClass && functional) {
					RDFClass value = entity.getRDFClass(uri);
					if (isSubClass && MSG.MESSAGE.equals(value.getResource()))
						continue;
					String className = resolver.getClassName(value.getURI());
					out.annotateClass(ann, attr, className);
				} else if (valueOfClass) {
					List<String> classNames = new ArrayList<String>();
					for (RDFClass value : entity.getRDFClasses(uri)) {
						if (value.getURI() == null)
							continue loop;
						if (isSubClass && MSG.MESSAGE.equals(value.getResource()))
							continue;
						classNames.add(resolver.getClassName(value.getURI()));
					}
					if (!classNames.isEmpty()) {
						out.annotateClasses(ann, attr, classNames);
					}
				} else if (functional) {
					String value = entity.getString(uri);
					if (isSubClass && MSG.MESSAGE.stringValue().equals(value))
						continue;
					out.annotateString(ann, attr, value);
				} else {
					Collection<String> values = entity.getStrings(uri);
					if (isSubClass) {
						values.remove(MSG.MESSAGE.stringValue());
					}
					out.annotateStrings(ann, attr, values);
				}
			}
		}
	}

	public String getPropertyName(RDFClass code, RDFProperty param) {
		String name = getSuggestedPropertyName(code, param);
		if (properties.add(name))
			return name;
		int count = 1;
		while (properties.contains(name + '_' + count)) {
			count++;
		}
		return name + '_' + count;
	}

	public String getPropertyClassName(RDFClass code, RDFProperty property)
			throws ObjectStoreConfigException {
		RDFClass range = code.getRange(property, code.isFunctional(property));
		String type = getObjectClassName(range);
		if (code.isMinCardinality(property)) {
			type = unwrap(type);
		}
		return type;
	}

	public String getParameterClassName(RDFClass code, RDFProperty property)
			throws ObjectStoreConfigException {
		RDFClass range = code.getRange(property);
		String type = getObjectClassName(range);
		if (code.isMinCardinality(property)) {
			type = unwrap(type);
		}
		return type;
	}

	public String getResponseClassName(RDFClass code, RDFProperty property)
			throws ObjectStoreConfigException {
		RDFClass range = code.getRange(property);
		String type = getObjectClassName(range);
		if (code.isMinCardinality(property)) {
			type = unwrap(type);
		}
		return type;
	}

	protected String getRangeObjectClassName(RDFClass code, RDFProperty property)
			throws ObjectStoreConfigException {
		return getObjectClassName(code.getRange(property, true));
	}

	protected boolean isPrimitiveType(String type) {
		return type.equals("char") || type.equals("byte")
				|| type.equals("short") || type.equals("int")
				|| type.equals("long") || type.equals("float")
				|| type.equals("double") || type.equals("boolean")
				|| type.equals("void");
	}

	private String getSuggestedPropertyName(RDFClass code, RDFProperty param) {
		if (code.isFunctional(param)) {
			return resolver.getSinglePropertyName(param.getURI());
		} else {
			return resolver.getPluralPropertyName(param.getURI());
		}
	}

	private String getObjectClassName(RDFClass rdfClass)
			throws ObjectStoreConfigException {
		if (rdfClass == null)
			return Object.class.getName();
		String type = null;
		if (rdfClass.isA(DATARANGE)) {
			for (Value value : rdfClass.getList(OWL.ONEOF)) {
				URI datatype = ((Literal) value).getDatatype();
				if (datatype == null) {
					type = String.class.getName();
				} else {
					type = resolver.getClassName(datatype);
				}
			}
		} else if (NOTHING.equals(rdfClass.getURI())) {
			return Void.class.getName();
		} else if (LITERAL.equals(rdfClass.getURI())) {
			return Object.class.getName();
		} else if (RESOURCE.equals(rdfClass.getURI())) {
			return Object.class.getName();
		} else if (rdfClass.getURI() != null) {
			type = resolver.getClassName(rdfClass.getURI());
		} else {
			return Object.class.getName();
		}
		return type;
	}

	private String unwrap(String type) {
		if (type.equals("java.lang.Character"))
			return "char";
		if (type.equals("java.lang.Byte"))
			return "byte";
		if (type.equals("java.lang.Short"))
			return "short";
		if (type.equals("java.lang.Integer"))
			return "int";
		if (type.equals("java.lang.Long"))
			return "long";
		if (type.equals("java.lang.Float"))
			return "float";
		if (type.equals("java.lang.Double"))
			return "double";
		if (type.equals("java.lang.Boolean"))
			return "boolean";
		if (type.equals("java.lang.Void"))
			return "void";
		return type;
	}

}
