package com.github.anno4j.rdf_generation.tests_food;

import java.util.Set;

import org.openrdf.annotations.Iri;

@Iri("http://www.example.de/Pizza")
public interface Pizza extends Hauptgericht {

	//-----------
	@Iri("http://www.example.de/hatSortenname") 
	String getName();

	@Iri("http://www.example.de/hatSortenname")
	void setName(String name);
	
}
