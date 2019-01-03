package com.github.anno4j.rdf_generation.validation;

import java.io.File;
import java.io.IOException;

import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;

import com.github.anno4j.schema_parsing.building.OWLJavaFileGenerator;
import com.github.anno4j.schema_parsing.building.OntGenerationConfig;
import com.github.anno4j.schema_parsing.generation.JavaFileGenerator;
import com.github.anno4j.schema_parsing.generation.JavaFileGenerator.JavaFileGenerationException;

/**
 * 
 * This test checks if roundtrip engineering is working properly. If the input
 * plaintext file is the same as the output plaintext file, then both the java
 * generation and the "Java-to-RDFS"-Convertion produce semantically correct
 * models.
 * 
 * The Filepath needs to be changed if you are not working on Windows.
 *
 */
public class RoundTrip_test {

	public static void main(String[] args)
			throws RepositoryConfigException, RepositoryException, JavaFileGenerationException, IOException {
		OntGenerationConfig config = new OntGenerationConfig();
		config.setBasePackage("com.example.model");
		String filePath = new File("").getAbsolutePath();

		JavaFileGenerator generator = new OWLJavaFileGenerator();
		generator.addRDF(filePath.toLowerCase() + "/src/main/resources/result.txt", "RDF/XML");

		File outputDir = new File(filePath.toLowerCase() + "/src/main/resources/resultRoundTrip.txt");
		generator.generateJavaFiles(config, outputDir);
	}

}
