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
package org.openrdf.repository.object.composition;

import org.openrdf.model.URI;
import org.openrdf.repository.object.composition.helpers.BehaviourConstructor;
import org.openrdf.repository.object.composition.helpers.BehaviourProviderService;
import org.openrdf.repository.object.composition.helpers.ClassComposer;
import org.openrdf.repository.object.exceptions.ObjectCompositionException;
import org.openrdf.repository.object.exceptions.ObjectStoreConfigException;
import org.openrdf.repository.object.managers.PropertyMapper;
import org.openrdf.repository.object.managers.RoleMapper;
import org.openrdf.repository.object.managers.helpers.DirUtil;
import org.openrdf.repository.object.managers.helpers.RoleClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.lang.reflect.Modifier.isAbstract;

/**
 * Find a proxy class that can be used for a set of rdf:types.
 * 
 * @author James Leigh
 *
 */
public class ClassResolver {
	private static final Set<URI> EMPTY_SET = Collections.emptySet();
	private static final String PKG_PREFIX = "object.proxies._";
	private static final String CLASS_PREFIX = "_EntityProxy";

	private static RoleMapper newRoleMapper(ClassLoader cl) throws ObjectStoreConfigException {
		if (cl == null) {
			return newRoleMapper(ClassResolver.class.getClassLoader());
		} else {
			RoleMapper mapper = new RoleMapper();
			new RoleClassLoader(mapper).loadRoles(cl);
			return mapper;
		}
	}

	private final Logger logger = LoggerFactory.getLogger(ClassResolver.class);
	private final PropertyMapper properties;
	private final ClassFactory cp;
	private final Collection<Class<?>> baseClassRoles;
	private final RoleMapper mapper;
	private final Class<?> blank;
	private final ConcurrentMap<Set<URI>, Class<?>> multiples = new ConcurrentHashMap<Set<URI>, Class<?>>();
	private final BehaviourProviderService behaviourService;

	public ClassResolver() throws ObjectStoreConfigException {
		this(Thread.currentThread().getContextClassLoader());
	}

	public ClassResolver(ClassLoader cl) throws ObjectStoreConfigException {
		this(newRoleMapper(cl), cl == null ? ClassResolver.class.getClassLoader() : cl);
	}

	public ClassResolver(RoleMapper mapper, ClassLoader cl)
			throws ObjectStoreConfigException {
		this(mapper, new PropertyMapper(cl, mapper.isNamedTypePresent()), cl);
	}

	public ClassResolver(RoleMapper mapper, PropertyMapper properties,
			ClassLoader cl) throws ObjectStoreConfigException {
		this.mapper = mapper;
		this.properties = properties;
		try {
			File dir = DirUtil.createTempDir("classes");
			DirUtil.deleteOnExit(dir);
			this.cp = new ClassFactory(dir, cl);
			behaviourService = BehaviourProviderService.newInstance(cp);
			Collection<Class<?>> baseClassRoles = mapper.getConceptClasses();
			this.baseClassRoles = new ArrayList<Class<?>>(baseClassRoles.size());
			for (Class<?> base : baseClassRoles) {
				try {
					// ensure the base class has a default constructor
					base.getConstructor();
					this.baseClassRoles.add(base);
				} catch (NoSuchMethodException e) {
					logger.warn("Concept will only be mergable: {}", base);
				}
			}
			blank = resolveBlankEntity(EMPTY_SET);
		} catch (IOException e) {
			throw new ObjectStoreConfigException(e);
		}
	}

	public RoleMapper getRoleMapper() {
		return mapper;
	}

	public PropertyMapper getPropertyMapper() {
		return properties;
	}

	public ClassLoader getClassLoader() {
		return cp;
	}

	public Class<?> resolveBlankEntity() {
		return blank;
	}

	public Class<?> resolveBlankEntity(Set<URI> types) {
		Class<?> proxy = multiples.get(types);
		if (proxy != null)
			return proxy;
		Collection<Class<?>> roles = new ArrayList<Class<?>>();
		proxy = resolveRoles(mapper.findRoles(types, roles));
		multiples.putIfAbsent(types, proxy);
		return proxy;
	}

	public Class<?> resolveEntity(URI resource) {
		if (resource != null && mapper.isIndividualRolesPresent(resource))
			return resolveIndividualEntity(resource, EMPTY_SET);
		return resolveBlankEntity();
	}

	public Class<?> resolveEntity(URI resource, Set<URI> types) {
		if (resource != null && mapper.isIndividualRolesPresent(resource))
			return resolveIndividualEntity(resource, types);
		return resolveBlankEntity(types);
	}

