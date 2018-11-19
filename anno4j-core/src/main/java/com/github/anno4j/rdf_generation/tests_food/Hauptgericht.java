package com.github.anno4j.rdf_generation.tests_food;

import java.util.Set;

import org.openrdf.annotations.Iri;

@Iri("http://www.example.de/Hauptgericht")
public interface Hauptgericht {
	
	@Iri("http://www.example.de/hatName")
	void setName(String name);
	
	@Iri("http://www.example.de/hatName")
	String getName();
	
	@Iri("http://www.example.de/istLeckerMit")
	Hauptgericht getSuitableDish();
	
	@Iri("http://www.example.de/Menu")
	void setMenu(Set<Hauptgericht> menu);
	
	@Iri("http://www.example.de/Menu")
	Set<Hauptgericht> getMenu(Set<Hauptgericht> menu);
	
	@Iri("http://www.example.de/Menu")
	void upgradeMenu(Hauptgericht dish);
	
	@Iri("http://www.example.de/hatKoch")
	Koch getChef();
	
	@Iri("http://www.example.de/hatKoch")
	void setChef(Koch chef);

}
