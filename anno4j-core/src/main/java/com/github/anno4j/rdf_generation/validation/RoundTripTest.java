package com.github.anno4j.rdf_generation.validation;

import java.io.File;
import java.io.IOException;

import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;

import com.github.anno4j.schema_parsing.building.OWLJavaFileGenerator;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.generation.JavaFileGenerator;
import com.github.anno4j.schema_parsing.generation.JavaFileGenerator.JavaFileGenerationException;

public class RoundTripTest {
	public static void main(String[] args)
			throws RepositoryConfigException, RepositoryException, JavaFileGenerationException, IOException {
		OntGenerationConfig config = new OntGenerationConfig();
		config.setBasePackage("com.example.model");

		JavaFileGenerator generator = new OWLJavaFileGenerator();
		generator.addRDF("", "RDF/XML");

		File outputDir = new File("C:\\Users\\Brinninger Sandra\\Documents\\resultround.txt");
		generator.generateJavaFiles(config, outputDir);
	}
}
