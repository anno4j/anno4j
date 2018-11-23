package com.github.anno4j.rdf_generation.tests_food;

import org.openrdf.annotations.Iri;

import com.github.anno4j.model.impl.ResourceObject;

@Iri("http://www.example.de/Zutat")
public interface Zutat extends ResourceObject {

	// --------
	@Iri("http://www.example.de/hatHerkunftsland")
	String getCountryOfOrigin();
	
	@Iri("http://www.example.de/hatHerkunftsland")
	void setCountryOfOrigin(String country);
}
