package com.github.anno4j.rdf_generation.validation;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import com.google.common.reflect.ClassPath;

public class GuavaTest {

	boolean found = false;
	public String classToPass = "com.github.anno4j.rdf_generation.tests_food.Hauptgericht";
	public String classNotToPass = "com.github.anno4j.rdf_generation.tests_food.Hauptgerichte";
	public String classReallyNotToPass = "C:\\\\Users\\\\Brinninger Sandra\\\\git\\\\anno4j\\\\anno4j-core\\\\src\\\\main\\\\java\\\\com\\\\github\\\\anno4j\\\\rdf_generation\\\\tests_food\\\\Hauptgericht";

	public String packageToPass = "com.github.anno4j.rdf_generation.tests_food.";
	public String packageNotToPass = "com.github.anno4j.rdf_generation.package.";
	public String packageReallyNotToPass = "C:\\\\Users\\\\Brinninger Sandra\\\\git\\\\anno4j\\\\anno4j-core\\\\src\\\\main\\\\java\\\\com\\\\github\\\\anno4j\\\\rdf_generation\\\\tests_food";

	@Test
	public void testClassPass() throws IOException {
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
			boolean matches = info.getName().matches(classToPass);
			if (matches) {
				found = true;
			}
		}
		assertTrue(found);
	}

	@Test
	public void testClassNotPass() throws IOException {
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
			boolean matches = info.getName().matches(classNotToPass);
			if (matches) {
				found = true;
			}
		}
		assertFalse(found);
	}

	@Test
	public void testClassReallyNotPass() throws IOException {
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
			boolean matches = info.getName().matches(classReallyNotToPass);
			if (matches) {
				found = true;
			}
		}
		assertFalse(found);
	}
	
	@Test
	public void testPackagePass() throws IOException {
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
			boolean matches = info.getName().startsWith(packageToPass);
			if (matches) {
				found = true;
			}
		}
		assertTrue(found);
	}

	@Test
	public void testPackageNotPass() throws IOException {
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
			boolean matches = info.getName().startsWith(packageNotToPass);
			if (matches) {
				found = true;
			}
		}
		assertFalse(found);
	}

	@Test
	public void testPackageReallyNotPass() throws IOException {
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
			boolean matches = info.getName().startsWith(packageReallyNotToPass);
			if (matches) {
				found = true;
			}
		}
		assertFalse(found);
	}
}
