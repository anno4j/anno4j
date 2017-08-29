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

import java.util.Map;

/**
 * Builders getters and setters in Java source code.
 * 
 * @author James Leigh
 *
 */
public class JavaPropertyBuilder extends JavaSourceBuilder {
	private String name;
	private String type;
	private String extype;
	private boolean isInterface;

	public JavaPropertyBuilder(String name, boolean isInterface,
			Map<String, String> imports, StringBuilder sb) {
		this.name = name;
		this.isInterface = isInterface;
		setImports(imports);
		setStringBuilder(sb);
		setIndent("\t");
	}

	public JavaPropertyBuilder type(String type) {
		this.type = this.extype = imports(type);
		return this;
	}

	public JavaPropertyBuilder setOf(String type) {
		this.type = imports("java.util.Set") + "<" + imports(type) + ">";
		if (Object.class.getName().equals(type)) {
			this.extype = imports("java.util.Set") + "<?>";
		} else {
			this.extype = imports("java.util.Set") + "<? extends "
					+ imports(type) + ">";
		}
		return this;
	}

	public void getter() {
		sb.append("\t");
		if (!isInterface) {
			sb.append("public abstract ");
		}
		if ("boolean".equals(type)) {
			sb.append("boolean is");
		} else {
			sb.append(type).append(" get");
		}
		String cap = name.substring(0, 1).toUpperCase();
		sb.append(cap).append(name.substring(1));
		sb.append("();\n");
	}

	public void openSetter() {
		String cap = name.substring(0, 1).toUpperCase();
		sb.append("\t");
		if (!isInterface) {
			sb.append("public abstract ");
		}
		sb.append("void set");
		sb.append(cap).append(name.substring(1));
		sb.append("(");
	}

	public void closeSetter() {
		sb.append(extype);
		sb.append(" ").append(var(name)).append(");\n");
	}

	public void end() {
		sb.append("\n");
	}

}