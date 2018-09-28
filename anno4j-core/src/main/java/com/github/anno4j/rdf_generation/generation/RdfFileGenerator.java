package com.github.anno4j.rdf_generation.generation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.anno4j.rdf_generation.configuration.Configuration;
import com.github.anno4j.rdf_generation.building.Extractor;
import com.google.common.reflect.ClassPath;

public class RdfFileGenerator implements FileGenerator {

	private String content;
	private Configuration config;
	private String packages;
	private List<Class<?>> allclasses = new ArrayList<>();

	public RdfFileGenerator(Configuration config, String packages) {
		content = "";
		this.config = config;
		this.packages = packages;
	}

	@Override
	public void generate() throws IOException { // generiert alle

		allclasses = loadAllClasses();

		if (!config.isBundled()) {
			for (Class<?> clazz : allclasses) { // nur für !bundled
				System.out.println("Size of my Classes-List :" + allclasses.size());
				generateFile(clazz);
			}
		} else {
			generateBundledFile(allclasses);
			// auch alle klassen in der list aber es gehört ALLES IN 1 DOKUMENT
		}
	}

	private List<Class<?>> loadAllClasses() throws IOException {

		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		// Start reader by specifying for example how the name of the package "starts
		// with"
		for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {

			if (info.getName().matches(packages) && !packages.endsWith(".")) {
				final Class<?> clazz = info.load();
				allclasses.add(clazz);
				System.out.println(clazz.getCanonicalName());
				return allclasses;
			} else if(info.getName().startsWith(packages) && packages.endsWith(".")) {
				final Class<?> clazz = info.load();
				allclasses.add(clazz);
			}

		}
		return allclasses;
	}
	
	private void generateBundledFile(List<Class<?>> allclasses) throws IOException {
		content = Extractor.extractFromList(allclasses);
		serialCheckAndWrite(content);
	}

	private void generateFile(Class<?> clazz) throws IOException { // generiert eine
		content = Extractor.extractFrom(clazz);
		serialCheckAndWrite(content);
	}
	
	private void serialCheckAndWrite(String content) throws IOException {
		if (config.getSerialization() == "RDF/XML") {
			writeFile(content, config.getOutputPath()); // delete "", only to avoid NullPointer since generating class
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
