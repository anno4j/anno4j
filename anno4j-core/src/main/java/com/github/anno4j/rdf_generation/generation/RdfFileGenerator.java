package com.github.anno4j.rdf_generation.generation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.xml.sax.SAXException;

import com.github.anno4j.rdf_generation.configuration.Configuration;
import com.github.anno4j.rdf_generation.building.Extractor;
import com.google.common.reflect.ClassPath;
import com.hp.hpl.jena.graph.Triple.Field;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class RdfFileGenerator implements FileGenerator {

	/**
	 * The content of the RDFS file which will be stored represented as a string.
	 */
	private String content;

	/**
	 * The configuration settings for generating a RDFS file.
	 */
	private Configuration config;

	/**
	 * The path where the package or class to convert is stored. Needed to analyse
	 * if more than one class should be converted.
	 */
	private String packages;

	/**
	 * True, if the user wants to convert more than one class to a RDFS file, false
	 * otherwise.
	 */
	private boolean isPackage;

	/**
	 * The list where all classes which will be converted are stored.
	 */
	private List<Class<?>> allclasses = new ArrayList<>();

	/**
	 * The constructor of the FileGenerator.
	 * 
	 * @param config   The configurations needed for the convertion.
	 * @param packages The path to the converted package or class, needed for
	 *                 analysis.
	 */
	public RdfFileGenerator(Configuration config, String packages) {
		content = "";
		this.config = config;
		this.packages = packages;
	}

	/**
	 * All classes to be converted are being stored in the allclasses list. If the
	 * boolean "bundled" is false, every class contained in the list will be
	 * converted separately. If true, one bundled file which contains all classes
	 * contained in the list will be generated.
	 */
	@Override
	public void generate() throws IOException {
		allclasses = loadAllClasses();
		if (!isPackage) {
			for (Class<?> clazz : allclasses) {
				generateFile(clazz);

			}
		} else {
			generateBundledFile(allclasses);
		}
	}

	/**
	 * Loads all classes from the path of a package or class. If the name of the
	 * package doesn't end with a '.' and matches the name of a class perfectly,
	 * only this one class will be stored in the list. If the name of the package
	 * ends with a '.', it is assumed that the user wants to convert all classes
	 * contained in the given package and all of them are stored in the list.
	 * 
	 * @return The list of all classes which should be converted to a RDFS file.
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
				if (allclasses.size() < 2) {
					setPackage(false);
				}
				return allclasses; // lädt nur eine klasse, da packagestruktur nicht mit punkt endete und ein pfad
									// perfekt mit der eingabe übereinstimmt
			} else if (info.getName().startsWith(packages) && packages.endsWith(".")) {
				final Class<?> clazz = info.load();
				allclasses.add(clazz); // lädt alle klassen in dem package
			}
		}
		if (allclasses.size() > 1) {
			setPackage(true);
		}
		return allclasses;
	}

	/**
	 * Generates one output file from contaning all classes stored in allclasses, if
	 * necessary converts the file into the required serialization and stores the
	 * RDFS file.
	 * 
	 * @param allclasses The list where all classes to be converted are stored.
	 * @throws IOException
	 */
	private void generateBundledFile(List<Class<?>> allclasses) throws IOException {
		content = Extractor.extractMany(allclasses, packages);
		if (content != null) {
			serialCheckAndWrite(content);
		}
	}

	/**
	 * Generates one output file for the given class, if necessary converts the file
	 * into the required serialization and stores the generated RDFS file.
	 * 
	 * @param clazz The class which will be converted.
	 * @throws IOException
	 */
	private void generateFile(Class<?> clazz) throws IOException { // generiert eine
		content = Extractor.extractOne(clazz, packages);
		if (content != null) {
			serialCheckAndWrite(content);
		}
	}

	/**
	 * Checks if the generated RDFS file is already in the correct serialization. If
	 * not it is converted into the correct serialization. Afterwards the file is
	 * being stored.
	 * 
	 * @param content The already converted RDFS file in "RDF/XML".
	 * @throws IOException
	 */
	private void serialCheckAndWrite(String content) throws IOException {
		if (config.getSerialization() == "RDF/XML") {
			writeFile(content, config.getOutputPath());
		} else if (config.getSerialization() == "TURTLE") {
			writeFile(content, config.getOutputPath());
			convert(config.getSerialization());
		} else if (config.getSerialization() == "N-TRIPLE") {
			writeFile(content, config.getOutputPath());
			convert(config.getSerialization());
		} else {
			System.out.println("WRONG SERIALIZATION");
		}

	}

	private void convert(String serialization) throws FileNotFoundException {
		OntModel m = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		m.read(new FileInputStream(new File(config.getOutputPath())), "RDF/XML");
		m.write(new FileOutputStream(new File(config.getOutputPath())), serialization);
	}

	/**
	 * Stores the generated RDFS file.
	 * 
	 * @param content The converted RDFS file.
	 * @param path    The path where to store the file.
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
		
		File file = new File("C:\\Users\\Brinninger Sandra\\Documents\\result.txt");
		
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema;
		try {
			schema = sf.newSchema(file);
			Validator validator = schema.newValidator();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Checks if the user wants to convert more than one class or not.
	 * 
	 * @return if the user wants to convert more than one class or not.
	 */
	public boolean isPackage() {
		return isPackage;
	}

	/**
	 * Sets the boolean if the user wants to convert more than one class or not.
	 * 
	 * @param isPackage If the user wants to convert more than one class or not.
	 */
	public void setPackage(boolean isPackage) {
		this.isPackage = isPackage;
	}

}
