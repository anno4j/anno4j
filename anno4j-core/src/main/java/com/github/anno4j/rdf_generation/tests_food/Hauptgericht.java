package com.github.anno4j.rdf_generation.tests_food;

import java.util.Set;

import org.openrdf.annotations.Iri;

@Iri("http://www.example.de/Hauptgericht")
public interface Hauptgericht {
	
	//-----------
	@Iri("http://www.example.de/hatName")
	String getName();
	
	@Iri("http://www.example.de/hatName")
	void setName(String name);
	
	
	//-----------
	@Iri("http://www.example.de/hatHauptbestandteil")
	Ingredient getMainIngredient();
	
	@Iri("http://www.example.de/hatHauptbestandteil")
	void setMainIngredient();
	
	
	//-----------
	@Iri("http://www.example.de/hatBestandteil")
	Set<Ingredient> getIngredients();
	
	@Iri("http://www.example.de/hatBestandteil")
	void setIngredients(Set<Ingredient> ingredients);
	
	@Iri("http://www.example.de/hatBestandteil")
	void addIngredient(Ingredient ingredient);
	
	
	//-----------
	@Iri("http://www.example.de/hatKoch")
	Koch getChef();
	
	@Iri("http://www.example.de/hatKoch")
	void setChef(Koch chef);

}
