package com.github.anno4j.rdf_generation.examples;

import com.google.common.reflect.ClassPath;

import java.io.IOException;

public class Reader {

	public void methGuava() throws IOException {

		final ClassLoader loader = Thread.currentThread().getContextClassLoader();

		// Start reader by specifying for example how the name of the package "starts
		// with"
		for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
			if (info.getName().startsWith("com.github.anno4j.")) {
				final Class<?> clazz = info.load();
				// do something with your clazz

				System.out.println(clazz.getCanonicalName());
			}
		}

	}

	public static void main(String[] args) throws IOException {

	}

}
