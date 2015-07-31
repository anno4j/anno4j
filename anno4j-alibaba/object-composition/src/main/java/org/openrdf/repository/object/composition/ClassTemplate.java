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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.SignatureAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

import org.openrdf.annotations.InstancePrivate;
import org.openrdf.annotations.ParameterTypes;
import org.openrdf.repository.object.exceptions.ObjectCompositionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class builder.
 * 
 * @author James Leigh
 * 
 */
public class ClassTemplate {
	private Logger logger = LoggerFactory.getLogger(ClassTemplate.class);

	private CodeBuilder cb;

	private CtClass cc;

	private ClassFactory cp;

	protected ClassTemplate(final CtClass cc, final ClassFactory cp) {
		this.cc = cc;
		this.cp = cp;
		this.cb = new CodeBuilder(this) {
			@Override
			public CodeBuilder end() {
				try {
					semi();
					cc.makeClassInitializer().insertAfter(toString());
				} catch (CannotCompileException e) {
					throw new ObjectCompositionException(e.getMessage()
							+ " for " + toString(), e);
				}
				clear();
				return this;
			}
		};
	}

	public String getName() {
		return cc.getName();
	}

	public void addConstructor(Class<?>[] types, String string)
			throws ObjectCompositionException {
		try {
			logger.trace("public {}({}) {{}}", new Object[] { getName(), types,
					string });
			CtConstructor con = new CtConstructor(asCtClassArray(types), cc);
			con.setBody("{" + string + "}");
			cc.addConstructor(con);
		} catch (CannotCompileException e) {
			throw new ObjectCompositionException(e);
		} catch (NotFoundException e) {
			throw new ObjectCompositionException(e);
		}
	}

	@Override
	public String toString() {
		return cc.getName();
	}

	public void addInterface(Class<?> face) throws ObjectCompositionException {
		logger.trace("{} implements {}", getName(), face.getName());
		cc.addInterface(get(face));
	}

	public CodeBuilder assignStaticField(final Class<?> type, final String fieldName)
			throws ObjectCompositionException {
		try {
			CtField field = new CtField(get(type), fieldName, cc);
			field.setModifiers(Modifier.PUBLIC | Modifier.STATIC);
			cc.addField(field);
		} catch (CannotCompileException e) {
			throw new ObjectCompositionException(e);
		}
		CodeBuilder code = new CodeBuilder(this) {
			@Override
			public CodeBuilder end() {
				semi();
				String codeString = toString();
				logger.trace("public static {} {} = {}",
						new Object[] { type.getName(), fieldName, codeString });
				return cb.code(codeString).end();
			}
		};
		return code.assign(fieldName);
	}

	public void createField(Class<?> type, String fieldName)
			throws ObjectCompositionException {
		try {
			logger.trace("public {} {};", type.getName(), fieldName);
			CtField field = new CtField(get(type), fieldName, cc);
			field.setModifiers(Modifier.PUBLIC);
			cc.addField(field);
		} catch (CannotCompileException e) {
			throw new ObjectCompositionException(e);
		}
	}

	public MethodBuilder createMethod(Class<?> type, String name,
			Class<?>... parameters) throws ObjectCompositionException {
		CtClass[] exces = new CtClass[] { get(Throwable.class) };
		try {
			CtMethod cm = CtNewMethod.make(get(type), name,
					asCtClassArray(parameters), exces, null, cc);
			return begin(cm, parameters);
		} catch (CannotCompileException e) {
			throw new ObjectCompositionException(e);
		} catch (NotFoundException e) {
			throw new ObjectCompositionException(e);
		}
	}

	public MethodBuilder createPrivateMethod(Class<?> type, String name,
			Class<?>... parameters) throws ObjectCompositionException {
		CtClass[] exces = new CtClass[] { get(Throwable.class) };
		try {
			CtMethod cm = CtNewMethod.make(Modifier.PRIVATE, get(type), name,
					asCtClassArray(parameters), exces, null, cc);
			return begin(cm, parameters);
		} catch (CannotCompileException e) {
			throw new ObjectCompositionException(e);
		} catch (NotFoundException e) {
			throw new ObjectCompositionException(e);
		}
	}

