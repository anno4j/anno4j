package com.github.anno4j.rdf_generation.generation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JFileChooser;
import com.github.anno4j.rdf_generation.reflection.Extractor;

public class RdfFileGenerator extends ClassLoader implements FileGenerator {

	String interfaceAsString;

	public RdfFileGenerator() {
		interfaceAsString = null;
	}

	@Override
	public void generateFile() {

		try (BufferedReader br = new BufferedReader(new FileReader(chooseFile()))) {
			String line;
			while ((line = br.readLine()) != null) {
				interfaceAsString = line + "\n";
			}

			Class<?> convclass = classConvertion(interfaceAsString);

			System.out.println(convclass);
			Extractor.reflect(convclass);

			// Eigentlich hier nur noch abspeicherung, umleitung der Befehle/Methoden über
			// alle anderen Klassen
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Class<?> classConvertion(String interfaceAsString) {
		return interfaceAsString.getClass();
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
}
