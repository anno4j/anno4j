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
//		config.setBasePackage("com.github.anno4j.rdf_generation.validation.model");
		String filePath = new File("").getAbsolutePath();

		JavaFileGenerator generator = new OWLJavaFileGenerator();
//		generator.addRDF(filePath.toLowerCase() + "/src/main/resources/result.txt", "RDF/XML");
		// Bei deinem Pfad fehlt denke ich das "anno4j-core" vor "src"
		// Finde hier bitte mal raus, was filePath für Dich macht, und was Du weiter an den Pfad hängen musst
		// Folgender Pfad hat bei mir funktioniert (ich habe result.txt ebenfalls in die Resources für die Tests kompiert)
		generator.addRDF("/Users/Manu/IdeaProjects/anno4j/anno4j-core/src/test/resources/result.txt", "RDF/XML");

		// Selbes Spiel für diesen Pfad
		// Wichtig aber vor allem: Der Pfad muss auf das Java-Package zeigen, wo du die erstellten Klassen haben möchtest!
		// Als Hilfestellung, was ich oft mache: Suche den Ordner bei Dir in Windows und suche den Pfad
//		File outputDir = new File(filePath.toLowerCase() + "/src/main/resources/resultRoundTrip.txt");
		File outputDir = new File("/Users/Manu/IdeaProjects/anno4j/anno4j-core/src/test/java/com/github/anno4j/rdf_generation/validation/model/");
		generator.generateJavaFiles(config, outputDir);
	}

}
