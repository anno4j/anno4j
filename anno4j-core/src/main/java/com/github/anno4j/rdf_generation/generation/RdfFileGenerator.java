package com.github.anno4j.rdf_generation.generation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;

import com.github.anno4j.rdf_generation.building.Extractor;
import com.github.anno4j.rdf_generation.konverter.Konverter;

public class RdfFileGenerator implements FileGenerator {

	private String interfaceAsString;
	private String content;
	private String serialization;

	public RdfFileGenerator() {
		interfaceAsString = "";
		content = "";
	}

	@Override
	public void generateFile(String path, String serial) {
		// Add serialization, different cases (and converter)
		serialization = serial;
		try (BufferedReader br = new BufferedReader(new FileReader(chooseFile()))) {
			String line;
			while ((line = br.readLine()) != null) {
				interfaceAsString += line + "\r\n";
			}

			Class<?> convclass = Konverter.classConvertion(interfaceAsString);

			content = Extractor.extractFrom(convclass);
			if (getSerial() == "RDF/XML") {
				writeFile("", path); // delete "", only to avoid NullPointer since generating class doesn't work
			} else if (getSerial() == "TURTLE") {
				// Converter
			} else if (getSerial() == "N3") {
				// Converter
			} else {
				System.out.println("WRONG SERIALIZATION");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String chooseFile() {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(null);
		int retrival = chooser.showSaveDialog(null);
		if (retrival == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile().getAbsolutePath();
		} else {
			return null;
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

	private String getSerial() {
		return serialization;
	}
}
