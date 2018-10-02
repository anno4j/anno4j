package com.github.anno4j.rdf_generation.examples;

import org.openrdf.annotations.Iri;

@Iri("http://example.de/Pet")
public interface Pet {

	@Iri("http://example.de/pet")
	void setPet();
}
