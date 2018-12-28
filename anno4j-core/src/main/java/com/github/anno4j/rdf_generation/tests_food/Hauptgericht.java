package com.github.anno4j.rdf_generation.tests_food;

import java.util.Set;

import org.openrdf.annotations.Iri;

import com.github.anno4j.model.impl.ResourceObject;

@Iri("http://www.example.de/Hauptgericht")
public interface Hauptgericht extends ResourceObject {
	
	//-----------
	@Iri("http://www.example.de/hatName")
	String getName();
	
	@Iri("http://www.example.de/hatName")
	void setName(String name);
	
	
	//-----------
	@Iri("http://www.example.de/hatHauptbestandteil")
	Zutat getMainIngredient();
	
	@Iri("http://www.example.de/hatHauptbestandteil")
	void setMainIngredient();
	
	
	//-----------
	@Iri("http://www.example.de/hatBestandteil")
	Set<Zutat> getIngredients();
	
	@Iri("http://www.example.de/hatBestandteil")
	void setIngredients(Set<Zutat> ingredients);
	
	void addIngredient(Zutat ingredient);
	
	
	//-----------
	@Iri("http://www.example.de/hatKoch")
	Koch getChef();
	
	@Iri("http://www.example.de/hatKoch")
	void setChef(Koch chef);
	
}
