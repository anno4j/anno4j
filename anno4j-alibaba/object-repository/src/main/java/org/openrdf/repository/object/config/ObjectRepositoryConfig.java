/*
 * Copyright (c) 2007-2009, James Leigh All rights reserved.
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

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static org.openrdf.repository.object.config.ObjectRepositorySchema.BEHAVIOUR;
import static org.openrdf.repository.object.config.ObjectRepositorySchema.BEHAVIOUR_JAR;
import static org.openrdf.repository.object.config.ObjectRepositorySchema.BLOB_STORE;
import static org.openrdf.repository.object.config.ObjectRepositorySchema.BLOB_STORE_PARAMETER;
import static org.openrdf.repository.object.config.ObjectRepositorySchema.CONCEPT;
import static org.openrdf.repository.object.config.ObjectRepositorySchema.CONCEPT_JAR;
import static org.openrdf.repository.object.config.ObjectRepositorySchema.DATATYPE;
import static org.openrdf.repository.object.config.ObjectRepositorySchema.KNOWN_AS;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Graph;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.GraphImpl;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.util.ModelException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.contextaware.config.ContextAwareConfig;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.exceptions.ObjectStoreConfigException;

/**
 * Defines the Scope of an {@link ObjectRepository} and its factory. This
 * includes roles, literals, factories, datasets, and contexts.
 * 
 * @author James Leigh
 * 
 */
