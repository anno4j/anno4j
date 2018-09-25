package com.github.anno4j.rdf_generation;

import java.io.IOException;

import org.openrdf.repository.RepositoryException;

import com.github.anno4j.rdf_generation.configuration.Configuration;
import com.github.anno4j.rdf_generation.generation.FileGenerator;
import com.github.anno4j.rdf_generation.generation.FileGenerator.FileGenerationException;
import com.github.anno4j.rdf_generation.generation.RdfFileGenerator;

public class MainClass {

	public static void main(String[] args) throws RepositoryException, FileGenerationException, IOException {
		FileGenerator generator = new RdfFileGenerator();
		Configuration config = new Configuration("C:\\Users\\Brinninger Sandra\\Documents\\result.txt", "RDF/XML", false);
		String packages = "com.github.anno4j.rdf_generation.Player";
		generator.generateFile(packages, config);
	}

}
