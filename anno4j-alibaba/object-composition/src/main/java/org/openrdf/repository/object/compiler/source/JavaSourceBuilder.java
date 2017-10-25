/*
 * Copyright (c) 2008, James Leigh All rights reserved.
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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.openrdf.model.URI;

/**
 * Common Java source commands, including annotations.
 * 
 * @author James Leigh
 *
 */
public class JavaSourceBuilder {
	private static Collection<String> keywords = Arrays.asList("abstract",
			"continue", "for", "new", "switch", "assert", "default", "goto",
			"package", "synchronized", "boolean", "do", "if", "private",
			"this", "break", "double", "implements", "protected", "throw",
			"byte", "else", "import", "public", "throws", "case", "enum",
			"instanceof", "return", "transient", "catch", "extends", "int",
			"short", "try", "char", "final", "interface", "static", "void",
			"class", "finally", "long", "strictfp", "volatile", "const",
			"float", "native", "super", "while", "true", "false", "null");
	protected Map<String, String> imports;
	protected StringBuilder sb;
	protected String indent = "";

	protected void setImports(Map<String, String> imports) {
		assert imports != null;
		this.imports = imports;
	}

	protected void setStringBuilder(StringBuilder sb) {
		this.sb = sb;
	}

	protected String getindent() {
		return indent;
	}

	protected void setIndent(String indent) {
		this.indent = indent;
	}

	public String imports(Class<?> klass) {
		return imports(klass.getName());
	}

	public String imports(String klass) {
		String name = klass.trim();
		if (name.contains("<"))
			return importsGeneric(name);
		if (name.endsWith("[]"))
			return imports(name.substring(0, name.length() - 2)) + "[]";
		if (name.indexOf('.') < 0)
			return name;
		imports.put(name.substring(0, name.indexOf('.')), null);
		int idx = name.lastIndexOf('.');
		String sn = name.substring(idx + 1);
		if (!imports.containsKey(sn)) {
			imports.put(sn, name);
			return sn;
		}
		if (name.equals(imports.get(sn)))
			return sn;
		return name;
	}

	public JavaCommentBuilder comment(String comment) {
		begin();
		return new JavaCommentBuilder(sb, indent, comment);
	}

	public JavaSourceBuilder annotate(Class<?> ann) {
		begin();
		sb.append(indent).append("@");
		sb.append(imports(ann.getName())).append("\n");
		return this;
	}

	public JavaSourceBuilder annotateStrings(String ann, String attr,
			Collection<String> values) {
		if (values == null || values.isEmpty())
			return this;
		begin();
		sb.append(indent).append("@").append(imports(ann));
		sb.append("(");
		if (attr != null && !"value".equals(attr)) {
			sb.append(attr).append("=");
		}
		sb.append("{");
		boolean first = true;
		for (String value : values) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			appendString(sb, value);
		}
		sb.append("})\n");
		return this;
	}

	public JavaSourceBuilder annotateString(String ann, String attr, String value) {
		if (value == null)
			return this;
		begin();
		sb.append(indent).append("@").append(imports(ann));
		sb.append("(");
		if (attr != null && !"value".equals(attr)) {
			sb.append(attr).append("=");
		}
		appendString(sb, value);
		sb.append(")\n");
		return this;
	}

	public JavaSourceBuilder annotateURI(Class<?> ann, String attr, URI value) {
		if (value == null)
			return this;
		begin();
		sb.append(indent).append("@").append(imports(ann));
		sb.append("(");
		if (attr != null && !"value".equals(attr)) {
			sb.append(attr).append("=");
		}
		appendString(sb, value);
		sb.append(")\n");
		return this;
	}

	public JavaSourceBuilder annotateURIs(Class<?> ann, String attr, List<URI> values) {
		if (values.isEmpty())
			return this;
		begin();
		sb.append(indent).append("@").append(imports(ann));
		sb.append("(");
		if (attr != null && !"value".equals(attr)) {
			sb.append(attr).append("=");
		}
		sb.append("{");
		for (int i = 0, n = values.size(); i < n; i++) {
			if (i > 0) {
				sb.append(", ");
			}
			appendString(sb, values.get(i));
		}
		sb.append("})\n");
		return this;
	}

	public JavaSourceBuilder annotateClasses(String ann, String attr, List<String> values) {
		begin();
		sb.append(indent).append("@").append(imports(ann));
		sb.append("(");
		if (attr != null && !"value".equals(attr)) {
			sb.append(attr).append("=");
		}
		sb.append("{");
		for (int i = 0, n = values.size(); i < n; i++) {
			if (i > 0) {
				sb.append(", ");
			}
			String type = values.get(i);
			sb.append(imports(type)).append(".class");
		}
		sb.append("})\n");
		return this;
	}

	public JavaSourceBuilder annotateClass(String ann, String attr, String value) {
		begin();
		sb.append(indent).append("@").append(imports(ann));
		sb.append("(");
		if (attr != null && !"value".equals(attr)) {
			sb.append(attr).append("=");
		}
		sb.append(imports(value)).append(".class)\n");
		return this;
	}

	public JavaSourceBuilder annotateEnum(Class<?> ann, String attr, Class<?> e, String value) {
		begin();
		sb.append(indent).append("@").append(imports(ann)).append("(");
		if (attr != null && !"value".equals(attr)) {
			sb.append(attr).append("=");
		}
		sb.append(imports(e)).append(".");
		sb.append(value).append(")\n");
		return this;
	}

	public JavaSourceBuilder annotateEnums(Class<?> ann, String attr, Class<?> e,
			String... values) {
		begin();
		sb.append(indent).append("@").append(imports(ann));
		sb.append("(");
		if (attr != null && !"value".equals(attr)) {
			sb.append(attr).append("=");
		}
		sb.append("{");
		for (int i = 0, n = values.length; i < n; i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(imports(e)).append(".");
			sb.append(values[i]);
		}
		sb.append("})\n");
		return this;
	}

	protected String var(String name) {
		if (keywords.contains(name))
			return "_" + name;
		return name;
	}

	protected void begin() {
		// allow subclass to override
	}

	private void appendString(StringBuilder sb, URI value) {
		appendString(sb, value.stringValue());
	}

	private void appendString(StringBuilder sb, String value) {
		String tab = indent + "\t";
		String newline = "\" + \n" + tab + "\"";
		String str = value;
		str = value.replace("\\", "\\\\");
		str = str.replace("\"", "\\\"");
		str = str.replace("\r\n", "\\r\\n" + newline);
		str = str.replace("\n", "\\n" + newline);
		str = str.replace("\r", "\\r" + newline);
		if (str.endsWith(newline)) {
			str = str.substring(0, str.length() - newline.length());
		}
		sb.append("\"");
		sb.append(str);
		sb.append("\"");
	}

	private String importsGeneric(String name) {
		int start = name.indexOf('<');
		int end = name.lastIndexOf('>');
		StringBuilder sb = new StringBuilder();
		sb.append(imports(name.substring(0, start)));
		sb.append('<');
		int idx = start + 1;
		int nested = 0;
		for (int i = start + 1; i < end; i++) {
			switch (name.charAt(i)) {
			case ',':
				if (nested == 0) {
					sb.append(imports(name.substring(idx, i))).append(", ");
					idx = i + 1;
				}
				break;
			case '<':
				nested++;
				break;
			case '>':
				nested--;
				break;
			}
		}
		sb.append(imports(name.substring(idx, end)));
		sb.append('>');
		return sb.toString();
	}
}
