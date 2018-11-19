package com.github.anno4j.rdf_generation.tests_food;

import org.openrdf.annotations.Iri;

@Iri("http://www.example.de/Nationalität")
public interface Nationalität {

	@Iri("http://www.example.de/Nationalität")
	boolean isItalian();
}