	public void copyAnnotationsFrom(Class<?> c) {
		ClassFile cf = get(c).getClassFile();
		AnnotationsAttribute ai = (AnnotationsAttribute) cf
				.getAttribute(visibleTag);
		if (ai != null && ai.getAnnotations().length > 0) {
			ClassFile info = cc.getClassFile();
			info.addAttribute(ai.copy(info.getConstPool(),
					Collections.EMPTY_MAP));
		}
	}

	public void addAnnotation(Class<?> type, Class<?>... values) {
		ClassFile cf = cc.getClassFile();
		ConstPool cp = cf.getConstPool();
		ClassMemberValue[] elements = new ClassMemberValue[values.length];
		for (int i = 0; i < values.length; i++) {
			elements[i] = cb.createClassMemberValue(values[i], cp);
		}
		ArrayMemberValue value = new ArrayMemberValue(cp);
		value.setValue(elements);
		AnnotationsAttribute ai = (AnnotationsAttribute) cf
				.getAttribute(visibleTag);
		if (ai == null) {
			ai = new AnnotationsAttribute(cp, visibleTag);
			cf.addAttribute(ai);
		}
		try {
			Annotation annotation = new Annotation(cp, get(type));
			annotation.addMemberValue("value", value);
			ai.addAnnotation(annotation);
		} catch (NotFoundException e) {
			throw new AssertionError(e);
		}
	}

	public MethodBuilder copyMethod(Method method, String name, boolean bridge)
			throws ObjectCompositionException {
		try {
			CtClass[] parameters = asCtClassArray(getParameterTypes(method));
			CtClass[] exces = new CtClass[] { get(Throwable.class) };
			CtMethod cm = CtNewMethod.make(get(method.getReturnType()), name,
					parameters, exces, null, cc);
			MethodInfo info = cm.getMethodInfo();
			copyAttributes(method, info);
			if (bridge) {
				info.setAccessFlags(info.getAccessFlags() | AccessFlag.BRIDGE);
			}
			return begin(cm, getParameterTypes(method));
		} catch (CannotCompileException e) {
			throw new ObjectCompositionException(e);
		} catch (NotFoundException e) {
			throw new ObjectCompositionException(e);
		}
	}

	public MethodBuilder createInstancePrivateMethod(Method method)
			throws ObjectCompositionException {
		String name = method.getName();
		Class<?> type = method.getReturnType();
		Class<?>[] parameters = getParameterTypes(method);
		CtClass[] exces = new CtClass[] { get(Throwable.class) };
		try {
			CtMethod cm = CtNewMethod.make(get(type), name,
					asCtClassArray(parameters), exces, null, cc);
			MethodInfo info = cm.getMethodInfo();
			copyAttributes(method, info);
			info.setAccessFlags(info.getAccessFlags() | AccessFlag.BRIDGE);
			ConstPool cp = info.getConstPool();
			AnnotationsAttribute ai = (AnnotationsAttribute) info
					.getAttribute(visibleTag);
			if (ai == null) {
				ai = new AnnotationsAttribute(cp, visibleTag);
				info.addAttribute(ai);
			}
			try {
				ai.addAnnotation(new Annotation(cp, get(InstancePrivate.class)));
			} catch (NotFoundException e) {
				throw new AssertionError(e);
			}
			return begin(cm, parameters);
		} catch (CannotCompileException e) {
			throw new ObjectCompositionException(e);
		} catch (NotFoundException e) {
			throw new ObjectCompositionException(e);
		}
	}

	public CodeBuilder getCodeBuilder() {
		return cb;
	}

	public CtClass getCtClass() {
		return cc;
	}

	public Set<String> getDeclaredFieldNames() {
		CtField[] fields = cc.getDeclaredFields();
		Set<String> result = new HashSet<String>(fields.length);
		for (CtField field : fields) {
			result.add(field.getName());
		}
		return result;
	}

	public Class<?> getSuperclass() {
		try {
			return cp.getJavaClass(cc.getSuperclass());
		} catch (NotFoundException e) {
			throw new ObjectCompositionException(e);
		} catch (ClassNotFoundException e) {
			throw new ObjectCompositionException(e);
		}
	}

