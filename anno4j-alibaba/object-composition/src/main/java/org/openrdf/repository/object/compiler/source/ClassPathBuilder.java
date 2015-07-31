/*
 * Copyright (c) 2012 3 Round Stones Inc., Some rights reserved.
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
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Searches the system properties to get the File location of the default classpath.
 */
public class ClassPathBuilder {
	private final Logger logger = LoggerFactory
			.getLogger(ClassPathBuilder.class);
	private final Set<URI> classpath = new LinkedHashSet<URI>();

	public ClassPathBuilder() {
		String classPath = System.getProperty("java.class.path");
		for (String path : classPath.split(File.pathSeparator)) {
			classpath.add(new File(path).toURI());
		}
	}

	public ClassPathBuilder append(ClassLoader cl) {
		appendManifest(cl);
		appendURLClassLoader(cl);
		return this;
	}

	public List<File> toFileList() {
		List<File> list = new ArrayList<File>(classpath.size());
		for (URI uri : classpath) {
			try {
				list.add(new File(uri));
			} catch (IllegalArgumentException e) {
				logger.warn("Not a local class path entry", e);
			}
		}
		return list;
	}

	private void appendManifest(ClassLoader cl) {
		try {
			Enumeration<URL> resources = cl
					.getResources("META-INF/MANIFEST.MF");
			while (resources.hasMoreElements()) {
				URL url = resources.nextElement();
				try {
					if ("jar".equalsIgnoreCase(url.getProtocol())) {
						appendManifest(url, cl);
					}
				} catch (IOException e) {
					logger.warn(e.toString(), e);
				} catch (URISyntaxException e) {
					logger.warn(e.toString(), e);
				}
			}
		} catch (IOException e) {
			logger.error(e.toString(), e);
		}
	}

	private void appendManifest(URL url, ClassLoader cl)
			throws URISyntaxException, IOException {
		String jar = url.getPath();
		if (jar.lastIndexOf('!') > 0) {
			jar = jar.substring(0, jar.lastIndexOf('!'));
		}
		java.net.URI uri = new java.net.URI(jar);
		Manifest manifest = new Manifest();
		InputStream in = url.openStream();
		try {
			manifest.read(in);
		} finally {
			in.close();
		}
		Attributes attributes = manifest.getMainAttributes();
		String dependencies = attributes.getValue("Class-Path");
		if (dependencies == null) {
			dependencies = attributes.getValue("Class-path");
		}
		if (dependencies != null) {
			for (String entry : dependencies.split("\\s+")) {
				if (entry.length() > 0) {
					classpath.add(uri.resolve(entry));
				}
			}
		}
	}

	private void appendURLClassLoader(ClassLoader cl) {
		if (cl instanceof URLClassLoader) {
			for (URL jar : ((URLClassLoader) cl).getURLs()) {
				try {
					classpath.add(jar.toURI());
				} catch (URISyntaxException e) {
					logger.error(e.toString(), e);
				}
			}
		}
		if (cl.getParent() != null) {
			appendURLClassLoader(cl.getParent());
		}
	}

}
