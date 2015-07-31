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
package org.openrdf.repository.object.compiler;

import info.aduna.io.MavenUtil;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.openrdf.model.Model;
import org.openrdf.model.impl.TreeModel;

/**
 * Converts OWL ontologies into Java source code from the command line.
 * 
 * @author James Leigh
 * 
 */
public abstract class Compiler {
	private static final String VERSION = MavenUtil.loadVersion(
			"org.openrdf.alibaba", "alibaba-repository-object", "devel");

	private static final String APP_NAME = "OpenRDF Alibaba owl-compiler";

	private static final Options options = new Options();
	static {
		Option jar = new Option("j", "jar", true,
				"filename where the jar will be saved");
		jar.setArgName("jar file");
		Option imports = new Option("i", "import", true,
				"jar file that should be imported before compiling");
		imports.setArgName("included jar file");
		Option prefix = new Option("p", "prefix", true,
				"prefix the property names with namespace prefix");
		prefix.setArgName("prefix");
		prefix.setOptionalArg(true);
		Option follow = new Option("f", "follow", true, "follow imports arg is 'true' or 'false'");
		Option baseClass = new Option("e", "extends", true,
				"super class that all concepts should extend");
		baseClass.setArgName("full class name");
		Option s = new Option("s", "singular", false, "Don't pluralise Set property names");
		Option o = new Option("o", "offline", false, "Don't resolve prefix using a Web service");
		options.addOption("h", "help", false, "Print Help (this message) and exit");
		options.addOption("v", "version", false, "Print version information and exit");
		options.addOption(baseClass);
		options.addOption(prefix);
		options.addOption(jar);
		options.addOption(imports);
		options.addOption(follow);
		options.addOption(s);
		options.addOption(o);
	}

	public static void main(String[] args) throws Exception {
		try {
			CommandLine line = new GnuParser().parse(options, args);
			if (line.hasOption('h')) {
				HelpFormatter formatter = new HelpFormatter();
				String cmdLineSyntax = Compiler.class.getSimpleName()
						+ " [options] ontology...";
				String header = "ontology... a list of RDF files that should be compiled together.";
				formatter.printHelp(cmdLineSyntax, header, options, "");
				return;
			}
			if (line.hasOption('v')) {
				System.out.println(APP_NAME + " " + VERSION);
				return;
			}
			File jar;
			if (line.hasOption('j')) {
				jar = new File(line.getOptionValue('j'));
			} else if (line.getArgs().length > 0) {
				String filename = line.getArgs()[line.getArgs().length - 1];
				if (!new File(filename).exists()) {
					filename = new File(new URL(filename).getPath()).getName();
				}
				if (filename.contains(".")) {
					filename = filename.substring(0, filename.lastIndexOf('.'));
				}
				jar = new File(filename + ".jar");
			} else {
				throw new ParseException("Missig -j option");
			}
			List<URL> list = getURLs(line.getOptionValues('i'));
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			cl = new URLClassLoader(list.toArray(new URL[list.size()]), cl);
			boolean follow = true;
			if (line.hasOption('f')) {
				follow = Boolean.parseBoolean(line.getOptionValue('f'));
			}
			List<URL> urls = getURLs(line.getArgs());
			OntologyLoader loader = new OntologyLoader(new TreeModel());
			loader.loadOntologies(urls);
			if (follow) {
				loader.followImports();
			}
			Model model = loader.getModel();
			OWLCompiler converter = new OWLCompiler(cl);
			if (line.hasOption('s')) {
				converter.setPluralForms(false);
			} else {
				converter.setPluralForms(true);
			}
			if (line.hasOption('o')) {
				converter.setResolvingPrefix(false);
			} else {
				converter.setResolvingPrefix(true);
			}
			converter.setModel(model);
			if (line.hasOption('p')) {
				String prefix = line.getOptionValue('p');
				if (prefix == null) {
					prefix = "";
				}
				converter.setMemberPrefix(prefix);
			}
			if (line.hasOption('e')) {
				converter.setBaseClasses(line.getOptionValues('e'));
			}
			urls.addAll(loader.getImported());
			converter.setOntologies(loader.getImportedFormats());
			converter.setPrefixNamespaces(loader.getNamespaces().values());
			converter.createJar(jar);
			if (isJarEmpty(jar))
				System.err.println("No classes found - Try a different namespace.");
			return;
		} catch (ParseException exp) {
			System.err.println(exp.getMessage());
			System.exit(1);
		}
	}

	private static boolean isJarEmpty(File jar) throws ZipException, IOException {
		if (jar == null || !jar.canRead())
			return true;
		ZipFile zip = new ZipFile(jar);
		try {
			return zip.size() <= 0;
		} finally {
			zip.close();
		}
	}

	private static List<URL> getURLs(String[] args)
			throws MalformedURLException {
		List<URL> list = new ArrayList<URL>();
		if (args == null)
			return list;
		for (String arg : args) {
			File file = new File(arg);
			if (file.exists()) {
				list.add(file.toURI().toURL());
			} else {
				list.add(new URL(arg));
			}
		}
		return list;
	}

}