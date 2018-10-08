package com.github.anno4j.rdf_generation.tests;

import org.openrdf.annotations.Iri;

@Iri("http://example.de/Pet")
public interface Pet {

	@Iri("http://example.de/Pet")
	Player getPerson();
}
