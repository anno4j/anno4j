package com.github.anno4j.rdf_generation.tests;

import org.openrdf.annotations.Iri;

@Iri("http://example.de/Players")
public interface PlayerInterface {

	@Iri("http://example.de/rank")
	public Integer getRank();
	
}
