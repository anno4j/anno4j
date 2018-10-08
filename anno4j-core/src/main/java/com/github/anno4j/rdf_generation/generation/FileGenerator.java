package com.github.anno4j.rdf_generation.generation;

import java.io.IOException;

public interface FileGenerator {

	/**
	 * Generates a RDFS file.
	 * 
	 * @throws IOException
	 */
    void generate() throws IOException;
    
}
