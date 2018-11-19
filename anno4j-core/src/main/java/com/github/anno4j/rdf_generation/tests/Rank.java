package com.github.anno4j.rdf_generation.tests;

import org.openrdf.annotations.Iri;

import com.github.anno4j.Anno4j;

@Iri("http://example.de/rank")
public interface Rank extends AnyRank {
	
	@Iri("http://example.de/rank")
	void changeRank();
	
	@Iri("http://example.de/Pet")
	boolean getPet();
	
	@Iri("http://example.de/rank")
	Rank achievedRank();

}
