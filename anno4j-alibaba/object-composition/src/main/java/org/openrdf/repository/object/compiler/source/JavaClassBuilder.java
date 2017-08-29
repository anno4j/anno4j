/*
 * Copyright (c) 2008-2010, James Leigh and Zepheira LLC Some rights reserved.
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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 * Builds Java source code for a class declaration.
 *  
 * @author James Leigh
 *
 */
public class JavaClassBuilder extends JavaSourceBuilder {
	private PrintWriter out;
	private String pkg;
	private String name;
	private boolean isInterface;
	private boolean extendsPrinted;
	private boolean implementsPrinted;
	private boolean headerPrinted;
	private boolean headerStarted;
	private boolean ended;
	private boolean closeHeader;
	private final Set<String> extended = new HashSet<String>();

	public JavaClassBuilder(PrintWriter out) {
		assert out != null;
		HashMap<String, String> imports = new HashMap<String, String>();
		imports.put("char", null);
		imports.put("byte", null);
		imports.put("short", null);
		imports.put("int", null);
		imports.put("long", null);
		imports.put("float", null);
		imports.put("double", null);
		imports.put("boolean", null);
		imports.put("void", null);
		setImports(imports);
		setStringBuilder(new StringBuilder());
		this.out = out;
	}

	public JavaClassBuilder(File file) throws FileNotFoundException {
		this(new PrintWriter(file));
		assert file != null;
	}

	public void close() {
		end();
		out.close();
	}

	public JavaClassBuilder pkg(String pkg) {
		if (sb.length() > 0) {
			out.append(sb);
			sb.setLength(0);
		}
		this.pkg = pkg;
		if (pkg != null) {
			out.print("package ");
			out.print(pkg);
			out.println(";");
			out.println();
		}
		return this;
	}

	public JavaClassBuilder className(String name) {
		this.name = name;
		headerStarted = true;
		if (pkg == null) {
			imports.put(name, name);
		} else {
			imports.put(name, pkg + "." + name);
		}
		sb.append("public class ");
		sb.append(name);
		setIndent(getindent() + "\t");
		return this;
	}

	public JavaClassBuilder abstractName(String name) {
		this.name = name;
		headerStarted = true;
		if (pkg == null) {
			imports.put(name, name);
		} else {
			imports.put(name, pkg + "." + name);
		}
		sb.append("public abstract class ");
		sb.append(name);
		setIndent(getindent() + "\t");
		return this;
	}

	public JavaClassBuilder interfaceName(String name) {
		this.name = name;
		headerStarted = true;
		isInterface = true;
		if (pkg == null) {
			imports.put(name, name);
		} else {
			imports.put(name, pkg + "." + name);
		}
		sb.append("public interface ");
		sb.append(name);
		setIndent(getindent() + "\t");
		return this;
	}

	public JavaClassBuilder annotationName(String name) {
		this.name = name;
		headerStarted = true;
		isInterface = true;
		if (pkg == null) {
			imports.put(name, name);
		} else {
			imports.put(name, pkg + "." + name);
		}
		sb.append("public @interface ");
		sb.append(name);
		closeHeader();
		setIndent(getindent() + "\t");
		return this;
	}

	public JavaClassBuilder extend(String name) {
		if (extended.contains(name))
			return this;
		extended.add(name);
		closeHeader = true;
		if (extendsPrinted) {
			sb.append(", ");
		} else {
			sb.append(" extends ");
			extendsPrinted = true;
		}
		sb.append(imports(name));
		return this;
	}

	public JavaClassBuilder implement(String name) {
		if (isInterface)
			return extend(name);
		if (extended.contains(name))
			return this;
		extended.add(name);
		closeHeader = true;
		if (implementsPrinted) {
			sb.append(", ");
		} else {
			sb.append(" implements ");
			implementsPrinted = true;
		}
		sb.append(imports(name));
		return this;
	}

	@Override
	protected void begin() {
		if (closeHeader) {
			closeHeader();
		}
		super.begin();
	}

	public JavaMethodBuilder staticMethod(String name) {
		closeHeader();
		return new JavaMethodBuilder(name, isInterface, true, false, imports, sb);
	}

	public JavaClassBuilder staticURIField(String name, URI value) {
		closeHeader();
		sb.append("\tpublic static final ").append(imports(URI.class));
		sb.append(" ").append(name).append(" = new ").append(imports(URIImpl.class));
		sb.append("(\"").append(value.stringValue()).append("\");\n");
		return this;
	}

	public JavaClassBuilder staticURIArrayField(String name, Collection<String> names) {
		closeHeader();
		sb.append("\tpublic static final ").append(imports(URI.class));
		sb.append("[] ").append(name).append(" = new ").append(imports(URI.class));
		sb.append("[]{");
		Iterator<String> iter = names.iterator();
		while (iter.hasNext()) {
			sb.append(iter.next());
			if (iter.hasNext()) {
				sb.append(", ");
			}
		}
		sb.append("};\n");
		return this;
	}

	public JavaClassBuilder staticField(String type, String name, String code) {
		closeHeader();
		sb.append("\tprivate static ").append(imports(type));
		sb.append(" ").append(var(name)).append(" = ").append(code).append(";\n");
		return this;
	}

	public JavaClassBuilder field(String type, String name) {
		closeHeader();
		sb.append("\tprivate ").append(imports(type));
		sb.append(" ").append(var(name)).append(";\n");
		return this;
	}

	public JavaMethodBuilder constructor() {
		closeHeader();
		sb.append("\n");
		return new JavaMethodBuilder(name, isInterface, false, false, imports, sb);
	}

	public JavaPropertyBuilder property(String name) {
		closeHeader();
		return new JavaPropertyBuilder(name, isInterface, imports, sb);
	}

	public JavaMethodBuilder method(String name, boolean isAbstract) {
		closeHeader();
		return new JavaMethodBuilder(name, isInterface, false, isAbstract, imports, sb);
	}

	public JavaClassBuilder code(String code) {
		closeHeader();
		sb.append(code);
		return this;
	}

	private void end() {
		if (ended)
			return;
		ended = true;
		boolean importsPrinted = false;
		List<String> values = new ArrayList<String>(imports.values());
		values.removeAll(Collections.singleton(null));
		for (String cn : new TreeSet<String>(values)) {
			if (cn == null)
				continue; // primitive
			int packageEnd = cn.lastIndexOf('.');
			if (packageEnd <= 0) {
				continue;
			}
			String pack = cn.substring(0, packageEnd);
			if (pack.equals(pkg))
				continue;
			out.print("import ");
			out.print(cn);
			out.println(";");
			importsPrinted = true;
		}
		if (importsPrinted) {
			out.println();
		}
		setIndent(getindent().replaceAll("\t$", ""));
		if (headerStarted) {
			closeHeader();
			out.append(sb);
			out.println("}");
		}
		out.flush();
	}

	private void closeHeader() {
		if (!headerPrinted) {
			headerPrinted = true;
			sb.append(" {\n");
		}
	}
}
