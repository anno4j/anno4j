package com.github.anno4j.rdf_generation;

import java.io.IOException;

import org.openrdf.repository.RepositoryException;

import com.github.anno4j.rdf_generation.generation.FileGenerator;
import com.github.anno4j.rdf_generation.generation.FileGenerator.FileGenerationException;
import com.github.anno4j.rdf_generation.generation.RdfFileGenerator;

public class MainClass {

	public static void main(String[] args) {
		FileGenerator generator = new RdfFileGenerator();
		try {
			generator.generateFile();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
