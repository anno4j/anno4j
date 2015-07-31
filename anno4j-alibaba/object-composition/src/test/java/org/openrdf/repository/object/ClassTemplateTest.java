package org.openrdf.repository.object;

import info.aduna.io.FileUtil;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Set;

import junit.framework.TestCase;

import org.openrdf.repository.object.composition.ClassFactory;
import org.openrdf.repository.object.composition.ClassTemplate;
import org.openrdf.repository.object.composition.MethodBuilder;

public class ClassTemplateTest extends TestCase {

	private File dir;

	public static class MyClass {
		public Set<MyClass> myMethod(Set<MyClass> set) throws Throwable {
			return set;
		}
	}

	public void setUp() throws Exception {
		super.setUp();
		dir = FileUtil.createTempDir("alibaba");
	}

	public void tearDown() throws Exception {
		System.gc();
		FileUtil.deleteDir(dir);
		super.tearDown();
	}

	public void test() throws Exception {
		ClassFactory factory = new ClassFactory(dir);
		ClassTemplate template = factory.createClassTemplate(MyClass.class.getName()+"SubClass");
		Method myMethod = MyClass.class.getMethod("myMethod", Set.class);
		MethodBuilder m = template.copyMethod(myMethod, "myOtherMethod", false);
		m.code("return $1;").end();
		Class<?> c = factory.createClass(template);
		Method myOtherMethod = c.getMethod("myOtherMethod", Set.class);
		String myString = myMethod.toGenericString();
		String myOtherString = myString.replaceAll(".myMethod", "SubClass.myOtherMethod");
		assertEquals(myOtherString, myOtherMethod.toGenericString());
	}
}
