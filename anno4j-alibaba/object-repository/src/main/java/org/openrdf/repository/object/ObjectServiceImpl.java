package org.openrdf.repository.object;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.openrdf.repository.object.composition.ClassResolver;
import org.openrdf.repository.object.exceptions.ObjectStoreConfigException;
import org.openrdf.repository.object.managers.LiteralManager;
import org.openrdf.repository.object.managers.RoleMapper;

public class ObjectServiceImpl implements ObjectService {
	static final Collection<File> temporary = new ArrayList<File>();

	private final LiteralManager literals;
	private final ClassResolver resolver;

	public ObjectServiceImpl() throws ObjectStoreConfigException {
		this(Thread.currentThread().getContextClassLoader());
	}

	public ObjectServiceImpl(ClassLoader cl) throws ObjectStoreConfigException {
		if (cl == null) {
			cl = getClass().getClassLoader();
		}
		this.literals = new LiteralManager(cl);
		resolver = new ClassResolver(cl);
	}

	public ObjectServiceImpl(RoleMapper mapper, LiteralManager literalManager,
			ClassLoader cl) throws ObjectStoreConfigException {
		this.literals = literalManager;
		resolver = new ClassResolver(mapper, cl);
	}

	public ObjectFactory createObjectFactory() {
		return new ObjectFactory(resolver, literals);
	}

}
