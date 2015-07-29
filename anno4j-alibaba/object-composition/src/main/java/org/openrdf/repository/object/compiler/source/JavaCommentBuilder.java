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

/**
 * JavaDoc comment builder.
 * 
 * @author James Leigh
 *
 */
public class JavaCommentBuilder {
	private StringBuilder out;
	private StringBuilder sb;
	private String indent;
	private boolean newline;

	public JavaCommentBuilder(StringBuilder out, String indent, String comment) {
		this.out = out;
		this.sb = new StringBuilder();
		this.indent = indent;
		if (comment != null) {
			if (newline = comment.contains("\n")) {
				sb.append(comment.replace("\n", "\n" + indent + " * "));
			} else {
				sb.append(comment);
			}
		}
	}

	public JavaCommentBuilder seeAlso(String seeAlso) {
		sb.append("\n").append(indent).append(" * @see ").append(seeAlso);
		newline = true;
		return this;
	}

	public JavaCommentBuilder seeAlso(String className, String member) {
		sb.append("\n").append(indent).append(" * @see ").append(className).append("#").append(member);
		newline = true;
		return this;
	}

	public JavaCommentBuilder seeBooleanProperty(String className, String property) {
		return seeAlso(className, "is" + initcap(property));
	}

	public JavaCommentBuilder seeProperty(String className, String property) {
		return seeAlso(className, "get" + initcap(property));

	}

	public JavaCommentBuilder version(String version) {
		sb.append("\n").append(indent).append(" * @version ").append(version);
		newline = true;
		return this;
	}

	public void end() {
		if (sb.length() > 0) {
			out.append(indent);
			out.append("/** ");
			if (newline) {
				out.append("\n").append(indent).append(" * ");
			}
			out.append(sb);
			if (newline) {
				out.append("\n").append(indent);
			}
			out.append(" */\n");
		}
	}

	private String initcap(String name) {
		String cap = name.substring(0,1).toUpperCase();
		return cap + name.substring(1);
	}
}
