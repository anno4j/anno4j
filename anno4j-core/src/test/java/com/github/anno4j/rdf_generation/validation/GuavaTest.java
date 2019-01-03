package com.github.anno4j.rdf_generation.validation;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.reflect.ClassPath;

/**
 * 
 * This Unittest tests if only a correct java package can be read via Google
 * Guava. Choosing a package or class through a filepath is not correct.
 * 
 * The filepaths for the test need to be changed if you are not working on Windows.
 *
 */
public class GuavaTest {

	boolean found = false;
	String filePath = new File("").getAbsolutePath();
	String path = filePath.replace("\\", "\\\\");

	public String classPass = "com.github.anno4j.rdf_generation.tests_food.Hauptgericht";
	public String classNoPass = "com.github.anno4j.rdf_generation.tests_food.Hauptgerichte";
	public String classpathNoPass = path + "/src/main/java/com/github/anno4j/rdf_generation/tests_food/Hauptgericht";

	public String packagePass = "com.github.anno4j.rdf_generation.tests_food.";
	public String packageNoPass = "com.github.anno4j.rdf_generation.package.";
	public String packagepathNoPass = path + "/src/main/java/com/github/anno4j/rdf_generation/tests_food";

	@Test
	public void testClassPass() throws IOException {
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
			boolean matches = info.getName().matches(classPass);
			if (matches) {
				found = true;
			}
		}
		assertTrue(found);
	}

	@Test
	public void testClassNoPass() throws IOException {
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
			boolean matches = info.getName().matches(classNoPass);
			if (matches) {
				found = true;
			}
		}
		assertFalse(found);
	}

	@Test
	@Ignore
	// change path if your're not working on Windows
	public void testClasspathNoPass() throws IOException {
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
			boolean matches = info.getName().matches(classpathNoPass);
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
			boolean matches = info.getName().startsWith(packagePass);
			if (matches) {
				found = true;
			}
		}
		assertTrue(found);
	}

	@Test
	public void testPackageNoPass() throws IOException {
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
			boolean matches = info.getName().startsWith(packageNoPass);
			if (matches) {
				found = true;
			}
		}
		assertFalse(found);
	}

	@Test
	@Ignore
	// change path if your're not working on Windows
	public void testPackagepathNoPass() throws IOException {
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
			boolean matches = info.getName().startsWith(packagepathNoPass);
			if (matches) {
				found = true;
			}
		}
		assertFalse(found);
	}
}
