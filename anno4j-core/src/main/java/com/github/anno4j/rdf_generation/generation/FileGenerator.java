package com.github.anno4j.rdf_generation.generation;

import java.io.IOException;

import com.github.anno4j.rdf_generation.ConvertionException;

public interface FileGenerator {

	/**
	 * Generates a RDFS file.
	 * 
	 * @throws IOException
	 * @throws ConvertionException 
	 * @throws NoSuchMethodException 
	 */
    void generate() throws IOException, ConvertionException, NoSuchMethodException;
    
}