public class ObjectRepositoryConfig extends ContextAwareConfig implements
		Cloneable {
	private static final String JAVA_NS = "java:";
	private ValueFactory vf = ValueFactoryImpl.getInstance();
	private ClassLoader cl;
	private Map<Class<?>, List<URI>> datatypes = new HashMap<Class<?>, List<URI>>();
	private Map<Method, List<URI>> annotations = new HashMap<Method, List<URI>>();
	private Map<Class<?>, List<URI>> concepts = new HashMap<Class<?>, List<URI>>();
	private Map<Class<?>, List<URI>> behaviours = new HashMap<Class<?>, List<URI>>();
	private List<URL> conceptJars = new ArrayList<URL>();
	private List<URL> behaviourJars = new ArrayList<URL>();
	private Value blobStore;
	private Set<Value> blobStoreParameters = new HashSet<Value>();

	public ObjectRepositoryConfig() {
		super();
		cl = Thread.currentThread().getContextClassLoader();
		if (cl == null) {
			cl = getClass().getClassLoader();
		}
	}

	public ObjectRepositoryConfig(ClassLoader cl) {
		this.cl = cl;
	}

	public void setClassLoader(ClassLoader cl) {
		this.cl = cl;
	}

	public ClassLoader getClassLoader() {
		return cl;
	}

	public Map<Class<?>, List<URI>> getDatatypes() {
		return unmodifiableMap(datatypes);
	}

	/**
	 * Associates this class with the given datatype.
	 * 
	 * @param type
	 *            serializable class
	 * @param datatype
	 *            URI
	 * @throws ObjectStoreConfigException
	 */
	public void addDatatype(Class<?> type, URI datatype)
			throws ObjectStoreConfigException {
		List<URI> list = datatypes.get(type);
		if (list == null && datatypes.containsKey(type))
			throw new ObjectStoreConfigException(type.getSimpleName()
					+ " can only be added once");
		if (list == null) {
			datatypes.put(type, list = new LinkedList<URI>());
		}
		list.add(datatype);
	}

	/**
	 * Associates this class with the given datatype.
	 * 
	 * @param type
	 *            serializable class
	 * @param datatype
	 *            uri
	 * @throws ObjectStoreConfigException
	 */
	public void addDatatype(Class<?> type, String datatype)
			throws ObjectStoreConfigException {
		addDatatype(type, vf.createURI(datatype));
	}

	public Map<Method, List<URI>> getAnnotations() {
		return unmodifiableMap(annotations);
	}

	/**
	 * Associates this annotation with its annotated type.
	 * 
	 * @param ann
	 * @throws ObjectStoreConfigException
	 */
	public void addAnnotation(Class<?> ann) throws ObjectStoreConfigException {
		if (ann.getDeclaredMethods().length != 1)
			throw new ObjectStoreConfigException(
					"Annotation class must have exactly one method: " + ann);
		addAnnotation(ann.getDeclaredMethods()[0]);
	}

	/**
	 * Associates this annotation with the given type.
	 * 
	 * @param ann
	 * @param type
	 * @throws ObjectStoreConfigException
	 */
	public void addAnnotation(Class<?> ann, URI type)
			throws ObjectStoreConfigException {
		if (ann.getDeclaredMethods().length != 1)
			throw new ObjectStoreConfigException(
					"Annotation class must have exactly one method: " + ann);
		addAnnotation(ann.getDeclaredMethods()[0], type);
	}

	/**
	 * Associates this annotation with the given type.
	 * 
	 * @param ann
	 * @param type
	 * @throws ObjectStoreConfigException
	 */
	public void addAnnotation(Class<?> ann, String type)
			throws ObjectStoreConfigException {
		if (ann.getDeclaredMethods().length != 1)
			throw new ObjectStoreConfigException(
					"Annotation class must have exactly one method: " + ann);
		addAnnotation(ann.getDeclaredMethods()[0], type);
	}

	/**
	 * Associates this annotation with its annotated type.
	 * 
	 * @param ann
	 * @throws ObjectStoreConfigException
	 */
	public void addAnnotation(Method ann) throws ObjectStoreConfigException {
		if (annotations.containsKey(ann))
			throw new ObjectStoreConfigException(ann.toString()
					+ " can only be added once");
		annotations.put(ann, null);
	}

	/**
	 * Associates this annotation with the given type.
	 * 
	 * @param ann
	 * @param type
	 * @throws ObjectStoreConfigException
	 */
	public void addAnnotation(Method ann, URI type)
			throws ObjectStoreConfigException {
		List<URI> list = annotations.get(ann);
		if (list == null && annotations.containsKey(ann))
			throw new ObjectStoreConfigException(ann.toString()
					+ " can only be added once");
		if (list == null) {
			annotations.put(ann, list = new LinkedList<URI>());
		}
		list.add(type);
	}

	/**
	 * Associates this annotation with the given type.
	 * 
	 * @param ann
	 * @param type
	 * @throws ObjectStoreConfigException
	 */
	public void addAnnotation(Method ann, String type)
			throws ObjectStoreConfigException {
		addAnnotation(ann, vf.createURI(type));
	}

	public Map<Class<?>, List<URI>> getConcepts() {
		return unmodifiableMap(concepts);
	}

	/**
	 * Associates this concept with its annotated type.
	 * 
	 * @param concept
	 *            interface or class
	 * @throws ObjectStoreConfigException
	 */
	public void addConcept(Class<?> concept) throws ObjectStoreConfigException {
		if (concepts.containsKey(concept))
			throw new ObjectStoreConfigException(concept.getSimpleName()
					+ " can only be added once");
		concepts.put(concept, null);
	}

	/**
	 * Associates this concept with the given type.
	 * 
	 * @param concept
	 *            interface or class
	 * @param type
	 *            URI
	 * @throws ObjectStoreConfigException
	 */
	public void addConcept(Class<?> concept, URI type)
			throws ObjectStoreConfigException {
		List<URI> list = concepts.get(concept);
		if (list == null && concepts.containsKey(concept))
			throw new ObjectStoreConfigException(concept.getSimpleName()
					+ " can only be added once");
		if (list == null) {
			concepts.put(concept, list = new LinkedList<URI>());
		}
		list.add(type);
	}

	/**
	 * Associates this concept with the given type.
	 * 
	 * @param concept
	 *            interface or class
	 * @param type
	 *            uri
	 * @throws ObjectStoreConfigException
	 */
	public void addConcept(Class<?> concept, String type)
			throws ObjectStoreConfigException {
		addConcept(concept, vf.createURI(type));
	}

	public Map<Class<?>, List<URI>> getBehaviours() {
		return unmodifiableMap(behaviours);
	}

	/**
	 * Associates this behaviour with its implemented type.
	 * 
	 * @param behaviour
	 *            class
	 * @throws ObjectStoreConfigException
	 */
	public void addBehaviour(Class<?> behaviour)
			throws ObjectStoreConfigException {
		if (behaviours.containsKey(behaviour))
			throw new ObjectStoreConfigException(behaviour.getSimpleName()
					+ " can only be added once");
		try {
			behaviour.getConstructor();
		} catch (NoSuchMethodException e) {
			throw new ObjectStoreConfigException(behaviour.getSimpleName()
					+ " must have a default constructor");
		}
		behaviours.put(behaviour, null);
	}

	/**
	 * Associates this behaviour with the given type.
	 * 
	 * @param behaviour
	 *            class
	 * @param type
	 *            URI
	 * @throws ObjectStoreConfigException
	 */
	public void addBehaviour(Class<?> behaviour, URI type)
			throws ObjectStoreConfigException {
		List<URI> list = behaviours.get(behaviour);
		if (list == null && behaviours.containsKey(behaviour))
			throw new ObjectStoreConfigException(behaviour.getSimpleName()
					+ " can only be added once");
		if (list == null) {
			behaviours.put(behaviour, list = new LinkedList<URI>());
		}
		list.add(type);
	}

	/**
	 * Associates this behaviour with the given type.
	 * 
	 * @param behaviour
	 *            class
	 * @param type
	 *            uri
	 * @throws ObjectStoreConfigException
	 */
	public void addBehaviour(Class<?> behaviour, String type)
			throws ObjectStoreConfigException {
		addBehaviour(behaviour, vf.createURI(type));
	}

	public List<URL> getConceptJars() {
		return unmodifiableList(conceptJars);
	}

	public void addConceptJar(URL jarFile) {
		conceptJars.add(jarFile);
	}

	public List<URL> getBehaviourJars() {
		return unmodifiableList(behaviourJars);
	}

	public void addBehaviourJar(URL jarFile) {
		behaviourJars.add(jarFile);
	}

	public String getBlobStore() {
		if (blobStore == null)
			return null;
		return blobStore.stringValue();
	}

	public void setBlobStore(String blobStore) {
		this.blobStore = vf.createLiteral(blobStore);
	}

	public Map<String, String> getBlobStoreParameters() {
		Map<String, String> result = new HashMap<String, String>();
		for (Value v : blobStoreParameters) {
			String[] split = v.stringValue().split(":", 2);
			result.put(split[0], split[1]);
		}
		return result;
	}

	public void setBlobStoreParameters(Map<String, String> blobStoreParameters) {
		this.blobStoreParameters.clear();
		for (Map.Entry<String, String> e : blobStoreParameters.entrySet()) {
			String label = e.getKey() + ":" + e.getValue();
			this.blobStoreParameters.add(vf.createLiteral(label));
		}
	}

	public ObjectRepositoryConfig clone() {
		try {
			Object o = super.clone();
			ObjectRepositoryConfig clone = (ObjectRepositoryConfig) o;
			clone.setReadContexts(copy(clone.getReadContexts()));
			clone.setAddContexts(copy(clone.getAddContexts()));
			clone.setRemoveContexts(copy(clone.getRemoveContexts()));
			clone.setArchiveContexts(copy(clone.getArchiveContexts()));
			clone.datatypes = copy(datatypes);
			clone.concepts = copy(concepts);
			clone.behaviours = copy(behaviours);
			clone.conceptJars = new ArrayList<URL>(conceptJars);
			clone.behaviourJars = new ArrayList<URL>(behaviourJars);
			clone.blobStoreParameters = new HashSet<Value>(blobStoreParameters);
			Graph model = new GraphImpl();
			Resource subj = clone.export(model);
			clone.parse(model, subj);
			return clone;
		} catch (RepositoryConfigException e) {
			throw new AssertionError(e);
		} catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	public Resource export(Graph model) {
		ValueFactory vf = ValueFactoryImpl.getInstance();
		Resource subj = super.export(model);
		exportAssocation(subj, datatypes, DATATYPE, model);
		exportAssocation(subj, concepts, CONCEPT, model);
		exportAssocation(subj, behaviours, BEHAVIOUR, model);
		for (URL jar : conceptJars) {
			model.add(subj, CONCEPT_JAR, vf.createURI(jar.toExternalForm()));
		}
		for (URL jar : behaviourJars) {
			model.add(subj, BEHAVIOUR_JAR, vf.createURI(jar.toExternalForm()));
		}
		if (blobStore != null) {
			model.add(subj, BLOB_STORE, blobStore);
		}
		for (Value v : blobStoreParameters) {
			model.add(subj, BLOB_STORE_PARAMETER, v);
		}
		return subj;
	}

	@Override
	public void parse(Graph graph, Resource subj)
			throws RepositoryConfigException {
		super.parse(graph, subj);
		try {
			Model model = new LinkedHashModel(graph);
			parseAssocation(subj, datatypes, DATATYPE, model);
			parseAssocation(subj, concepts, CONCEPT, model);
			parseAssocation(subj, behaviours, BEHAVIOUR, model);
			conceptJars.clear();
			for (Value obj : model.filter(subj, CONCEPT_JAR, null).objects()) {
				conceptJars.add(new URL(obj.stringValue()));
			}
			behaviourJars.clear();
			for (Value obj : model.filter(subj, BEHAVIOUR_JAR, null).objects()) {
				behaviourJars.add(new URL(obj.stringValue()));
			}
			blobStore = model.filter(subj, BLOB_STORE, null).objectValue();
			blobStoreParameters.clear();
			blobStoreParameters.addAll(model.filter(subj, BLOB_STORE_PARAMETER, null).objects());
		} catch (MalformedURLException e) {
			throw new ObjectStoreConfigException(e);
		} catch (ModelException e) {
			throw new ObjectStoreConfigException(e);
		}
	}

	private Map<Class<?>, List<URI>> copy(Map<Class<?>, List<URI>> map) {
		Map<Class<?>, List<URI>> result = new HashMap<Class<?>, List<URI>>();
		for (Map.Entry<Class<?>, List<URI>> e : map.entrySet()) {
			if (e.getValue() == null) {
				result.put(e.getKey(), null);
			} else {
				result.put(e.getKey(), new LinkedList<URI>(e.getValue()));
			}
		}
		return result;
	}

	private URI[] copy(URI[] ar) {
		URI[] result = new URI[ar.length];
		System.arraycopy(ar, 0, result, 0, ar.length);
		return result;
	}

	private void exportAssocation(Resource subj, Map<Class<?>, List<URI>> assocation,
			URI relation, Graph model) {
		ValueFactory vf = ValueFactoryImpl.getInstance();
		for (Map.Entry<Class<?>, List<URI>> e : assocation.entrySet()) {
			URI name = vf.createURI(JAVA_NS, e.getKey().getName());
			model.add(subj, relation, name);
			if (e.getValue() != null) {
				for (URI value : e.getValue()) {
					model.add(name, KNOWN_AS, value);
				}
			}
		}
	}

	private void parseAssocation(Resource subj, Map<Class<?>, List<URI>> assocation,
			URI relation, Model model) throws ObjectStoreConfigException {
		assocation.clear();
		for (Value obj : model.filter(subj, relation, null).objects()) {
			Class<?> role = loadClass(obj);
			Set<Value> objects = model.filter((Resource) obj, KNOWN_AS, null)
					.objects();
			if (objects.isEmpty()) {
				assocation.put(role, null);
			}
			for (Value uri : objects) {
				List<URI> list = assocation.get(role);
				if (list == null) {
					assocation.put(role, list = new LinkedList<URI>());
				}
				list.add((URI) uri);
			}
		}
	}

	private Class<?> loadClass(Value base) throws ObjectStoreConfigException {
		if (base instanceof URI) {
			URI uri = (URI) base;
			if (JAVA_NS.equals(uri.getNamespace())) {
				String name = uri.getLocalName();
				try {
					synchronized (cl) {
						return Class.forName(name, true, cl);
					}
				} catch (ClassNotFoundException e) {
					throw new ObjectStoreConfigException(e);
				}
			}
		}
		throw new ObjectStoreConfigException("Invalid java URI: " + base);
	}
}