	private Class<?> resolveIndividualEntity(URI resource, Collection<URI> types) {
		Collection<Class<?>> roles = new ArrayList<Class<?>>();
		roles = mapper.findIndividualRoles(resource, roles);
		roles = mapper.findRoles(types, roles);
		return resolveRoles(roles);
	}

	private Class<?> resolveRoles(Collection<Class<?>> roles) {
		try {
			String className = getJavaClassName(roles);
			return getComposedBehaviours(className, roles);
		} catch (Exception e) {
			List<String> roleNames = new ArrayList<String>();
			for (Class<?> f : roles) {
				roleNames.add(f.getSimpleName());
			}
			throw new ObjectCompositionException(e.toString()
					+ " for entity with roles: " + roleNames, e);
		}
	}

	private Class<?> getComposedBehaviours(String className,
			Collection<Class<?>> roles) throws Exception {
		synchronized (cp) {
			try {
				return cp.classForName(className);
			} catch (ClassNotFoundException e1) {
				return composeBehaviours(className, roles);
			}
		}
	}

	private Class<?> composeBehaviours(String className,
			Collection<Class<?>> roles) throws Exception {
		List<Class<?>> types = new ArrayList<Class<?>>(roles.size());
		types.addAll(roles);
		types = removeSuperClasses(types);
		ClassComposer cc = new ClassComposer(className, types.size());
		cc.setClassFactory(cp);
		Set<Class<?>> behaviours = new LinkedHashSet<Class<?>>(types.size());
		Set<BehaviourConstructor> concretes = new LinkedHashSet<BehaviourConstructor>(types.size());
		Set<Class<?>> bases = new LinkedHashSet<Class<?>>();
		Class<?> baseClass = Object.class;
		for (Class<?> role : types) {
			if (role.isInterface()) {
				cc.addInterface(role);
			} else {
				if (baseClassRoles.contains(role)) {
					if (baseClass != null && baseClass.isAssignableFrom(role)) {
						baseClass = role;
					} else if (!role.equals(baseClass)) {
						baseClass = null;
					}
					bases.add(role);
				} else if (!isAbstract(role.getModifiers())) {
					try {
						concretes.add(new BehaviourConstructor(role));
					} catch (NoSuchMethodException e) {
						// ignore
					}
				}
				behaviours.add(role);
			}
		}
		if (baseClass == null) {
			logger.error("Cannot compose multiple concept classes: " + types);
		} else {
			cc.setBaseClass(baseClass);
		}
		Set<Class<?>> allRoles = new HashSet<Class<?>>(behaviours.size() + cc.getInterfaces().size());
		allRoles.addAll(behaviours);
		allRoles.addAll(cc.getInterfaces());
		PropertyMapper pm = properties;
		cc.addAllBehaviours(concretes);
		cc.addAllBehaviours(behaviourService.findImplementations(pm, allRoles, bases));
		return cc.compose();
	}

	private List<Class<?>> removeSuperClasses(List<Class<?>> classes) {
		for (int i = classes.size() - 1; i >= 0; i--) {
			Class<?> c = classes.get(i);
			for (int j = classes.size() - 1; j >= 0; j--) {
				Class<?> d = classes.get(j);
				if (i != j && c.isAssignableFrom(d)
						&& c.isInterface() == d.isInterface()) {
					classes.remove(i);
					break;
				}
			}
		}
		return classes;
	}

	private String getJavaClassName(Collection<Class<?>> javaClasses) {
		String phex = packagesToHexString(javaClasses);
		String chex = classesToHexString(javaClasses);
		return PKG_PREFIX + phex + "." + CLASS_PREFIX + chex;
	}

	private String packagesToHexString(Collection<Class<?>> javaClasses) {
		TreeSet<String> names = new TreeSet<String>();
		for (Class<?> clazz : javaClasses) {
			if (clazz.getPackage() != null) {
				names.add(clazz.getPackage().getName());
			}
		}
		return toHexString(names);
	}

	private String classesToHexString(Collection<Class<?>> javaClasses) {
		TreeSet<String> names = new TreeSet<String>();
		for (Class<?> clazz : javaClasses) {
			names.add(clazz.getName());
		}
		return toHexString(names);
	}

	private String toHexString(TreeSet<String> names) {
		long hashCode = 0;
		for (String name : names) {
			hashCode = 31 * hashCode + name.hashCode();
		}
		return Long.toHexString(hashCode);
	}

}
