package com.github.anno4j.rdf_generation.tests_food;

import org.openrdf.annotations.Iri;

import com.github.anno4j.model.impl.ResourceObject;

@Iri("http://www.example.de/Pizza")
public interface Pizza extends Hauptgericht, ResourceObject {

	// -----------
	@Iri("http://www.example.de/hatSortenname") // gehört hier noch @override rein, damit ich erkenne kann dass es ein
												// subproperty von etwas ist?
	String getName();

	@Iri("http://www.example.de/hatSortenname")
	void setName(String name); // selbes Getter/Setter-Paar wie in Klasse "Hauptgericht", aber andere URI.
								// getName()/setName() überflüssig, da diese aus "Hauptgericht geerbt werden,
								// oder wichtig, da speziellere URI im Bezug auf "Pizza" wichtiger ist ?!
	
	
	// wenn eine klasse ein extends besitzt, also oben im RDFS-Dokument bereits eine subclassof zu Hauptgericht hat
	// dann in der subklasse (evtl auch mehreren suchen, ob methode den selben namen haben, wenn ja -> subPropertyOf Methodenname
	// evtl auch mehrere
	// wenn nein -> nichts

}
