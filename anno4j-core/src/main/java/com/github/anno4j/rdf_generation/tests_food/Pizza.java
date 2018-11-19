package com.github.anno4j.rdf_generation.tests_food;

import org.openrdf.annotations.Iri;

@Iri("http://www.example.de/Pizza")
public interface Pizza extends Hauptgericht {

	@Iri("http://www.example.de/Koch")
	boolean isChefItalian();
	
	@Iri("http://www.example.de/Pizzasorte")
	void setName(String name);
	
	@Iri("http://www.example.de/Pizzasorte")
	String getName();
	
}
