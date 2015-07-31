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

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.config.RepositoryFactory;
import org.openrdf.repository.config.RepositoryImplConfig;
import org.openrdf.repository.contextaware.config.ContextAwareFactory;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.ObjectServiceImpl;
import org.openrdf.repository.object.behaviours.RDFObjectImpl;
import org.openrdf.repository.object.exceptions.ObjectStoreConfigException;
import org.openrdf.repository.object.managers.LiteralManager;
import org.openrdf.repository.object.managers.RoleMapper;
import org.openrdf.repository.object.managers.helpers.RoleClassLoader;

/**
 * Creates {@link ObjectRepository} from any {@link Repository}.
 * 
 * @author James Leigh
 *
 */
public class ObjectRepositoryFactory extends ContextAwareFactory {

	/**
	 * The type of repositories that are created by this factory.
	 * 
	 * @see RepositoryFactory#getRepositoryType()
	 */
	public static final String REPOSITORY_TYPE = "openrdf:ObjectRepository";

	@Override
	public String getRepositoryType() {
		return REPOSITORY_TYPE;
	}

	/**
	 * Creates a new ObjectRepositoryConfig instance.
	 */
	@Override
	public ObjectRepositoryConfig getConfig() {
		return new ObjectRepositoryConfig();
	}

	/**
	 * Wrap a previously initialised repository in an ObjectRepository.
	 */
	public ObjectRepository createRepository(ObjectRepositoryConfig config,
			Repository delegate) throws RepositoryConfigException,
			RepositoryException {
		ObjectRepository repo = getRepository(config, delegate.getValueFactory());
		repo.setDelegate(delegate);
		return repo;
	}

	/**
	 * Wrap a previously initialised repository in an ObjectRepository.
	 */
	public ObjectRepository createRepository(Repository delegate)
			throws RepositoryConfigException, RepositoryException {
		return createRepository(getConfig(), delegate);
	}

	/**
	 * Create an uninitialised ObjectRepository without a delegate.
	 */
	@Override
	public ObjectRepository getRepository(RepositoryImplConfig configuration)
			throws RepositoryConfigException {
		if (!(configuration instanceof ObjectRepositoryConfig))
			throw new RepositoryConfigException("Invalid configuration class: "
					+ configuration.getClass());
		ObjectRepositoryConfig config = (ObjectRepositoryConfig) configuration;
		return getRepository(config, ValueFactoryImpl.getInstance());
	}

	protected LiteralManager createLiteralManager(ValueFactory uf,
			ValueFactory lf) {
		return new LiteralManager(uf, lf);
	}

	protected RoleMapper createRoleMapper(ValueFactory vf)
			throws ObjectStoreConfigException {
		return new RoleMapper(vf);
	}

	protected ObjectRepository createObjectRepository(RoleMapper mapper,
			LiteralManager literals, ClassLoader cl) throws ObjectStoreConfigException {
		return new ObjectRepository(new ObjectServiceImpl(mapper, literals, cl));
	}

	private ObjectRepository getRepository(ObjectRepositoryConfig config,
			ValueFactory vf) throws ObjectStoreConfigException {
		ObjectRepository repo = getObjectRepository(config, vf);

		repo.setIncludeInferred(config.isIncludeInferred());
		repo.setMaxQueryTime(config.getMaxQueryTime());
		repo.setQueryLanguage(config.getQueryLanguage());
		repo.setReadContexts(config.getReadContexts());
		repo.setAddContexts(config.getAddContexts());
		repo.setInsertContext(config.getInsertContext());
		repo.setRemoveContexts(config.getRemoveContexts());
		repo.setArchiveContexts(config.getArchiveContexts());
		// repo.setQueryResultLimit(config.getQueryResultLimit());
		return repo;
	}

	private ObjectRepository getObjectRepository(ObjectRepositoryConfig module,
			ValueFactory vf) throws ObjectStoreConfigException {
		ClassLoader cl = getClassLoader(module);
		RoleMapper mapper = getRoleMapper(cl, vf, module);
		LiteralManager literals = getLiteralManager(cl, vf, module);
		ObjectRepository repo = createObjectRepository(mapper, literals, cl);
		repo.setBlobStoreUrl(module.getBlobStore());
		repo.setBlobStoreParameters(module.getBlobStoreParameters());
		return repo;
	}

	private ClassLoader getClassLoader(ObjectRepositoryConfig module) {
		ClassLoader cl = module.getClassLoader();
		List<URL> jars = new ArrayList<URL>();
		jars.addAll(module.getConceptJars());
		jars.addAll(module.getBehaviourJars());
		if (jars.isEmpty())
			return cl;
		URL[] array = jars.toArray(new URL[jars.size()]);
		return new URLClassLoader(array, cl);
	}

	private RoleMapper getRoleMapper(ClassLoader cl, ValueFactory uf,
			ObjectRepositoryConfig module) throws ObjectStoreConfigException {
		RoleMapper mapper = createRoleMapper(uf);
		mapper.addBehaviour(RDFObjectImpl.class, RDFS.RESOURCE);
		RoleClassLoader loader = new RoleClassLoader(mapper);
		loader.loadRoles(cl);
		if (module.getConceptJars() != null) {
			for (URL url : module.getConceptJars()) {
				loader.scan(url, cl);
			}
		}
		if (module.getBehaviourJars() != null) {
			for (URL url : module.getBehaviourJars()) {
				loader.scan(url, cl);
			}
		}
		for (Map.Entry<Method, List<URI>> e : module.getAnnotations().entrySet()) {
			if (e.getValue() == null) {
				mapper.addAnnotation(e.getKey());
			} else {
				for (URI value : e.getValue()) {
					mapper.addAnnotation(e.getKey(), value);
				}
			}
		}
		for (Map.Entry<Class<?>, List<URI>> e : module.getConcepts().entrySet()) {
			if (e.getValue() == null) {
				mapper.addConcept(e.getKey());
			} else {
				for (URI value : e.getValue()) {
					mapper.addConcept(e.getKey(), value);
				}
			}
		}
		for (Map.Entry<Class<?>, List<URI>> e : module.getBehaviours().entrySet()) {
			if (e.getValue() == null) {
				mapper.addBehaviour(e.getKey());
			} else {
				for (URI value : e.getValue()) {
					mapper.addBehaviour(e.getKey(), value);
				}
			}
		}
		return mapper;
	}

	private LiteralManager getLiteralManager(ClassLoader cl, ValueFactory vf,
			ObjectRepositoryConfig module) {
		LiteralManager literalManager = createLiteralManager(vf, vf);
		literalManager.setClassLoader(cl);
		for (Map.Entry<Class<?>, List<URI>> e : module.getDatatypes().entrySet()) {
			for (URI value : e.getValue()) {
				literalManager.addDatatype(e.getKey(), value);
			}
		}
		return literalManager;
	}

}
