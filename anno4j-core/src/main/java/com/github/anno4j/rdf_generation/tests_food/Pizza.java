package com.github.anno4j.rdf_generation.tests_food;

import org.openrdf.annotations.Iri;

import com.github.anno4j.model.impl.ResourceObject;

@Iri("http://www.example.de/Pizza")
public interface Pizza extends Hauptgericht {

	//-----------
	@Iri("http://www.example.de/hatSortenname") 
	@Override
	String getName();

	@Iri("http://www.example.de/hatSortenname")
	@Override
	void setName(String name);
	
}
