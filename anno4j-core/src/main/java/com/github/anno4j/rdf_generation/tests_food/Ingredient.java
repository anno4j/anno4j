package com.github.anno4j.rdf_generation.tests_food;

import org.openrdf.annotations.Iri;

@Iri("http://www.example.de/Zutat")
public interface Ingredient {

	// --------
	@Iri("http://www.example.de/hatHerkunftsland")
	String getCountryOfOrigin();
	
	@Iri("http://www.example.de/hatHerkunftsland")
	void setCountryOfOrigin(String country);
}
