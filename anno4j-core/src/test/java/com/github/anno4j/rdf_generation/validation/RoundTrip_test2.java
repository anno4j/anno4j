package com.github.anno4j.rdf_generation.validation;

import java.io.File;
import java.io.IOException;

import com.github.anno4j.rdf_generation.ConvertionException;
import com.github.anno4j.rdf_generation.configuration.Configuration;
import com.github.anno4j.rdf_generation.generation.FileGenerator;
import com.github.anno4j.rdf_generation.generation.RdfFileGenerator;

public class RoundTrip_test2 {

	public static void main(String[] args) throws NoSuchMethodException, IOException, ConvertionException {
	String filePath = new File("").getAbsolutePath();
	String packages = "com.github.anno4j.testfiles.";
			Configuration config = new Configuration(filePath + "/src/main/resources/resultTest2.txt", "RDF/XML",
			packages);
	FileGenerator generator = new RdfFileGenerator(config, packages);
		generator.generate();
	}
	
}
