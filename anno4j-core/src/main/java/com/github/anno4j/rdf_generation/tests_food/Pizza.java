package com.github.anno4j.rdf_generation.tests_food;

import org.openrdf.annotations.Iri;

import com.github.anno4j.model.impl.ResourceObject;

@Iri("http://www.example.de/Pizza")
public interface Pizza extends Hauptgericht, ResourceObject {

	// -----------
	@Iri("http://www.example.de/hatSortenname") 
	String getName();

	@Iri("http://www.example.de/hatSortenname")
	void setName(String name); // selbes Getter/Setter-Paar wie in Klasse "Hauptgericht", aber andere URI.
								// getName()/setName() überflüssig, da diese aus "Hauptgericht geerbt werden,
								// oder wichtig, da speziellere URI im Bezug auf "Pizza" wichtiger ist ?!
}