	public Class<?>[] getInterfaces() throws ObjectCompositionException {
		try {
			CtClass[] cc1 = cc.getInterfaces();
			Class<?>[] result = new Class<?>[cc1.length];
			for (int i = 0; i < cc1.length; i++) {
				result[i] = cp.getJavaClass(cc1[i]);
			}
			return result;
		} catch (NotFoundException e) {
			throw new ObjectCompositionException(e);
		} catch (ClassNotFoundException e) {
			throw new ObjectCompositionException(e);
		}
	}

	public MethodBuilder overrideMethod(Method method, boolean bridge)
			throws ObjectCompositionException {
		return copyMethod(method, method.getName(), bridge);
	}

	public Set<Field> getFieldsRead(Method method) throws NotFoundException {
		String name = method.getName();
		CtClass[] parameters = asCtClassArray(getParameterTypes(method));
		final Set<CtMethod> methods = new HashSet<CtMethod>();
		final Set<Field> accessed = new HashSet<Field>();
		for (CtMethod cm : cc.getMethods()) {
			if (equals(cm, name, parameters)) {
				findMethodCalls(cm, methods);
			}
		}
		for (CtMethod cm : methods) {
			try {
				cm.instrument(new ExprEditor() {
					@Override
					public void edit(FieldAccess f) {
						try {
							if (f.isReader()) {
								CtField field = f.getField();
								String name = field.getName();
								String dname = field.getDeclaringClass()
										.getName();
								Class<?> declared = cp.loadClass(dname);
								accessed.add(declared.getDeclaredField(name));
							}
						} catch (RuntimeException exc) {
							throw exc;
						} catch (Exception exc) {
							logger.warn(exc.toString(), exc);
						}
					}
				});
			} catch (CannotCompileException e) {
				throw new AssertionError(e);
			}
		}
		return accessed;
	}

	public Set<Field> getFieldsWritten(Method method) throws NotFoundException {
		String name = method.getName();
		CtClass[] parameters = asCtClassArray(getParameterTypes(method));
		final Set<CtMethod> methods = new HashSet<CtMethod>();
		final Set<Field> accessed = new HashSet<Field>();
		for (CtMethod cm : cc.getMethods()) {
			if (equals(cm, name, parameters)) {
				findMethodCalls(cm, methods);
			}
		}
		for (CtMethod cm : methods) {
			try {
				cm.instrument(new ExprEditor() {
					@Override
					public void edit(FieldAccess f) {
						try {
							if (f.isWriter()) {
								CtField field = f.getField();
								String name = field.getName();
								String dname = field.getDeclaringClass()
										.getName();
								Class<?> declared = cp.loadClass(dname);
								accessed.add(declared.getDeclaredField(name));
							}
						} catch (RuntimeException exc) {
							throw exc;
						} catch (Exception exc) {
							logger.warn(exc.toString(), exc);
						}
					}
				});
			} catch (CannotCompileException e) {
				throw new AssertionError(e);
			}
		}
		return accessed;
	}

	CtClass get(Class<?> type) throws ObjectCompositionException {
		return cp.get(type);
	}

	private Set<CtMethod> getAll(Method method) throws NotFoundException {
		Set<CtMethod> result = new HashSet<CtMethod>();
		String name = method.getName();
		CtClass[] parameters = asCtClassArray(getParameterTypes(method));
		for (CtMethod cm : get(method.getDeclaringClass()).getDeclaredMethods()) {
			if (!equals(cm, name, parameters))
				continue;
			result.add(cm);
		}
		return result;
	}

	private Class<?>[] getParameterTypes(Method method) {
		if (method.isAnnotationPresent(ParameterTypes.class))
			return method.getAnnotation(ParameterTypes.class).value();
		return method.getParameterTypes();
	}

