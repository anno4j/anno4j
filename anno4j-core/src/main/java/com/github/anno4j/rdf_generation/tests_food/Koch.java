package com.github.anno4j.rdf_generation.tests_food;

import java.util.Date;

import org.openrdf.annotations.Iri;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObject;

@Iri("http://www.example.de/Koch")
public interface Koch extends ResourceObject {

	//-----------
	@Iri("http://www.example.de/hatPersonenName")
	String getName();

	@Iri("http://www.example.de/hatPersonenName")
	void setName(String name);

	
	//-----------
	@Iri("http://www.example.de/hatGeburtstag")
	Date getBirthday();

	@Iri("http://www.example.de/hatGeburtstag")
	void setBirthday(Date birth);

	
	//-----------
	@Iri("http://www.example.de/hatAlter")
	Integer getAge();

	@Iri("http://www.example.de/hatAlter")
	void setAge(Integer age);

	
	//-----------
	@Iri("http://www.example.de/hatNationalität")
	String getNationality();

	@Iri("http://www.example.de/hatNationalität")
	void setNationality(String nationality);

	
	//-----------
	@Partial
	boolean isItalian();

}
