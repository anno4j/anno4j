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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;

import org.openrdf.repository.object.exceptions.ObjectCompositionException;

/**
 * Factory class for creating Class and ClassTemplates.
 * 
 * @author James Leigh
 *
 */
public class ClassFactory extends ClassLoader {

	public static Class<?> classForName(String name, ClassLoader cl)
			throws ClassNotFoundException {
		if (cl == null)
			return Class.forName(name);
		synchronized (cl) {
			return Class.forName(name, true, cl);
		}
	}

	private Reference<ClassPool> cp;
	private File output;
	private List<ClassLoader> alternatives = new ArrayList<ClassLoader>();

	/**
	 * Creates a new Class Factory using the current context class loader.
	 */
	public ClassFactory(File dir) {
		this(dir, Thread.currentThread().getContextClassLoader());
	}

	/**
	 * Create a given Class Factory with the given class loader.
	 * 
	 * @param parent
	 */
	public ClassFactory(File dir, ClassLoader parent) {
		super(parent);
		this.output = dir;
		dir.mkdirs();
	}

	public synchronized Class<?> classForName(String name)
			throws ClassNotFoundException {
		return Class.forName(name, true, this);
	}

	public synchronized Object newInstance(String name) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		return classForName(name).newInstance();
	}

	/**
	 * Create the new Java Class from this template.
	 * 
	 * @param template
	 * @return new Java Class Object
	 * @throws ObjectCompositionException
	 */
	public Class<?> createClass(ClassTemplate template)
			throws ObjectCompositionException {
		CtClass cc = template.getCtClass();
		String name = cc.getName();
		try {
			byte[] bytecode = cc.toBytecode();
			cc.detach();
			return defineClass(name, bytecode);
		} catch (IOException e) {
			throw new ObjectCompositionException(e);
		} catch (CannotCompileException e) {
			throw new ObjectCompositionException(e);
		}
	}

	/**
	 * Create a new Class template, which can later be used to create a Java
	 * class.
	 * 
	 * @param className
	 * @return temporary Class template
	 */
	public ClassTemplate createClassTemplate(String className) {
		ClassPool cp = getClassPool();
		return new ClassTemplate(cp.makeClass(className), this);
	}

	public ClassTemplate loadClassTemplate(Class<?> class1) {
		return new ClassTemplate(get(class1), this);
	}

	/**
	 * Create a new Class template, which can later be used to create a Java
	 * class.
	 * 
	 * @param name
	 * @param class1
	 *            super class
	 * @return temporary Class template
	 */
	public ClassTemplate createClassTemplate(String name, Class<?> class1) {
		ClassPool cp = getClassPool();
		CtClass cc = cp.makeClass(name, get(class1));
		return new ClassTemplate(cc, this);
	}

	@Override
	public URL getResource(String name) {
		try {
			File file = new File(output, name);
			if (file.exists())
				return file.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new AssertionError(e);
		}
		URL url = super.getResource(name);
		if (url != null)
			return url;
		synchronized (alternatives) {
			for (ClassLoader cl : alternatives) {
				url = cl.getResource(name);
				if (url != null)
					return url;
			}
		}
		return null;
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		File file = new File(output, name);
		if (file.exists()) {
			try {
				return new FileInputStream(file);
			} catch (FileNotFoundException e) {
			}
		}
		InputStream stream = getParent().getResourceAsStream(name);
		if (stream != null)
			return stream;
		synchronized (alternatives) {
			for (ClassLoader cl : alternatives) {
				stream = cl.getResourceAsStream(name);
				if (stream != null)
					return stream;
			}
		}
		return null;
	}

	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		Enumeration<URL> resources = super.getResources(name);
		if (resources.hasMoreElements())
			return resources;
		synchronized (alternatives) {
			for (ClassLoader cl : alternatives) {
				resources = cl.getResources(name);
				if (resources.hasMoreElements())
					return resources;
			}
		}
		return resources;
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		try {
			return super.findClass(name);
		} catch (ClassNotFoundException e) {
			synchronized (alternatives) {
				for (ClassLoader cl : alternatives) {
					try {
						return cl.loadClass(name);
					} catch (ClassNotFoundException e1) {
						continue;
					}
				}
			}
			throw e;
		}
	}

	private void appendClassLoader(ClassLoader cl) {
		synchronized (alternatives) {
			alternatives.add(cl);
		}
	}

	CtClass get(Class<?> type) throws ObjectCompositionException {
		ClassPool cp = getClassPool();
		if (type.isPrimitive()) {
			return getPrimitive(type);
		}
		try {
			if (type.isArray())
				return Descriptor.toCtClass(type.getName(), cp);
			return cp.get(type.getName());
		} catch (NotFoundException e) {
			try {
				ClassLoader cl = type.getClassLoader();
				if (cl == null)
					throw new ObjectCompositionException(e);
				appendClassLoader(cl);
				if (type.isArray())
					return Descriptor.toCtClass(type.getName(), cp);
				return cp.get(type.getName());
			} catch (NotFoundException e1) {
				throw new ObjectCompositionException(e);
			}
		}
	}

	private CtClass getPrimitive(Class<?> type) {
		if (type.equals(Boolean.TYPE))
			return CtClass.booleanType;
		if (type.equals(Byte.TYPE))
			return CtClass.byteType;
		if (type.equals(Character.TYPE))
			return CtClass.charType;
		if (type.equals(Double.TYPE))
			return CtClass.doubleType;
		if (type.equals(Float.TYPE))
			return CtClass.floatType;
		if (type.equals(Integer.TYPE))
			return CtClass.intType;
		if (type.equals(Long.TYPE))
			return CtClass.longType;
		if (type.equals(Short.TYPE))
			return CtClass.shortType;
		if (type.equals(Void.TYPE))
			return CtClass.voidType;
		throw new ObjectCompositionException("Unknown primative type: "
				+ type.getName());
	}

	private synchronized ClassPool getClassPool() {
		ClassPool pool = cp == null ? null : cp.get();
		if (pool == null) {
			pool = new ClassPool();
			pool.appendClassPath(new LoaderClassPath(this));
			cp = new SoftReference<ClassPool>(pool);
		}
		return pool;
	}

	private Class<?> defineClass(String name, byte[] bytecode) {
		String resource = name.replace('.', '/') + ".class";
		saveResource(resource, bytecode);
		return defineClass(name, bytecode, 0, bytecode.length);
	}

	private void saveResource(String fileName, byte[] bytecode) {
		try {
			File file = new File(output, fileName);
			file.getParentFile().mkdirs();
			FileOutputStream out = new FileOutputStream(file);
			try {
				out.write(bytecode);
			} finally {
				out.close();
			}
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}

	Class<?> getJavaClass(CtClass cc) throws ClassNotFoundException {
		if (cc.isPrimitive()) {
			if (cc.equals(CtClass.booleanType))
				return Boolean.TYPE;
			if (cc.equals(CtClass.byteType))
				return Byte.TYPE;
			if (cc.equals(CtClass.charType))
				return Character.TYPE;
			if (cc.equals(CtClass.doubleType))
				return Double.TYPE;
			if (cc.equals(CtClass.floatType))
				return Float.TYPE;
			if (cc.equals(CtClass.intType))
				return Integer.TYPE;
			if (cc.equals(CtClass.longType))
				return Long.TYPE;
			if (cc.equals(CtClass.shortType))
				return Short.TYPE;
			throw new AssertionError();
		}
		String name = Descriptor.toJavaName(Descriptor.toJvmName(cc));
		return classForName(name);
	}
}
