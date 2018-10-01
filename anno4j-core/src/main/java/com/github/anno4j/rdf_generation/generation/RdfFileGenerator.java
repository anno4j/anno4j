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

	/**
	 * Extracts all classes the user inputted, if not bundled, each class will be
	 * converted separately, if bundled there will be only one output file, no
	 * matter how many input classes.
	 */
	@Override
	public void generate() throws IOException {
		allclasses = loadAllClasses();
		if (!config.isBundled()) {
			for (Class<?> clazz : allclasses) { // nur für !bundled
//				System.out.println("Size of my Classes-List :" + allclasses.size());
				generateFile(clazz); // jede file wird extra generiert.
			}
		} else {
			generateBundledFile(allclasses); // einmalige file als allen klassen
		}
	}

	/**
	 * Loads all classes from the packagesuructure the user typed in
	 * 
	 * @return
	 * @throws IOException
	 */
	private List<Class<?>> loadAllClasses() throws IOException {
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		// Start reader by specifying for example how the name of the package "starts
		// with"
		for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {

			if (info.getName().matches(packages) && !packages.endsWith(".")) {
				final Class<?> clazz = info.load();
				allclasses.add(clazz);
//				System.out.println(clazz.getCanonicalName());
				return allclasses; // lädt nur eine klasse, da packagestruktur nicht mit punkt endete und ein pfad
									// perfekt mit der eingabe übereinstimmt
			} else if (info.getName().startsWith(packages) && packages.endsWith(".")) {
				final Class<?> clazz = info.load();
				allclasses.add(clazz); // lädt alle klassen in dem package
			}

		}
		return allclasses;
	}

	/**
	 * Generates only one file from many input classes and writes the RDFS File
	 * 
	 * @param allclasses
	 * @throws IOException
	 */
	private void generateBundledFile(List<Class<?>> allclasses) throws IOException {
		content = Extractor.extractMany(allclasses);
		serialCheckAndWrite(content);
	}

	/**
	 * Generates a RDFS file from one input class and writes the generated file
	 * 
	 * @param clazz
	 * @throws IOException
	 */
	private void generateFile(Class<?> clazz) throws IOException { // generiert eine
		content = Extractor.extractOne(clazz);
		serialCheckAndWrite(content);
	}

	/**
	 * Wählt die richtige serialisierungsart aus und schreibt das dokument
	 * 
	 * @param content
	 * @throws IOException
	 */
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

	/**
	 * Writes the RDFS-File
	 * 
	 * @param content
	 * @param path
	 * @throws IOException
	 */
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
