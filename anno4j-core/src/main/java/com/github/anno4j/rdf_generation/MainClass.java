package com.github.anno4j.rdf_generation;

import java.io.IOException;

import org.openrdf.annotations.Iri;
import org.openrdf.repository.RepositoryException;

import com.github.anno4j.rdf_generation.configuration.Configuration;
import com.github.anno4j.rdf_generation.generation.FileGenerator;
import com.github.anno4j.rdf_generation.generation.FileGenerator.FileGenerationException;
import com.github.anno4j.rdf_generation.generation.RdfFileGenerator;

public class MainClass {

	/**
	 * User needs to add the following:
	 * -packagestructure
	 * -outputfile
	 * -serialisierung
	 * -bundled serialisierung, if a package was chosen
	 * 
	 * @param args
	 * @throws RepositoryException
	 * @throws FileGenerationException
	 * @throws IOException
	 */
	public static void main(String[] args) throws RepositoryException, FileGenerationException, IOException {
		String packages = "com.github.anno4j.rdf_generation.examples.";
		Configuration config = new Configuration("C:\\Users\\Brinninger Sandra\\Documents\\result.txt", "RDF/XML",
				packages, false);
		FileGenerator generator = new RdfFileGenerator(config, packages);
		generator.generate();
	}

}
