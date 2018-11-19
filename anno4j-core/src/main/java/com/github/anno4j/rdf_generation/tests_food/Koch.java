package com.github.anno4j.rdf_generation.tests_food;

import java.util.Date;

import org.openrdf.annotations.Iri;

@Iri("http://www.example.de/Koch")
public interface Koch {

	@Iri("http://www.example.de/hatName")
	String getName();
	
	@Iri("http://www.example.de/hatName")
	void setName(String name);
	
	@Iri("http://www.example.de/hatNationalität")
	Nationalität getNationality();
	
	@Iri("http://www.example.de/hatNationalität")
	void setNationality(Nationalität nationality);
	
	@Iri("http://www.example.de/hatGeburtstag")
	Date getBirthday();
	
	@Iri("http://www.example.de/hatAlter")
	Integer getAge();
	
}
