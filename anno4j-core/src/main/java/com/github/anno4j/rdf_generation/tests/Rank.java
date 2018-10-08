package com.github.anno4j.rdf_generation.tests;

import org.openrdf.annotations.Iri;

@Iri("http://example.de/rank")
public interface Rank extends AnyRank {
	
	@Iri("http://example.de/rank")
	void changeRank();

}
