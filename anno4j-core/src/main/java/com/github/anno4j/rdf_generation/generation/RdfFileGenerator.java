package com.github.anno4j.rdf_generation.generation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import com.github.anno4j.rdf_generation.configuration.Configuration;
import com.github.anno4j.rdf_generation.building.Extractor;
import com.google.common.reflect.ClassPath;

public class RdfFileGenerator implements FileGenerator {

	private String content;

	public RdfFileGenerator() {
		content = "";
	}

	@Override
	public void generateFile(String packages, Configuration config) throws IOException {
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		// Start reader by specifying for example how the name of the package "starts
		// with"
		for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
			if (info.getName().startsWith(packages)) {
					final Class<?> clazz = info.load();
					// do something with your clazz

					System.out.println(clazz.getCanonicalName());
					content = Extractor.extractFrom(clazz);
				} else {
					//...
				}
			}
			if (config.getSerialization() == "RDF/XML") {
				writeFile("", config.getOutputPath()); // delete "", only to avoid NullPointer since generating class
														// doesn't work
			} else if (config.getSerialization() == "TURTLE") {
				// Converter
			} else if (config.getSerialization() == "N3") {
				// Converter
			} else {
				System.out.println("WRONG SERIALIZATION");
			}
		}

	public void writeFile(String content, String path) throws IOException {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		writer.write(content);
		writer.close();
	}
}
