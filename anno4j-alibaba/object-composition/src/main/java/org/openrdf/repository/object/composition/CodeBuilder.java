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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.CtClass;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.annotation.ClassMemberValue;

import org.openrdf.repository.object.exceptions.ObjectCompositionException;

/**
 * Java code builder that abstracts away from the Java syntax a bit.
 * 
 * @author James Leigh
 *
 */
public abstract class CodeBuilder {
	private StringBuilder body = new StringBuilder();

	private ClassTemplate klass;

	private Map<String, Map<List<Class<?>>, String>> methodTemplateVars = new HashMap();

	private Map<Method, String> methodVars = new HashMap();

	private int varCounter;

	protected CodeBuilder(ClassTemplate klass) {
		super();
		this.klass = klass;
	}

	public CodeBuilder assign(String var) {
		body.append(var).append(" = ");
		return this;
	}

	public CodeBuilder castObject(Class<?> type) {
		body.append("(");
		if (type.isPrimitive()) {
			body.append(getPrimitiveWrapper(type)).append(")");
		} else {
			body.append(getJavaClassCodeNameOf(type)).append(")");
		}
		return this;
	}

	public CodeBuilder valueOf(Class<?> type) {
		if (type.isPrimitive()) {
			body.append(getPrimitiveWrapper(type)).append(".valueOf");
		} else {
			body.append("(");
			body.append(getJavaClassCodeNameOf(type)).append(")");
		}
		return this;
	}

	public CodeBuilder cast(Class<?> type) {
		body.append("(");
		body.append(getJavaClassCodeNameOf(type)).append(")");
		return this;
	}

	public CodeBuilder code(String str) {
		body.append(str);
		return this;
	}

	public CodeBuilder codeInstanceof(String field, Class<?> type) {
		body.append(field).append(" instanceof ");
		body.append(getJavaClassCodeNameOf(type));
		return this;
	}

	public CodeBuilder codeObject(String field, Class<?> type) {
		if (type.isPrimitive()) {
			body.append(getPrimitiveWrapper(type));
			body.append(".valueOf(").append(field).append(")");
		} else {
			body.append(field);
		}
		return this;
	}

	public CodeBuilder construct(Class<?> javaClass, Object... args) {
		body.append("new ").append(javaClass.getName()).append("(");
		for (int i = 0; i < args.length; i++) {
			if (i > 0) {
				code(",");
			}
			insert(args[i]);
		}
		body.append(")");
		return this;
	}

	public CodeBuilder staticInvoke(Method method, Object... args) {
		code(method.getDeclaringClass().getName());
		code(".").code(method.getName()).code("(");
		for (int i = 0; i < args.length; i++) {
			if (i > 0) {
				code(",");
			}
			insert(args[i]);
		}
		code(")");
		return this;
	}

	public CodeBuilder declareObject(Class<?> type, String var) {
		code(getJavaClassCodeNameOf(type));
		return code(" ").assign(var);
	}

	public CodeBuilder declareWrapper(Class<?> type, String var) {
		if (type.isPrimitive()) {
			code(getPrimitiveWrapper(type));
		} else {
			code(getJavaClassCodeNameOf(type));
		}
		return code(" ").assign(var);
	}

	public abstract CodeBuilder end();

	public CodeBuilder insert(boolean b) {
		body.append(b);
		return this;
	}

	public CodeBuilder insert(char c) {
		body.append("'").append(c).append("'");
		return this;
	}

	public CodeBuilder insert(Class<?> javaClass) {
		return insert(klass.get(javaClass));
	}

	public CodeBuilder insertObjectClass(String className) {
		body.append(ClassFactory.class.getName());
		body.append(".classForName(\"");
		body.append(className);
		body.append("\", ").append(Class.class.getName());
		body.append(".forName(\"").append(klass.getName()).append("\")");
		body.append(".getClassLoader())");
		return this;
	}

	public CodeBuilder insert(double d) {
		body.append(d);
		return this;
	}

	public CodeBuilder insert(float f) {
		body.append(f);
		return this;
	}

	public CodeBuilder insert(int i) {
		body.append(i);
		return this;
	}

	public CodeBuilder insert(long lng) {
		body.append(lng);
		return this;
	}

	public CodeBuilder insert(Method method) {
		Class<?> declaringClass = method.getDeclaringClass();
		String name = method.getName();
		Class<?>[] params = method.getParameterTypes();
		CodeBuilder cb = klass.getCodeBuilder();
		String var = cb.methodVars.get(method);
		if (var == null) {
			var = cb.getVarName("Method");
		} else {
			body.append(var);
			return this;
		}
		String before = toString();
		clear();
		String parameterTypes = declareVar(params, cb);
		CodeBuilder field = klass.assignStaticField(Method.class, var);
		field.insert(declaringClass);
		field.code(".getDeclaredMethod(").insert(name);
		field.code(", ").code(parameterTypes).code(")").end();
		cb.methodVars.put(method, var);
		code(before);
		body.append(var);
		return this;
	}

	public CodeBuilder insert(Object o) {
		if (o == null) {
			body.append("null");
		} else {
			visit(o, o.getClass());
		}
		return this;
	}

	public CodeBuilder insert(String str) {
		if (str == null) {
			body.append("null");
		} else {
			body.append("\"").append(str).append("\"");
		}
		return this;
	}

