package com.github.anno4j.rdf_generation.validation;

import java.io.File;
import java.io.IOException;
import java.net.URI;

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
public class RoundTrip_test1 {

	public static void main(String[] args)
			throws RepositoryConfigException, RepositoryException, JavaFileGenerationException, IOException {
		OntGenerationConfig config = new OntGenerationConfig();
		config.setBasePackage("com.example.model");
		String filePath = new File("").getAbsolutePath();
		String path2 = filePath + "/src/test/resources/result.txt";
		File path = new File(path2);

		JavaFileGenerator generator = new OWLJavaFileGenerator();
		URI uri = path.toURI();
		generator.addRDF(uri.toString(), "RDF/XML");
		//change the following path
		File outputDir = new File("/Users/Brinninger Sandra/git/anno4j/anno4j-core/src/test/java/com/github/anno4j/rdf_generation/validation/model/");
		generator.generateJavaFiles(config, outputDir);
	}

}
