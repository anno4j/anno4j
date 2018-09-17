package com.github.anno4j.rdf_generation.generation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;

import com.github.anno4j.rdf_generation.building.Extractor;
import com.github.anno4j.rdf_generation.konverter.Konverter;

public class RdfFileGenerator extends ClassLoader implements FileGenerator {

	private String interfaceAsString;
	private String content;

	public RdfFileGenerator() {
		interfaceAsString = "";
		content = "";
	}

	@Override
	public void generateFile(String path) {
		try (BufferedReader br = new BufferedReader(new FileReader(chooseFile()))) {
			String line;
			while ((line = br.readLine()) != null) {
				interfaceAsString += line + "\r\n";
			}
			
			Class<?> convclass = Konverter.classConvertion(interfaceAsString);

			content =  Extractor.reflect(convclass);
			writeFile(content, path);
			
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
}
