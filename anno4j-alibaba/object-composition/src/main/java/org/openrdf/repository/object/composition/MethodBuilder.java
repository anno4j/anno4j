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

import static javassist.bytecode.AnnotationsAttribute.visibleTag;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.ClassMemberValue;

import org.openrdf.repository.object.exceptions.ObjectCompositionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used to build the Java method syntax.
 */
public class MethodBuilder extends CodeBuilder {
	private final Logger logger = LoggerFactory.getLogger(MethodBuilder.class);
	private ClassTemplate klass;
	private CtMethod cm;

	protected MethodBuilder(ClassTemplate klass, CtMethod cm) {
		super(klass);
		this.klass = klass;
		this.cm = cm;
		code("{");
	}

	public MethodBuilder ann(Class<?> type, Class<?>... values) {
		MethodInfo info = cm.getMethodInfo();
		ConstPool cp = info.getConstPool();
		ClassMemberValue[] elements = new ClassMemberValue[values.length];
		for (int i = 0; i < values.length; i++) {
			elements[i] = createClassMemberValue((Class<?>) values[i], cp);
		}
		ArrayMemberValue value = new ArrayMemberValue(cp);
		value.setValue(elements);
		AnnotationsAttribute ai = (AnnotationsAttribute) info
				.getAttribute(visibleTag);
		if (ai == null) {
			ai = new AnnotationsAttribute(cp, visibleTag);
			info.addAttribute(ai);
		}
		try {
			Annotation annotation = new Annotation(cp, klass.get(type));
			annotation.addMemberValue("value", value);
			ai.addAnnotation(annotation);
		} catch (NotFoundException e) {
			throw new AssertionError(e);
		}
		return this;
	}

	@Override
	public CodeBuilder end() {
		code("}");
		CtClass cc = cm.getDeclaringClass();
		String body = toString();
		try {
			int mod = cm.getModifiers();
			mod = Modifier.clear(mod, Modifier.ABSTRACT);
			mod = Modifier.clear(mod, Modifier.NATIVE);
			cm.setModifiers(mod);
			cm.setBody(body);
			cc.addMethod(cm);
			if (logger.isTraceEnabled()) {
				logger.trace(
						"public {} {}({}) {{}}",
						new Object[] { cm.getReturnType().getName(),
								cm.getName(), cm.getParameterTypes(), body });
			}
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			try {
				for (CtClass inter : cc.getInterfaces()) {
					sb.append(inter.getSimpleName()).append(" ");
				}
			} catch (NotFoundException e2) {
			}
			String sn = cc.getSimpleName();
			System.err.println(sn + " implements " + sb);
			throw new ObjectCompositionException(e.getMessage() + " for "
					+ body, e);
		}
		clear();
		return this;
	}

}