	private boolean equals(CtMethod cm, String name, CtClass[] parameters)
			throws NotFoundException {
		if (!cm.getName().equals(name))
			return false;
		if (Arrays.equals(cm.getParameterTypes(), parameters))
			return true;
		if (cm.getParameterTypes().length != 1)
			return false;
		MethodInfo mi = cm.getMethodInfo();
		AnnotationsAttribute ainfo = (AnnotationsAttribute) mi
				.getAttribute(AnnotationsAttribute.invisibleTag);
		if (ainfo == null)
			return false;
		Annotation[] anno = ainfo.getAnnotations();
		if (anno == null)
			return false;
		String typeName = ParameterTypes.class.getName();
		for (int i = 0; i < anno.length; i++) {
			if (anno[i].getTypeName().equals(typeName)) {
				ArrayMemberValue mv = (ArrayMemberValue) anno[i]
						.getMemberValue("value");
				MemberValue[] mvalues = mv.getValue();
				if (mvalues.length != parameters.length)
					return false;
				for (int j = 0; j < mvalues.length; j++) {
					ClassMemberValue cmv = (ClassMemberValue) mvalues[j];
					if (!parameters[j].getName().equals(cmv.getValue()))
						return false;
				}
				return true;
			}
		}
		return false;
	}

	private void findMethodCalls(CtMethod cm, final Set<CtMethod> methods) {
		if (methods.add(cm)) {
			try {
				cm.instrument(new ExprEditor() {
					@Override
					public void edit(MethodCall m) {
						try {
							CtClass enclosing = m.getEnclosingClass();
							String className = m.getClassName();
							if (isAssignableFrom(className, enclosing)) {
								findMethodCalls(m.getMethod(), methods);
							}
						} catch (NotFoundException e) {
							logger.warn(e.toString(), e);
						}
					}

					private boolean isAssignableFrom(String className,
							CtClass enclosing) throws NotFoundException {
						if (enclosing == null)
							return false;
						if (className.equals(enclosing.getName()))
							return true;
						return isAssignableFrom(className, enclosing
								.getSuperclass());
					}
				});
			} catch (CannotCompileException e) {
				throw new AssertionError(e);
			}
		}
	}

	private void copyAttributes(Method method, MethodInfo info)
			throws NotFoundException {
		copyMethodAnnotations(method, info);
		copyParameterAnnotations(method, info);
		copyMethodSignature(method, info);
	}

	private void copyMethodAnnotations(Method method, MethodInfo info)
			throws NotFoundException {
		for (CtMethod e : getAll(method)) {
			MethodInfo em = e.getMethodInfo();
			AnnotationsAttribute ai = (AnnotationsAttribute) em
					.getAttribute(AnnotationsAttribute.visibleTag);
			if (ai == null)
				continue;
			if (ai.getAnnotations().length > 0) {
				info.addAttribute(ai.copy(info.getConstPool(),
						Collections.EMPTY_MAP));
				break;
			}
		}
	}

	private void copyParameterAnnotations(Method method, MethodInfo info)
			throws NotFoundException {
		for (CtMethod e : getAll(method)) {
			MethodInfo em = e.getMethodInfo();
			ParameterAnnotationsAttribute ai = (ParameterAnnotationsAttribute) em
					.getAttribute(ParameterAnnotationsAttribute.visibleTag);
			if (ai == null)
				continue;
			Annotation[][] anns = ai.getAnnotations();
			for (int i = 0, n = anns.length; i < n; i++) {
				if (anns[i].length > 0) {
					info.addAttribute(ai.copy(info.getConstPool(),
							Collections.EMPTY_MAP));
					return;
				}
			}
		}
	}

	private void copyMethodSignature(Method method, MethodInfo info)
			throws NotFoundException {
		for (CtMethod e : getAll(method)) {
			MethodInfo em = e.getMethodInfo();
			SignatureAttribute sa = (SignatureAttribute) em
					.getAttribute(SignatureAttribute.tag);
			if (sa == null)
				continue;
			if (sa.getSignature() != null) {
				info.addAttribute(sa.copy(info.getConstPool(),
						Collections.EMPTY_MAP));
				break;
			}
		}
	}

	private CtClass[] asCtClassArray(Class<?>[] cc) throws NotFoundException {
		CtClass[] result = new CtClass[cc.length];
		for (int i = 0; i < cc.length; i++) {
			result[i] = get(cc[i]);
		}
		return result;
	}

	private MethodBuilder begin(CtMethod cm, Class<?>... parameters) {
		return new MethodBuilder(this, cm);
	}

}