	public CodeBuilder insert(Class<?>[] params) {
		CodeBuilder cb = klass.getCodeBuilder();
		String parameterTypes = declareVar(params, cb);
		String var = cb.getVarName("Classes");
		CodeBuilder field = klass.assignStaticField(params.getClass(), var);
		field.code(parameterTypes).end();
		body.append(var);
		return this;
	}

	public CodeBuilder insertMethod(String name, Class<?>[] params) {
		CtClass cc = klass.getCtClass();
		String className = Descriptor.toJavaName(Descriptor.toJvmName(cc));
		List<Class<?>> list = Arrays.asList(params);
		CodeBuilder cb = klass.getCodeBuilder();
		Map<List<Class<?>>, String> map = cb.methodTemplateVars.get(name);
		if (map == null) {
			cb.methodTemplateVars.put(name, map = new HashMap());
		} else {
			if (map.containsKey(list)) {
				body.append(map.get(list));
				return this;
			}
		}
		String parameterTypes = declareVar(params, cb);
		String var = cb.getVarName("Method");
		CodeBuilder field = klass.assignStaticField(Method.class, var);
		field.insertObjectClass(className);
		field.code(".getDeclaredMethod(").insert(name);
		field.code(", ").code(parameterTypes).code(")").end();
		map.put(list, var);
		body.append(var);
		return this;
	}

	public int length() {
		return body.length();
	}

	public CodeBuilder semi() {
		body.append(";\n");
		return this;
	}

	@Override
	public String toString() {
		return body.toString();
	}

	protected void clear() {
		body.delete(0, length());
	}

	protected ClassMemberValue createClassMemberValue(Class<?> type, ConstPool cp) {
		int idx = cp.addUtf8Info(descriptor(type));
		return new ClassMemberValue(idx, cp);
	}

	public static String descriptor(Class<?> type) {
		if (type.isArray())
			return "[" + descriptor(type.getComponentType());
		if (Void.TYPE.equals(type))
			return "V";
		if (Integer.TYPE.equals(type))
			return "I";
		if (Byte.TYPE.equals(type))
			return "B";
		if (Long.TYPE.equals(type))
			return "J";
		if (Double.TYPE.equals(type))
			return "D";
		if (Float.TYPE.equals(type))
			return "F";
		if (Character.TYPE.equals(type))
			return "C";
		if (Short.TYPE.equals(type))
			return "S";
		if (Boolean.TYPE.equals(type))
			return "Z";
		return "L" + type.getName().replace('.', '/') + ";";
	}

	private String declareVar(Class<?>[] classes, CodeBuilder cb) {
		String var = cb.getVarName("Classes");
		cb.code("java.lang.Class[] ").code(var);
		cb.code(" = ").code("new java.lang.Class[");
		cb.insert(classes.length).code("]").code(";\n");
		for (int i = 0; i < classes.length; i++) {
			cb.code(var).code("[").insert(i).code("]");
			cb.code(" = ");
			cb.insert(classes[i]);
			cb.code(";\n");
		}
		return var;
	}

	private String getJavaClassCodeNameOf(Class<?> type) {
		return klass.get(type).getName();
	}

	private Class<?> getPrimitiveJavaClassWrapper(CtClass cc) {
		if (cc.equals(CtClass.booleanType))
			return Boolean.class;
		if (cc.equals(CtClass.byteType))
			return Byte.class;
		if (cc.equals(CtClass.charType))
			return Character.class;
		if (cc.equals(CtClass.doubleType))
			return Double.class;
		if (cc.equals(CtClass.floatType))
			return Float.class;
		if (cc.equals(CtClass.intType))
			return Integer.class;
		if (cc.equals(CtClass.longType))
			return Long.class;
		if (cc.equals(CtClass.shortType))
			return Short.class;
		throw new AssertionError();
	}

	private String getPrimitiveWrapper(Class<?> type) {
		String wrap;
		if (boolean.class.equals(type)) {
			wrap = Boolean.class.getName();
		} else if (char.class.equals(type)) {
			wrap = Character.class.getName();
		} else if (int.class.equals(type)) {
			wrap = Integer.class.getName();
		} else {
			String prim = type.getName();
			wrap = Character.toUpperCase(prim.charAt(0)) + prim.substring(1);

		}
		return wrap;
	}

	private String getVarName(String type) {
		return "_$" + type + varCounter++;
	}

	private CodeBuilder insert(CtClass cc) {
		if (cc.isPrimitive()) {
			body.append(getPrimitiveJavaClassWrapper(cc).getName())
					.append(".TYPE");
			return this;
		}
		return insertObjectClass(Descriptor.toJavaName(Descriptor.toJvmName(cc)));
	}

	private boolean visit(Object o, Class oc) {
		try {
			Class c = getClass();
			Class[] args = new Class[] { oc };
			Method m = c.getMethod("insert", args);
			m.invoke(this, o);
			return true;
		} catch (NoSuchMethodException e) {
			Class sc = oc.getSuperclass();
			if (sc != null && !Object.class.equals(sc)) {
				if (visit(o, sc))
					return true;
			}
			for (Class face : oc.getInterfaces()) {
				if (visit(o, face))
					return true;
			}
			return false;
		} catch (IllegalArgumentException e) {
			throw new ObjectCompositionException(e);
		} catch (IllegalAccessException e) {
			throw new ObjectCompositionException(e);
		} catch (InvocationTargetException e) {
			throw new ObjectCompositionException(e);
		}
	}
}
