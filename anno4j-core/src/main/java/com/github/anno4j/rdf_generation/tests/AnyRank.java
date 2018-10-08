package com.github.anno4j.rdf_generation.tests;

import org.openrdf.annotations.Iri;

@Iri("http://example.de/allRanks")
public interface AnyRank {
	
	@Iri("http://example.de/rank")
	Rank getRandomRank();

}
